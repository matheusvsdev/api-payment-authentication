package com.matheusvsdev.apipaymentauthentication.unit;

import com.matheusvsdev.apipaymentauthentication.entities.User;
import com.matheusvsdev.apipaymentauthentication.factory.UserFactory;
import com.matheusvsdev.apipaymentauthentication.services.AuthService;
import com.matheusvsdev.apipaymentauthentication.services.UserService;
import com.matheusvsdev.apipaymentauthentication.services.exceptions.ForbiddenException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Testes unitários para `AuthService`
 * Verifica lógica de validação de permissões (Admin ou Self)
 */
@ExtendWith(SpringExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserService userService;
    
    private User admin, selfClient, otherClient;

    /**
     * Configura o ambiente de testes antes de cada execução.
     * Simula usuários ADMIN, CLIENT próprio e CLIENT de outra conta.
     */
    @BeforeEach
    void setUp() {
    	admin = UserFactory.createAdminUser();
		selfClient = UserFactory.createCustomClientUser(1L, "John Doe");
		otherClient = UserFactory.createCustomClientUser(2L, "Jane Doe");
    }

    /**
     * Testa se `validateSelfOrAdmin` permite acesso quando ADMIN está logado.
     */
    @Test
	public void validateSelfOrAdminShouldDoNothingWhenAdminLogged() {
		
		Mockito.when(userService.authenticated()).thenReturn(admin);
		
		Long userId = admin.getId();
		
		Assertions.assertDoesNotThrow(() -> {
			authService.validateSelfOrAdmin(userId);
		});
	}
	
    /**
     * Testa se `validateSelfOrAdmin` permite acesso ao próprio usuário logado.
     */
	@Test
	public void validateSelfOrAdminShouldDoNothingWhenSelfLogged() {
		
		Mockito.when(userService.authenticated()).thenReturn(selfClient);
		
		Long userId = selfClient.getId();
		
		Assertions.assertDoesNotThrow(() -> {
			authService.validateSelfOrAdmin(userId);
		});
	}
	
	/**
     * Testa se `validateSelfOrAdmin` bloqueia acesso indevido de outro cliente.
     */

	@Test
	public void validateSelfOrAdminThrowsForbiddenExceptionWhenClientOtherLogged() {
		
		Mockito.when(userService.authenticated()).thenReturn(selfClient);
		
		Long userId = otherClient.getId();
		
		Assertions.assertThrows(ForbiddenException.class, () -> {
			authService.validateSelfOrAdmin(userId);
		});
	}

}
