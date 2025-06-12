package com.matheusvsdev.apipaymentauthentication.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.matheusvsdev.apipaymentauthentication.dto.CreateWalletDTO;
import com.matheusvsdev.apipaymentauthentication.dto.WalletDTO;
import com.matheusvsdev.apipaymentauthentication.entities.User;
import com.matheusvsdev.apipaymentauthentication.entities.Wallet;
import com.matheusvsdev.apipaymentauthentication.entities.enums.WalletType;
import com.matheusvsdev.apipaymentauthentication.exceptions.DuplicateWalletException;
import com.matheusvsdev.apipaymentauthentication.exceptions.MaxWalletsLimitException;
import com.matheusvsdev.apipaymentauthentication.factory.UserFactory;
import com.matheusvsdev.apipaymentauthentication.factory.WalletFactory;
import com.matheusvsdev.apipaymentauthentication.repositories.UserRepository;
import com.matheusvsdev.apipaymentauthentication.repositories.WalletRepository;
import com.matheusvsdev.apipaymentauthentication.services.WalletService;

@ExtendWith(SpringExtension.class)
public class WalletServiceTest {
	
	@InjectMocks
    private WalletService walletService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private CreateWalletDTO walletDTO;
    
    @BeforeEach
    void setUp() {

        walletDTO = WalletFactory.createWalletDTO("12345678900", WalletType.PERSONAL);

        User mockUser = new User(1L, "John Doe", "12345678900", "johndoe@email.com", "encodedPassword");

        Mockito.when(userRepository.findByCpfWithWallets(Mockito.anyString()))
                .thenReturn(Optional.of(mockUser));

        Mockito.when(walletRepository.save(Mockito.any(Wallet.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Mockito.when(passwordEncoder.encode(Mockito.any(CharSequence.class)))
                .thenReturn("encodedPassword");
    }
    
    @Test
    void shouldCreateWalletSuccessfully() {
        WalletDTO createdWallet = walletService.create(walletDTO);

        assertNotNull(createdWallet);
        assertEquals(WalletType.PERSONAL, createdWallet.getWalletType());
    }
    
    @Test
    void shouldFailWhenUserAlreadyHasSameWalletType() {
        User user = UserFactory.createClientUser();
        Wallet existingWallet = WalletFactory.createPersonalWallet();
        user.getWallets().add(existingWallet);
        
        Mockito.when(userRepository.findByCpfWithWallets(Mockito.anyString()))
        	.thenReturn(Optional.of(user));

        // Criando o DTO para tentar adicionar uma carteira duplicada
        CreateWalletDTO walletDTO = WalletFactory.createWalletDTO(user.getCpf(), WalletType.PERSONAL);

        // Confirma que a exceção esperada está sendo lançada
        Assertions.assertThrows(DuplicateWalletException.class, () -> {
            walletService.create(walletDTO);
        });

        Mockito.verify(userRepository, Mockito.times(1)).findByCpfWithWallets(Mockito.anyString());
    }
    
    @Test
    void shouldFailWhenUserAlreadyHasMaxWallets() {
        User user = UserFactory.createClientUser();
        Wallet walletPersonal = WalletFactory.createPersonalWallet();
        Wallet walletCompany = WalletFactory.createCompanyWallet();
        user.getWallets().addAll(Arrays.asList(walletPersonal, walletCompany));
        
        Mockito.when(userRepository.findByCpfWithWallets(Mockito.anyString()))
        	.thenReturn(Optional.of(user));

        CreateWalletDTO walletDTO = WalletFactory.createWalletDTO(user.getCpf(), WalletType.COMPANY);

        Assertions.assertThrows(MaxWalletsLimitException.class, () -> {
            walletService.create(walletDTO);
        });

        Mockito.verify(userRepository, Mockito.times(1)).findByCpfWithWallets(Mockito.anyString());
    }
}
