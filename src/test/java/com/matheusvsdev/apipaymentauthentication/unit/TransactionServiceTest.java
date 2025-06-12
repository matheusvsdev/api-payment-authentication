package com.matheusvsdev.apipaymentauthentication.unit;

import com.matheusvsdev.apipaymentauthentication.dto.CreateTransactionDTO;
import com.matheusvsdev.apipaymentauthentication.dto.TransactionDTO;
import com.matheusvsdev.apipaymentauthentication.entities.Transaction;
import com.matheusvsdev.apipaymentauthentication.entities.Wallet;
import com.matheusvsdev.apipaymentauthentication.entities.enums.WalletType;
import com.matheusvsdev.apipaymentauthentication.exceptions.InvalidTransactionException;
import com.matheusvsdev.apipaymentauthentication.exceptions.NotFoundException;
import com.matheusvsdev.apipaymentauthentication.factory.TransactionFactory;
import com.matheusvsdev.apipaymentauthentication.factory.UserFactory;
import com.matheusvsdev.apipaymentauthentication.factory.WalletFactory;
import com.matheusvsdev.apipaymentauthentication.repositories.TransactionRepository;
import com.matheusvsdev.apipaymentauthentication.repositories.WalletRepository;
import com.matheusvsdev.apipaymentauthentication.services.TransactionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
public class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    private Wallet senderWallet, receiverWallet;

    @BeforeEach
    void setUp() {

        senderWallet = WalletFactory.createCustomWallet(1L, UserFactory.createClientUser(), WalletType.PERSONAL);
        senderWallet.setBalance(BigDecimal.valueOf(100)); // Define saldo inicial

        receiverWallet = WalletFactory.createCustomWallet(2L, UserFactory.createAdminUser(), WalletType.COMPANY);

        Mockito.when(walletRepository.findAllById(List.of(1L, 2L)))
                .thenReturn(List.of(senderWallet, receiverWallet));

        Mockito.when(transactionRepository.save(Mockito.any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void shouldCreateTransactionSuccessfully() {
        CreateTransactionDTO transactionDTO = TransactionFactory.createTransactionDTO();

        TransactionDTO createdTransaction = transactionService.createTransaction(transactionDTO);

        assertNotNull(createdTransaction);
        assertEquals(BigDecimal.valueOf(100), createdTransaction.getAmount());
        assertEquals(senderWallet.getBalance(), BigDecimal.valueOf(0));
        assertEquals(receiverWallet.getBalance(), BigDecimal.valueOf(100));

        Mockito.verify(walletRepository, Mockito.times(1)).findAllById(Mockito.anyList());
        Mockito.verify(transactionRepository, Mockito.times(1)).save(Mockito.any(Transaction.class));
    }

    @Test
    void shouldFailWhenSenderHasInsufficientBalance() {
        Wallet senderWallet = WalletFactory.createCustomWallet(1L, UserFactory.createClientUser(), WalletType.PERSONAL);
        senderWallet.setBalance(BigDecimal.valueOf(30)); // Simulando saldo insuficiente

        Wallet receiverWallet = WalletFactory.createCustomWallet(2L, UserFactory.createAdminUser(), WalletType.COMPANY);

        // Mockando corretamente a busca no banco
        Mockito.when(walletRepository.findAllById(List.of(1L, 2L)))
                .thenReturn(List.of(senderWallet, receiverWallet)); // Garantindo que ambas as carteiras existem

        CreateTransactionDTO transactionDTO = TransactionFactory.createCustomTransactionDTO(1L, 2L, BigDecimal.valueOf(50));

        Assertions.assertThrows(InvalidTransactionException.class, () -> {
            transactionService.createTransaction(transactionDTO);
        });

        Mockito.verify(walletRepository, Mockito.times(1))
                .findAllById(List.of(senderWallet.getId(), receiverWallet.getId()));

        Mockito.verify(transactionRepository, Mockito.never()).save(Mockito.any(Transaction.class));

        Mockito.verify(walletRepository, Mockito.never()).save(Mockito.any(Wallet.class));
    }

    @Test
    void shouldFailWhenWalletsNotFound() {
        Mockito.when(walletRepository.findAllById(Mockito.anyList()))
                .thenReturn(Collections.emptyList()); // Simulando carteiras inexistentes

        CreateTransactionDTO transactionDTO = TransactionFactory.createCustomTransactionDTO(1L, 2L, BigDecimal.valueOf(50));

        Assertions.assertThrows(NotFoundException.class, () -> {
            transactionService.createTransaction(transactionDTO);
        });

        Mockito.verify(transactionRepository, Mockito.never()).save(Mockito.any(Transaction.class));
        Mockito.verify(walletRepository, Mockito.never()).save(Mockito.any(Wallet.class));
    }

    @Test
    void shouldFailWhenSenderAndReceiverAreSame() {
        Wallet sameWallet = WalletFactory.createPersonalWallet();

        Mockito.when(walletRepository.findAllById(List.of(1L, 1L)))
                .thenReturn(List.of(sameWallet));

        CreateTransactionDTO transactionDTO = TransactionFactory.createCustomTransactionDTO(1L, 1L, BigDecimal.valueOf(50));

        Assertions.assertThrows(InvalidTransactionException.class, () -> {
            transactionService.createTransaction(transactionDTO);
        });

        Mockito.verify(walletRepository, Mockito.times(1)).findAllById(Mockito.anyList());
        Mockito.verify(transactionRepository, Mockito.never()).save(Mockito.any(Transaction.class));
        Mockito.verify(walletRepository, Mockito.never()).save(Mockito.any(Wallet.class));
    }
}
