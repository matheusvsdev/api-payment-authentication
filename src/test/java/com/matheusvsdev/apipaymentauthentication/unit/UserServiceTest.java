package com.matheusvsdev.apipaymentauthentication.unit;

import com.matheusvsdev.apipaymentauthentication.dto.UserDTO;
import com.matheusvsdev.apipaymentauthentication.entities.User;
import com.matheusvsdev.apipaymentauthentication.factory.UserDetailsFactory;
import com.matheusvsdev.apipaymentauthentication.factory.UserFactory;
import com.matheusvsdev.apipaymentauthentication.projections.UserDetailsProjection;
import com.matheusvsdev.apipaymentauthentication.repository.UserRepository;
import com.matheusvsdev.apipaymentauthentication.service.UserService;
import com.matheusvsdev.apipaymentauthentication.utils.CustomUserUtil;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 3.0
 * Testes unitários para `UserService`
 * Verifica lógica de autenticação e busca de usuário
 */
@ExtendWith(SpringExtension.class)
public class UserServiceTest {
	
	@InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;
    
    @Mock
	private CustomUserUtil userUtil;
    
    private String existingUsername, nonExistingUsername;
	private User user;
	private List<UserDetailsProjection> userDetails;

	/**
     * Configura o ambiente de testes antes de cada execução.
     * Simula usuários existentes e inexistentes.
     */
    @BeforeEach
    void setUp() throws Exception {
    	
    	// Define usernames (emails) para simular um usuário existente e um inexistente
    	existingUsername = "johndoe@example.com";
    	nonExistingUsername = "invaliduser@example.com";
    	
    	// Cria um usuário fictício com a role CLIENT usando Factory
    	user = UserFactory.createCustomClientUser(1L, existingUsername);
        
    	// Simula detalhes de usuário com role ADMIN
    	userDetails = UserDetailsFactory.createCustomAdminUser(existingUsername);
    	
    	// Simula retorno do banco ao buscar usuário por email e roles
    	Mockito.when(userRepository.searchUserAndRolesByEmail(existingUsername))
    			.thenReturn(userDetails); // Retorna dados do usuário existente
    	
    	Mockito.when(userRepository.searchUserAndRolesByEmail(nonExistingUsername))
    			.thenReturn(Collections.emptyList()); // Retorna uma lista vazia quando o usuário não existe
    
    	// Simula busca por email no banco para autentição
    	Mockito.when(userRepository.findByEmail(existingUsername))
    			.thenReturn(Optional.of(user)); // Usuário existe
    	
		Mockito.when(userRepository.findByEmail(nonExistingUsername))
				.thenReturn(Optional.empty()); // Usuário não existe
    }

    /**
     * Testa se `loadUserByUsername` retorna detalhes do usuário corretamente
     */
    @Test
	public void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {
		
		UserDetails result = userService.loadUserByUsername(existingUsername);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getUsername(), existingUsername);
	}
	
    /**
     * Testa se `loadUserByUsername` lança erro quando usuário não existe.
     */
	@Test
	public void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExist() {
		
		Assertions.assertThrows(UsernameNotFoundException.class, () -> {
			userService.loadUserByUsername(nonExistingUsername);
		});
	}
	
	/**
     * Testa se `authenticated` retorna usuário corretamente.
     */
	@Test
	public void authenticatedShouldReturnUserWhenUserExists() {
		
		Mockito.when(userUtil.getLoggedUsername()).thenReturn(existingUsername);
		
		User result = userService.authenticated();
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getUsername(), existingUsername);
	}
	
	/**
     * Testa se `authenticated` lança erro quando usuário não existe.
     */
	@Test
	public void authenticatedShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExist() {
		
		Mockito.doThrow(ClassCastException.class).when(userUtil).getLoggedUsername();
		
		Assertions.assertThrows(UsernameNotFoundException.class, () -> {
			userService.authenticated();
		});
	}
	
	/**
     * Testa se `getMe` retorna um `UserDTO` corretamente.
     */
	@Test
	public void getMeShouldReturnUserDTOWhenUserAuthenticated() {
	
		UserService spyUserService = Mockito.spy(userService);
		Mockito.doReturn(user).when(spyUserService).authenticated();
		
		UserDTO result = spyUserService.getMe();
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getEmail(), existingUsername);		
	}
	
	/**
     * Testa se `getMe` lança erro quando usuário não está autenticado.
     */
	@Test
	public void getMeShouldThrowUsernameNotFoundExceptionWhenUserNotAuthenticated() {
		
		UserService spyUserService = Mockito.spy(userService);
		Mockito.doThrow(UsernameNotFoundException.class).when(spyUserService).authenticated();
		
		Assertions.assertThrows(UsernameNotFoundException.class, () -> {
			@SuppressWarnings("unused")
			UserDTO result = spyUserService.getMe();
		});
	}
}
