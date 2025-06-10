package com.matheusvsdev.apipaymentauthentication.unit;

import com.matheusvsdev.apipaymentauthentication.dto.CreateWalletDTO;
import com.matheusvsdev.apipaymentauthentication.entities.User;
import com.matheusvsdev.apipaymentauthentication.entities.Wallet;
import com.matheusvsdev.apipaymentauthentication.entities.enums.WalletType;
import com.matheusvsdev.apipaymentauthentication.factory.UserFactory;
import com.matheusvsdev.apipaymentauthentication.factory.WalletFactory;
import com.matheusvsdev.apipaymentauthentication.repositories.UserRepository;
import com.matheusvsdev.apipaymentauthentication.repositories.WalletRepository;
import com.matheusvsdev.apipaymentauthentication.services.WalletService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

@ExtendWith(SpringExtension.class)
public class WalletServiceTest {

    @InjectMocks
    private WalletService walletService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletRepository walletRepository;

    private User user;
    private Wallet walletPersonal, walletCompany;

    @BeforeEach
    void setUp() throws Exception {

        user = UserFactory.createCustomClientUser(1L, "johndoe@example.com");
        walletPersonal = WalletFactory.createCustomWallet(1L, WalletType.PERSONAL);
        walletCompany = WalletFactory.createCustomWallet(2L, WalletType.COMPANY);

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
        Mockito.when(walletRepository.save(Mockito.any(Wallet.class))).thenReturn(walletPersonal); // Melhor estabilidade!

    }

    @Test
    public void testCreateWalletForUser() {

        walletService.create(new CreateWalletDTO(walletPersonal));
        walletService.create(new CreateWalletDTO(walletCompany));

        List<Wallet> wallets = Arrays.asList(walletPersonal, walletCompany);
        Mockito.when(walletRepository.findAllByUserId(user.getId())).thenReturn(wallets);

        List<Wallet> userWallets = walletService.findAllByUserId(user.getId());

        Assertions.assertNotNull(userWallets);
        Assertions.assertEquals(2, userWallets.size());
    }

    @Test
    public void shouldThrowExceptionWhenUserTriesToCreateDuplicateWalletType() {

        Mockito.when(walletRepository.existsByUserIdAndWalletType(user.getId(), WalletType.PERSONAL)).thenReturn(true);

        CreateWalletDTO duplicateWalletDTO = new CreateWalletDTO(walletPersonal);

        Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {
            walletService.create(duplicateWalletDTO);
        });

        Assertions.assertEquals("Usuário já possui uma carteira do tipo PERSONAL", exception.getMessage());

    }
}
