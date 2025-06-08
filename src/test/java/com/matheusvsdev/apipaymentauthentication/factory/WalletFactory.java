package com.matheusvsdev.apipaymentauthentication.factory;

import com.matheusvsdev.apipaymentauthentication.entities.Wallet;
import com.matheusvsdev.apipaymentauthentication.entities.enums.WalletType;

import java.math.BigDecimal;

/**
 * Factory para criação de objetos `Wallet` usados em testes unitários.
 * Facilita a geração de wallets padrão e personalizadas para diversos cenários.
 */
public class WalletFactory {
	
	/**
     * Cria uma carteira padrão do tipo PERSONAL.
     * Retorna um objeto `Wallet` com ID fixo e walletType PERSONAL.
     */
	public static Wallet createPersonalWallet() {

        return new Wallet(1L, WalletType.PERSONAL, BigDecimal.ZERO, UserFactory.createClientUser());
	}
	
	/**
     * Cria uma carteira padrão do tipo COMPANY.
     * Retorna um objeto `Wallet` com ID fixo e walletType COMPANY.
     */
	public static Wallet createCompanyWallet() {
		return new Wallet(2L, WalletType.COMPANY, BigDecimal.ZERO, UserFactory.createAdminUser());
	}
	
	/**
     * Cria uma Wallet com ID e walletType customizáveis.
     * Útil para testar diferentes wallets sem criar repetição manual.
     *
     * @param id       ID da carteira
     * @param walletType Tipo da carteira
     * @return Wallet personalizado
     */
	public static Wallet createCustomWallet(Long id, WalletType walletType) {
		return new Wallet(id, walletType, BigDecimal.ZERO, UserFactory.createClientUser());
	}
}
