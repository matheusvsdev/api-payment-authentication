package com.matheusvsdev.apipaymentauthentication.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Wallet senderId;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private Wallet receiverId;

    private BigDecimal amount;

    private LocalDateTime moment;

    public Transaction() {
    }

    public Transaction(Long id, Wallet senderId, Wallet receiverId, BigDecimal amount, LocalDateTime moment) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.amount = amount;
        this.moment = moment;
    }

    public Long getId() {
        return id;
    }

    public Wallet getSenderId() {
        return senderId;
    }

    public void setSenderId(Wallet senderId) {
        this.senderId = senderId;
    }

    public Wallet getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Wallet receiverId) {
        this.receiverId = receiverId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getMoment() {
        return moment;
    }

    public void setMoment(LocalDateTime moment) {
        this.moment = moment;
    }
}
