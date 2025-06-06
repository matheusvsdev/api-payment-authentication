package com.matheusvsdev.apipaymentauthentication.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class CustomUserUtil {
	
	/**
     * Obtém o nome de usuário do token JWT armazenado no contexto de segurança.
     * Utiliza `SecurityContextHolder` para acessar a autenticação atual.
     * Extrai a claim `"username"` do JWT para identificar o usuário logado.
     * Retorna o nome de usuário do usuário autenticado.
     */
	public String getLoggedUsername() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Jwt jwtPrincipal = (Jwt) authentication.getPrincipal();
		return jwtPrincipal.getClaim("username");
	}
}
