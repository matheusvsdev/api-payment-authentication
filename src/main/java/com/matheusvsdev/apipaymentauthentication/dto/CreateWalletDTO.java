package com.matheusvsdev.apipaymentauthentication.dto;

import com.matheusvsdev.apipaymentauthentication.entities.Wallet;
import com.matheusvsdev.apipaymentauthentication.entities.enums.WalletType;
import jakarta.validation.Valid;

import java.math.BigDecimal;

public class CreateWalletDTO {

	private WalletType walletType;
	private BigDecimal balance;

	@Valid
	private CreateUserDTO user;

	public CreateWalletDTO() {
	}

	public CreateWalletDTO(WalletType walletType, CreateUserDTO user) {
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
