package com.matheusvsdev.apipaymentauthentication.unit;

import com.matheusvsdev.apipaymentauthentication.entities.User;
import com.matheusvsdev.apipaymentauthentication.factory.UserFactory;
import com.matheusvsdev.apipaymentauthentication.service.AuthService;
import com.matheusvsdev.apipaymentauthentication.service.UserService;
import com.matheusvsdev.apipaymentauthentication.service.exceptions.ForbiddenException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserService userService;
    
    private User admin, selfClient, otherClient;

    @BeforeEach
    void setUp() {
    	admin = UserFactory.createAdminUser();
		selfClient = UserFactory.createCustomClientUser(1L, "John Doe");
		otherClient = UserFactory.createCustomClientUser(2L, "Jane Doe");
    }

    @Test
	public void validateSelfOrAdminShouldDoNothingWhenAdminLogged() {
		
		Mockito.when(userService.authenticated()).thenReturn(admin);
		
		Long userId = admin.getId();
		
		Assertions.assertDoesNotThrow(() -> {
			authService.validateSelfOrAdmin(userId);
		});
	}
	
	@Test
	public void validateSelfOrAdminShouldDoNothingWhenSelfLogged() {
		
		Mockito.when(userService.authenticated()).thenReturn(selfClient);
		
		Long userId = selfClient.getId();
		
		Assertions.assertDoesNotThrow(() -> {
			authService.validateSelfOrAdmin(userId);
		});
	}
	
	@Test
	public void validateSelfOrAdminThrowsForbiddenExceptionWhenClientOtherLogged() {
		
		Mockito.when(userService.authenticated()).thenReturn(selfClient);
		
		Long userId = otherClient.getId();
		
		Assertions.assertThrows(ForbiddenException.class, () -> {
			authService.validateSelfOrAdmin(userId);
		});
	}

}
