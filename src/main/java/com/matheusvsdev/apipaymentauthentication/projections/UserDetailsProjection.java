package com.matheusvsdev.apipaymentauthentication.projections;

/**
 * Interface para projeção de detalhes do usuário
 * Permite obter informações essenciais sem expor a entidade completa
 */
public interface UserDetailsProjection {
    // Retorna o nome de usuário
    // Utilizado para autenticação e identificação
	String getUsername();
	
	// Retorna a senha do usuário
    // A senha pode ser criptografada para maior segurança
	String getPassword();
	
	// Retorna o ID da role associada ao usuário
    // Útil para validar permissões e acessos na aplicação
	Long getRoleId();
	
	// Retorna a autoridade (role) do usuário
    // Exemplo: "ROLE_ADMIN", "ROLE_CLIENT"
	String getAuthority();

}
