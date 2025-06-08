package com.matheusvsdev.apipaymentauthentication.services;

import com.matheusvsdev.apipaymentauthentication.dto.TransactionDTO;
import com.matheusvsdev.apipaymentauthentication.entities.Transaction;
import com.matheusvsdev.apipaymentauthentication.entities.Wallet;
import com.matheusvsdev.apipaymentauthentication.repositories.TransactionRepository;
import com.matheusvsdev.apipaymentauthentication.repositories.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransactionService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional
    public TransactionDTO createTransaction(TransactionDTO transaction) {

        Wallet sender = walletRepository.findById(transaction.getSenderId())
                .orElseThrow(() -> new RuntimeException("Carteira de origem não encontrada"));

        Wallet receiver = walletRepository.findById(transaction.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Carteira de destino não encontrada"));

        calculate(sender, receiver, transaction.getAmount());

        Transaction newTransaction = new Transaction();
        newTransaction.setSenderId(sender);
        newTransaction.setReceiverId(receiver);
        newTransaction.setAmount(transaction.getAmount());

        newTransaction = transactionRepository.save(newTransaction);

        return new TransactionDTO(newTransaction);
    }

    private void calculate(Wallet sender, Wallet receiver, BigDecimal amount) {
        if (sender.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Saldo insuficiente");
        }
        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        walletRepository.save(sender);
        walletRepository.save(receiver);
    }
}
