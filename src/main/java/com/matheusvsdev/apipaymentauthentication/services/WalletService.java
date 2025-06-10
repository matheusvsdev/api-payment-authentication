package com.matheusvsdev.apipaymentauthentication.services;

import com.matheusvsdev.apipaymentauthentication.dto.CreateUserDTO;
import com.matheusvsdev.apipaymentauthentication.dto.CreateWalletDTO;
import com.matheusvsdev.apipaymentauthentication.dto.RoleDTO;
import com.matheusvsdev.apipaymentauthentication.dto.WalletDTO;
import com.matheusvsdev.apipaymentauthentication.entities.Role;
import com.matheusvsdev.apipaymentauthentication.entities.User;
import com.matheusvsdev.apipaymentauthentication.entities.Wallet;
import com.matheusvsdev.apipaymentauthentication.repositories.RoleRepository;
import com.matheusvsdev.apipaymentauthentication.repositories.UserRepository;
import com.matheusvsdev.apipaymentauthentication.repositories.WalletRepository;
import com.matheusvsdev.apipaymentauthentication.exceptions.DuplicateWalletException;
import com.matheusvsdev.apipaymentauthentication.exceptions.MaxWalletsLimitException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class WalletService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Transactional
    public WalletDTO create(CreateWalletDTO walletDTO) {

        User existingUser = userRepository.findByCpfWithWallets(walletDTO.getUser().getCpf())
                .orElseGet(() -> createUser(walletDTO.getUser()));

        if(existingUser.getWallets().size() >= 2) {
            throw new MaxWalletsLimitException("Usuário já possui o limite máximo de carteiras vinculadas ao CPF");
        }

        boolean walletExists = existingUser.getWallets().stream()
                .anyMatch(wallet -> wallet.getWalletType().equals(walletDTO.getWalletType()));

        if (walletExists) {
            throw new DuplicateWalletException("Usuário já possui uma carteira do tipo " + walletDTO.getWalletType());
        }

        Wallet newWallet = new Wallet();
        newWallet.setWalletType(walletDTO.getWalletType());
        newWallet.setBalance(BigDecimal.ZERO);
        newWallet.setUser(existingUser);

        newWallet = walletRepository.save(newWallet);

        return new WalletDTO(newWallet);

    }

    @Transactional(readOnly = true)
    public List<Wallet> findAllByUserId(Long userId) {
        return walletRepository.findAllByUserId(userId);
    }

    private User createUser(CreateUserDTO userDTO) {
        User newUser = new User();
        newUser.setName(userDTO.getName());
        newUser.setCpf(userDTO.getCpf());
        newUser.setEmail(userDTO.getEmail());
        newUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        addUserRole(newUser, userDTO);

        newUser = userRepository.save(newUser);

        return newUser;
    }

    private void addUserRole(User user, CreateUserDTO createUserDTO) {
        user.getRoles().clear();
        if (createUserDTO.getRoles() == null || createUserDTO.getRoles().isEmpty()) {
            Role defaultRole = roleRepository.findByAuthority("CLIENT");
            user.getRoles().add(defaultRole);
        } else {
            for (RoleDTO roleDTO : createUserDTO.getRoles()) {
                Role role = roleRepository.findByAuthority(roleDTO.getAuthority());
                if (role != null) {
                    user.getRoles().add(role);
                }
            }
        }
    }
}
