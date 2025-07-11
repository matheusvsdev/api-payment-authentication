package com.matheusvsdev.apipaymentauthentication.services;

import com.matheusvsdev.apipaymentauthentication.entities.User;
import com.matheusvsdev.apipaymentauthentication.exceptions.ForbiddenException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserService userService;

    /**
     * Valida se o usuário é ADMIN ou está acessando seus próprios dados.
     * Se for ADMIN, acesso liberado.
     * Se não for ADMIN e tentar acessar outro ID que não seja seu, lança ForbiddenException.
     */
    public void validateSelfOrAdmin(Long userId) {
    	// Obtém o usuário autenticado
		User me = userService.authenticated();
		
		// Se for ADMIN, acesso é liberado
		if (me.hasRole("ADMIN")) {
			return;
		}
		
		// Se não for ADMIN e tentar acessar outro usuário, lança erro
		if(!me.getId().equals(userId)) {
			throw new ForbiddenException("Access denied. Should be self or admin");
		}
	}
}
