package com.matheusvsdev.apipaymentauthentication.unit;

import com.matheusvsdev.apipaymentauthentication.dto.CreateWalletDTO;
import com.matheusvsdev.apipaymentauthentication.entities.User;
import com.matheusvsdev.apipaymentauthentication.entities.Wallet;
import com.matheusvsdev.apipaymentauthentication.entities.WalletType;
import com.matheusvsdev.apipaymentauthentication.factory.UserFactory;
import com.matheusvsdev.apipaymentauthentication.repository.UserRepository;
import com.matheusvsdev.apipaymentauthentication.repository.WalletRepository;
import com.matheusvsdev.apipaymentauthentication.service.WalletService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
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

    @BeforeEach
    void setUp() throws Exception {

        user = UserFactory.createCustomClientUser(1L, "johndoe@example.com");

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        Mockito.when(walletRepository.save(Mockito.any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    public void testCreateWalletForUser() {

        Wallet walletPersonal = new Wallet();
        walletPersonal.setUser(user);
        walletPersonal.setWalletType(WalletType.PERSONAL);
        walletPersonal.setBalance(BigDecimal.ZERO);

        Wallet walletCompany = new Wallet();
        walletCompany.setUser(user);
        walletCompany.setWalletType(WalletType.PERSONAL);
        walletCompany.setBalance(BigDecimal.ZERO);

        walletService.create(new CreateWalletDTO(walletPersonal));
        walletService.create(new CreateWalletDTO(walletCompany));

        List<Wallet> wallets = Arrays.asList(walletPersonal, walletCompany);
        Mockito.when(walletRepository.findAllByUserId(user.getId())).thenReturn(wallets);

        List<Wallet> userWallets = walletService.findByUserId(user.getId());

        Assertions.assertNotNull(userWallets);
        Assertions.assertEquals(2, userWallets.size());
    }

    @Test
    public void shouldThrowExceptionWhenUserTriesToCreateDuplicateWalletType() {
        Wallet walletPersonal = new Wallet();
        walletPersonal.setUser(user);
        walletPersonal.setWalletType(WalletType.PERSONAL);
        walletPersonal.setBalance(BigDecimal.ZERO);

        Mockito.when(walletRepository.existsByUserIdAndWalletType(user.getId(), WalletType.PERSONAL)).thenReturn(true);

        CreateWalletDTO duplicateWalletDTO = new CreateWalletDTO(walletPersonal);

        Assertions.assertThrows(RuntimeException.class, () -> {
            walletService.create(duplicateWalletDTO);
        });
    }
}
