package com.matheusvsdev.apipaymentauthentication.entities;

import com.matheusvsdev.apipaymentauthentication.entities.enums.WalletType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tb_wallet")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private WalletType walletType;
    private BigDecimal balance;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "senderId")
    private Set<Transaction> sentTransactions = new HashSet<>();

    @OneToMany(mappedBy = "receiverId")
    private Set<Transaction> receivedTransactions = new HashSet<>();

    public Wallet() {
    }

    public Wallet(Long id, WalletType walletType, BigDecimal balance, User user) {
        this.id = id;
        this.walletType = walletType;
        this.balance = balance;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public WalletType getWalletType() {
        return walletType;
    }

    public void setWalletType(WalletType walletType) {
        this.walletType = walletType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Transaction> getSentTransactions() {
        return sentTransactions;
    }

    public Set<Transaction> getReceivedTransactions() {
        return receivedTransactions;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Wallet wallet = (Wallet) o;
        return Objects.equals(id, wallet.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
