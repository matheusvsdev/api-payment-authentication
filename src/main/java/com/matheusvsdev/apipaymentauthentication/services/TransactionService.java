package com.matheusvsdev.apipaymentauthentication.services;

import com.matheusvsdev.apipaymentauthentication.dto.CreateTransactionDTO;
import com.matheusvsdev.apipaymentauthentication.dto.TransactionDTO;
import com.matheusvsdev.apipaymentauthentication.entities.Transaction;
import com.matheusvsdev.apipaymentauthentication.entities.User;
import com.matheusvsdev.apipaymentauthentication.entities.Wallet;
import com.matheusvsdev.apipaymentauthentication.exceptions.InvalidTransactionException;
import com.matheusvsdev.apipaymentauthentication.exceptions.NotFoundException;
import com.matheusvsdev.apipaymentauthentication.repositories.TransactionRepository;
import com.matheusvsdev.apipaymentauthentication.repositories.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserService userService;

    @Transactional
    public TransactionDTO createTransaction(CreateTransactionDTO transactionDTO) {
        User ownUser = userService.authenticated();

        List<Wallet> wallets = findSenderAndReceiver(ownUser.getId(), transactionDTO.getReceiverId());
        Wallet sender = wallets.get(0);
        Wallet receiver = wallets.get(1);

        calculate(sender, receiver, transactionDTO.getAmount());

        Transaction newTransaction = new Transaction();
        newTransaction.setSenderId(sender);
        newTransaction.setReceiverId(receiver);
        newTransaction.setAmount(transactionDTO.getAmount());
        newTransaction.setMoment(LocalDateTime.now());

        newTransaction = transactionRepository.save(newTransaction);

        return new TransactionDTO(newTransaction);
    }

    private List<Wallet> findSenderAndReceiver(Long senderId, Long receiverId) {
        List<Wallet> wallets = walletRepository.findAllById(List.of(
                senderId,
                receiverId
        ));

        Wallet sender = wallets.stream()
                .filter(wallet -> wallet.getId().equals(senderId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Carteira do remetente não encontrada"));

        Wallet receiver = wallets.stream()
                .filter(wallet -> wallet.getId().equals(receiverId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Carteira do destinatário não encontrada"));

        return List.of(sender, receiver);
    }

    private void calculate(Wallet sender, Wallet receiver, BigDecimal amount) {
        if (sender.getId().equals(receiver.getId())) {
            throw new InvalidTransactionException("O remetente e o destinatário não podem ser iguais");
        }

        if (sender.getBalance().compareTo(amount) < 0) {
            throw new InvalidTransactionException("Saldo insuficiente");
        }
        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        walletRepository.save(sender);
        walletRepository.save(receiver);
    }
}
