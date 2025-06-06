package com.matheusvsdev.apipaymentauthentication.unit;

import com.matheusvsdev.apipaymentauthentication.entities.User;
import com.matheusvsdev.apipaymentauthentication.repository.UserRepository;
import com.matheusvsdev.apipaymentauthentication.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void mustGenerateJwtTokenWhenLoggingInWithCorrectData() {
        User user = new User(1L, "John Doe", "johndoe@example.com", "password123"); // Senha correta

        when(userRepository.findByEmail("johndoe@example.com")).thenReturn(Optional.of(user));

        String token = authService.authenticate("johndoe@example.com", "password123");

        assertNotNull(token);
        assertTrue(token.startsWith("ey"));
    }

}
