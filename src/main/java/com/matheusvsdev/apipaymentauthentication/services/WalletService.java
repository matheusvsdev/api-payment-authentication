package com.matheusvsdev.apipaymentauthentication.services;

import com.matheusvsdev.apipaymentauthentication.dto.CreateUserDTO;
import com.matheusvsdev.apipaymentauthentication.dto.CreateWalletDTO;
import com.matheusvsdev.apipaymentauthentication.dto.WalletDTO;
import com.matheusvsdev.apipaymentauthentication.entities.User;
import com.matheusvsdev.apipaymentauthentication.entities.Wallet;
import com.matheusvsdev.apipaymentauthentication.repositories.UserRepository;
import com.matheusvsdev.apipaymentauthentication.repositories.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class WalletService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Transactional
    public WalletDTO create(CreateWalletDTO walletDTO) {

        User existingUser = userRepository.findByEmail(walletDTO.getUser().getEmail())
                .orElseGet(() -> createUser(walletDTO.getUser()));

        boolean walletExists = walletRepository.existsByUserIdAndWalletType(existingUser.getId(), walletDTO.getWalletType());

        if(walletExists) {
            throw new RuntimeException("Usuário já possui uma carteira do tipo " + walletDTO.getWalletType());
        }

        Wallet newWallet = new Wallet();
        newWallet.setUser(existingUser);
        newWallet.setWalletType(walletDTO.getWalletType());
        newWallet.setBalance(BigDecimal.ZERO);

        newWallet = walletRepository.save(newWallet);

        return new WalletDTO(newWallet);
    }

    @Transactional(readOnly = true)
    public List<Wallet> findByUserId(Long userId) {
        return walletRepository.findAllByUserId(userId);
    }

    private User createUser(CreateUserDTO userDTO) {
        User newUser = new User();
        newUser.setName(userDTO.getName());
        newUser.setEmail(userDTO.getEmail());
        newUser.setPassword(userDTO.getPassword());

        newUser = userRepository.save(newUser);

        return newUser;
    }
}
