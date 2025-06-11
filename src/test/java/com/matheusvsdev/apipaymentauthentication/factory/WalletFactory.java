package com.matheusvsdev.apipaymentauthentication.factory;

import java.math.BigDecimal;

import com.matheusvsdev.apipaymentauthentication.dto.CreateUserDTO;
import com.matheusvsdev.apipaymentauthentication.dto.CreateWalletDTO;
import com.matheusvsdev.apipaymentauthentication.dto.WalletDTO;
import com.matheusvsdev.apipaymentauthentication.entities.User;
import com.matheusvsdev.apipaymentauthentication.entities.Wallet;
import com.matheusvsdev.apipaymentauthentication.entities.enums.WalletType;

/**
 * Factory para criação de objetos `Wallet` usados em testes unitários.
 * Facilita a geração de carteira padrão e personalizadas para diversos cenários.
 */
public class WalletFactory {
	
	/**
     * Cria um `CreateWalletDTO` simulando uma solicitação de carteira.
     * @param cpf CPF do usuário que criará a carteira
     * @param walletType Tipo da carteira (PERSONAL, BUSINESS, etc.)
     * @return `CreateWalletDTO` pronto para testes
     */
	public static CreateWalletDTO createWalletDTO(String cpf, WalletType walletType) {
		CreateUserDTO userDTO = new CreateUserDTO("John Doe", cpf, "johndoe@example.com", "password");
		return new CreateWalletDTO(walletType, userDTO);
	}
	
	/**
     * Cria um `WalletDTO` baseado em uma carteira existente.
     * Útil para validar o retorno do serviço em testes unitários.
     * @param wallet Carteira existente
     * @return `WalletDTO` pronto para testes
     */
	public static WalletDTO createWalletDTOFromEntity(Wallet wallet) {		
		return new WalletDTO(wallet);
	}
	
	/**
     * Cria uma Wallet associada a um usuário.
     * @param user Usuário dono da carteira
     * @param walletType Tipo da carteira
     * @return `Wallet` pronta para testes
     */
	public static Wallet createWallet(User user, WalletType walletType) {
		return new Wallet(1L, walletType, BigDecimal.ZERO, user);
	}

	public static Wallet createCustomWallet(Long id, User user, WalletType walletType) {
		return new Wallet(id, walletType, BigDecimal.ZERO, user);
	}
}
