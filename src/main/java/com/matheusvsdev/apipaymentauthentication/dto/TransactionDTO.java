package com.matheusvsdev.apipaymentauthentication.dto;

import com.matheusvsdev.apipaymentauthentication.entities.Transaction;

import java.math.BigDecimal;

public class TransactionDTO {

    private Long id;
    private Long senderId;
    private Long receiverId;
    private BigDecimal amount;

    public TransactionDTO() {
    }

    public TransactionDTO(Long id, Long senderId, Long receiverId, BigDecimal amount) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.amount = amount;
    }

    public TransactionDTO(Transaction entity) {
        id = entity.getId();
        senderId = entity.getSenderId().getId();
        receiverId = entity.getReceiverId().getId();
        amount = entity.getAmount();
    }

    public Long getId() {
        return id;
    }

    public Long getSenderId() {
        return senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
