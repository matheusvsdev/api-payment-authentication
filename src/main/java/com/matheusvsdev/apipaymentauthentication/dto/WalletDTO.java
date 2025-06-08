package com.matheusvsdev.apipaymentauthentication.dto;

import com.matheusvsdev.apipaymentauthentication.entities.Wallet;
import com.matheusvsdev.apipaymentauthentication.entities.enums.WalletType;

import java.math.BigDecimal;

public class WalletDTO {

    private Long id;
    private WalletType walletType;
    private BigDecimal balance;
    private UserDTO user;

    public WalletDTO() {
    }

    public WalletDTO(Long id, WalletType walletType, BigDecimal balance, UserDTO user) {
        this.id = id;
        this.walletType = walletType;
        this.balance = balance;
        this.user = user;
    }

    public WalletDTO(Wallet entity) {
        id = entity.getId();
        walletType = entity.getWalletType();
        balance = entity.getBalance();
        user = new UserDTO(entity.getUser());
    }

    public Long getId() {
        return id;
    }

    public WalletType getWalletType() {
        return walletType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public UserDTO getUser() {
        return user;
    }
}
