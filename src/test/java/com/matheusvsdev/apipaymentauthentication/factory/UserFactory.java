package com.matheusvsdev.apipaymentauthentication.factory;

import com.matheusvsdev.apipaymentauthentication.entities.Role;
import com.matheusvsdev.apipaymentauthentication.entities.User;


/**
 * 1.0
 * Factory para criação de objetos `User` usados em testes unitários.
 * Facilita a geração de usuários padrão e personalizados para diversos cenários.
 */
public class UserFactory {
	
	/**
     * Cria um usuário padrão do tipo CLIENT.
     * Retorna um objeto `User` com ID fixo e role CLIENT.
     */
	public static User createClientUser() {
		User user = new User(1L, "John Doe", "johndoe@example.com", "password123");
		user.addRole(new Role(1L, "ROLE_CLIENT"));		
		return user;
	}
	
	/**
     * Cria um usuário padrão do tipo ADMIN.
     * Retorna um objeto `User` com ID fixo e role ADMIN.
     */
	public static User createAdminUser() {
		User admin = new User(2L, "Jane Doe", "janedoe@example.com", "password123");
		admin.addRole(new Role(2L, "ROLE_ADMIN"));		
		return admin;
	}
	
	/**
     * Cria um usuário CLIENT com ID e e-mail customizáveis.
     * Útil para testar diferentes usuários sem criar repetição manual.
     *
     * @param id       ID do usuário
     * @param username E-mail do usuário
     * @return Usuário CLIENT personalizado
     */
	public static User createCustomClientUser(Long id, String username) {
		User user = new User(id, "John Doe", username, "password123");
		user.addRole(new Role(1L, "ROLE_CLIENT"));		
		return user;
	}
	
	/**
     * Cria um usuário ADMIN com ID e e-mail customizáveis.
     * Permite testar diferentes admins e permissões específicas.
     *
     * @param id       ID do usuário
     * @param username E-mail do usuário
     * @return Usuário ADMIN personalizado
     */
	public static User createCustomAdminUser(Long id, String username) {
		User admin = new User(id, "Jane Doe", username, "password123");
		admin.addRole(new Role(2L, "ROLE_ADMIN"));
		return admin;
	}	

}
