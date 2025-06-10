package com.matheusvsdev.apipaymentauthentication.controllers;

import com.matheusvsdev.apipaymentauthentication.dto.CreateTransactionDTO;
import com.matheusvsdev.apipaymentauthentication.dto.TransactionDTO;
import com.matheusvsdev.apipaymentauthentication.services.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionDTO> createWallet(@Valid @RequestBody CreateTransactionDTO transactionDTO) {
        TransactionDTO newTransaction = transactionService.createTransaction(transactionDTO);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newTransaction.getId())
                .toUri();

        return ResponseEntity.created(uri).body(newTransaction);
    }
}
