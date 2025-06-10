package com.matheusvsdev.apipaymentauthentication.services;

import com.matheusvsdev.apipaymentauthentication.dto.CreateTransactionDTO;
import com.matheusvsdev.apipaymentauthentication.dto.TransactionDTO;
import com.matheusvsdev.apipaymentauthentication.entities.Transaction;
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

@Service
public class TransactionService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional
    public TransactionDTO createTransaction(CreateTransactionDTO transactionDTO) {

        Wallet sender = walletRepository.findById(transactionDTO.getSenderId())
                .orElseThrow(() -> new NotFoundException("Carteira do remetente não encontrada"));

        Wallet receiver = walletRepository.findById(transactionDTO.getReceiverId())
                .orElseThrow(() -> new NotFoundException("Carteira de destino não encontrada"));

        calculate(sender, receiver, transactionDTO.getAmount());

        Transaction newTransaction = new Transaction();
        newTransaction.setSenderId(sender);
        newTransaction.setReceiverId(receiver);
        newTransaction.setAmount(transactionDTO.getAmount());
        newTransaction.setMoment(LocalDateTime.now());

        newTransaction = transactionRepository.save(newTransaction);

        return new TransactionDTO(newTransaction);
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
