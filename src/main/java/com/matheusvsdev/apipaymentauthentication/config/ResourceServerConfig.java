package com.matheusvsdev.apipaymentauthentication.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity // Habilita configuração de segurança no Spring Security
@EnableMethodSecurity // Permite proteção de métodos com @PreAuthorize e @Secured
public class ResourceServerConfig {

	/**
	 * Variável de configuração de CORS
     * Define origens permitidas via application.properties ou .env
	 */
	@Value("${cors.origins}")
	private String corsOrigins;

	/**
     * Configuração de permissões para o banco H2 (somente em ambiente de teste)
     * Permite acesso ao console do H2 sem restrições de segurança
     * Somente ativado quando o perfil `test` estiver ativo
     */
	@Bean
	@Profile("test") // Executado apenas em testes
	@Order(1) // Executado antes das configurações de segurança padrão
	public SecurityFilterChain h2SecurityFilterChain(HttpSecurity http) throws Exception {

		http.securityMatcher(PathRequest.toH2Console()) // Aplica essa segurança apenas ao console do H2
				.csrf(AbstractHttpConfigurer::disable) // Desabilita CSRF para evitar bloqueio de requisições
				.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)); // Permite que o navegador exiba o console H2
		return http.build();
	}

	/**
     * Configuração do Resource Server
     * Protege a API com autenticação via JWT
     * Define regras de autorização para endpoints
     */
	@Bean
	@Order(3) // Executado após a configuração do Authorization Server
	public SecurityFilterChain rsSecurityFilterChain(HttpSecurity http) throws Exception {

		http.csrf(AbstractHttpConfigurer::disable); // Desabilita proteção CSRF (não necessário para APIs REST)
		http.authorizeHttpRequests((authorize) -> authorize.anyRequest().permitAll()); // Permite acesso irrestrito (pode ser refinado depois)
		http.oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer.jwt(Customizer.withDefaults())); // Habilita autenticação JWT para proteger a API
		http.cors(cors -> cors.configurationSource(corsConfigurationSource())); // Aplica configuração CORS corretamente
		return http.build();
	}

	/**
     * Conversão de authorities do token JWT
     * Define como as permissões dos usuários são extraídas do JWT
     */
	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
		grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities"); // Extrai roles do claim "authorities"
		grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_"); // Adiciona prefixo padrão do Spring Security

		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
		return jwtAuthenticationConverter;
	}

	/**
     * Configuração de CORS
     * Permite requisições de múltiplas origens
     * Define regras para permitir requisições de múltiplas origens
     * Controla métodos permitidos e cabeçalhos aceitos
     */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {

		CorsConfiguration corsConfig = new CorsConfiguration();

		// Verifica se a variável corsOrigins está preenchida antes de configurar
		if(!corsOrigins.isBlank()) {
			List<String> allowedOrigins = Arrays.asList( corsOrigins.split(","));
			corsConfig.setAllowedOriginPatterns(allowedOrigins);
		}

		corsConfig.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE", "PATCH")); // Métodos HTTP permitidos
		corsConfig.setAllowCredentials(true); // Permite envio de cookies/sessões cross-origin
		corsConfig.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type")); // Define os headers aceitos

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfig); // Aplica configuração CORS para toda a API
		return source;
	}
}
