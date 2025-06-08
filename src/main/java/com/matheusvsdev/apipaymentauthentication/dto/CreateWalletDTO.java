package com.matheusvsdev.apipaymentauthentication.dto;

import com.matheusvsdev.apipaymentauthentication.entities.Wallet;
import com.matheusvsdev.apipaymentauthentication.entities.enums.WalletType;

import java.math.BigDecimal;

public class CreateWalletDTO {

	private WalletType walletType;
	private BigDecimal balance;
	private CreateUserDTO user;
	
	public CreateWalletDTO(WalletType walletType, BigDecimal balance, CreateUserDTO user) {
		this.walletType = walletType;
		this.balance = BigDecimal.ZERO;
		this.user = user;
	}
	
	public CreateWalletDTO(Wallet entity) {
		walletType = entity.getWalletType();
		balance = entity.getBalance();
		user = new CreateUserDTO(entity.getUser());
	}

	public WalletType getWalletType() {
		return walletType;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public CreateUserDTO getUser() {
		return user;
	}
}
