package com.matheusvsdev.apipaymentauthentication.factory;

import java.util.ArrayList;
import java.util.List;

import com.matheusvsdev.apipaymentauthentication.projections.UserDetailsProjection;

/**
 * 2.0
 * Factory para criar listas de `UserDetailsProjection` usadas em testes.
 * Permite simular diferentes tipos de usuários sem precisar de banco de dados real.
 */
public class UserDetailsFactory {
	
	/**
     * Cria uma lista de projeções para um usuário CLIENT com e-mail personalizado.
     * Útil para testar casos onde o usuário tem a role CLIENT e precisa ser autenticado.
     *
     * @param username E-mail do usuário
     * @return Lista contendo um único objeto `UserDetailsProjection` simulando um CLIENT
     */
	public static List<UserDetailsProjection> createCustomClientUser(String username) {
		List<UserDetailsProjection> list = new ArrayList<>();
		list.add(new UserDetailsImpl(username, "password123", 1L, "ROLE_CLIENT"));
		return list;
	}
	
	/**
     * Cria uma lista de projeções para um usuário ADMIN com e-mail personalizado.
     * Útil para testar autenticação e permissões específicas de administradores.
     *
     * @param username E-mail do usuário
     * @return Lista contendo um único objeto `UserDetailsProjection` simulando um ADMIN
     */
	public static List<UserDetailsProjection> createCustomAdminUser(String username) {
		List<UserDetailsProjection> list = new ArrayList<>();
		list.add(new UserDetailsImpl(username, "password123", 1L, "ROLE_ADMIN"));
		return list;
	}

}

/**
 * 2.1
 * Implementação da interface `UserDetailsProjection`
 * Usada para armazenar detalhes de usuários fictícios em testes.
 */
class UserDetailsImpl implements UserDetailsProjection {
	private String username;
	private String password;
	private Long roleId;
	private String authority;
	
	public UserDetailsImpl() {}
	
	public UserDetailsImpl(String username, String password, Long roleId, String authority) {
		this.username = username;
		this.password = password;
		this.roleId = roleId;
		this.authority = authority;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public Long getRoleId() {
		return roleId;
	}

	@Override
	public String getAuthority() {
		return authority;
	}
}