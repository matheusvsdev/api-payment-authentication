package com.matheusvsdev.apipaymentauthentication.config;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.DelegatingOAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.JwtGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2AccessTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.web.SecurityFilterChain;

import com.matheusvsdev.apipaymentauthentication.config.customgrant.CustomPasswordAuthenticationConverter;
import com.matheusvsdev.apipaymentauthentication.config.customgrant.CustomPasswordAuthenticationProvider;
import com.matheusvsdev.apipaymentauthentication.config.customgrant.CustomUserAuthorities;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;

@Configuration
public class AuthorizationServerConfig {

	/**
	* Variáveis de configuração do cliente OAuth2 (valores definidos no application.properties)
	* Valores definidos em application.properties para configurar ID e Secret do cliente
	*/
	@Value("${security.client-id}")
	private String clientId;

	@Value("${security.client-secret}")
	private String clientSecret;

	// Duração do token JWT em segundos
	@Value("${security.jwt.duration}")
	private Integer jwtDurationSeconds;

	@Autowired
	private UserDetailsService userDetailsService;

	/**
     * Configura o Authorization Server (Spring Security OAuth2)
     * Define os endpoints do OAuth2 Authorization Server, incluindo token e autenticação
     */
	@Bean
	@Order(2) // Ordem de prioridade na configuração do SecurityFilterChain
	public SecurityFilterChain asSecurityFilterChain(HttpSecurity http) throws Exception {
	    OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = OAuth2AuthorizationServerConfigurer.authorizationServer();
	    
	    http.securityMatcher(authorizationServerConfigurer.getEndpointsMatcher()) // Define endpoints OAuth2 como protegidos
	        .authorizeHttpRequests(auth -> auth.anyRequest().authenticated()) // Todos os endpoints requerem autenticação
	        .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())) // Configuração do servidor de recursos OAuth2
	        .with(authorizationServerConfigurer, customizer -> customizer
	            .tokenEndpoint(tokenEndpoint -> tokenEndpoint
	                .accessTokenRequestConverter(new CustomPasswordAuthenticationConverter()) // Conversor personalizado para login com senha
	                .authenticationProvider(new CustomPasswordAuthenticationProvider(
	                    authorizationService(), tokenGenerator(), userDetailsService, passwordEncoder() // Define lógica de autenticação personalizada
	                ))
	            )
	        );

	    return http.build();
	}


	/**
     * Define serviços de autorização em memória
     * OAuth2 Authorization Service → Gerencia a persistência das autorizações de acesso
     * OAuth2 Authorization Consent Service → Controla os consentimentos de acesso do usuário
     */
	@Bean
	public OAuth2AuthorizationService authorizationService() {
		return new InMemoryOAuth2AuthorizationService();
	}

	/**
	 * Controla os **consentimentos do usuário** para acessar determinados escopos OAuth2.
	 * Se um cliente OAuth2 precisa de **permissões adicionais**, esse serviço gerencia esses consentimentos.
	 * Em aplicações maiores, isso normalmente seria persistido em um banco de dados.
	 */
	@Bean
	public OAuth2AuthorizationConsentService oAuth2AuthorizationConsentService() {
		return new InMemoryOAuth2AuthorizationConsentService();
	}

	/**
     * Gerenciamento de senhas usando BCrypt
     * Usa BCrypt para criptografar e validar senhas de usuários
     */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(12); // Define força do algoritmo BCrypt
	}

	/**
	 * Integra-se ao serviço "Have I Been Pwned" para verificar se uma senha já apareceu em vazamentos de segurança.
	 * Caso a senha tenha sido exposta, o sistema pode alertar o usuário ou bloquear sua utilização.
	 * Isso reduz o risco de ataques baseados em credenciais comprometidas.
	 */
	@Bean
	public CompromisedPasswordChecker passwordChecker() {
		return new HaveIBeenPwnedRestApiPasswordChecker();
	}

	/**
	 * Registro de clientes OAuth2
	 * Define clientes permitidos para autenticação e seus escopos
	 */
	@Bean
	public RegisteredClientRepository registeredClientRepository() {
		// @formatter:off
		RegisteredClient registeredClient = RegisteredClient
			.withId(UUID.randomUUID().toString()) // Gera ID aleatório para o cliente
			.clientId(clientId) // Usa clientId definido nas configurações
			.clientSecret(passwordEncoder().encode(clientSecret)) // Criptografa o clientSecret
			.scope("read") // Escopo para leitura de dados
			.scope("write") // Escopo para escrita/modificação de dados
			.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS) // Grant type apropriado para autenticação OAuth2
            .tokenSettings(tokenSettings()) // Configuração dos tokens
			.clientSettings(clientSettings()) // Configuração do cliente OAuth2
			.build();
		// @formatter:on

		return new InMemoryRegisteredClientRepository(registeredClient);
	}

	/**
	 * Configuração de tokens e suas regras
	 * Define formato e tempo de vida dos tokens gerados
	 */
	@Bean
	public TokenSettings tokenSettings() {
		// @formatter:off
		return TokenSettings.builder()
			.accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED) // Define tokens JWT auto-contidos
			.accessTokenTimeToLive(Duration.ofSeconds(jwtDurationSeconds)) // Define duração do token JWT
			.build();
		// @formatter:on
	}

	/**
	 * Define as configurações específicas do **cliente OAuth2**.
	 * Permite ajustar opções como **requisição de consentimento do usuário** antes de liberar permissões.
	 * Pode ser expandido para exigir **autenticação mTLS** em clientes mais seguros.
	 */
	@Bean
	public ClientSettings clientSettings() {
		return ClientSettings.builder().build();
	}

	/**
	 * Configura **parâmetros globais** do Authorization Server.
	 * Gerencia URLs dos endpoints OAuth2 (token, autorização, introspecção, revogação).
	 * Pode ser modificado para **personalizar caminhos das APIs OAuth2**, caso necessário.
	 */
	@Bean
	public AuthorizationServerSettings authorizationServerSettings() {
		return AuthorizationServerSettings.builder().build();
	}

	/**
	 * Configuração de geração e customização de tokens JWT
	 * Define como os tokens JWT são gerados e assinados digitalmente
	 */
	@Bean
	public OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator() {
		NimbusJwtEncoder jwtEncoder = new NimbusJwtEncoder(jwkSource()); // Instancia encoder de JWT usando JWK
		JwtGenerator jwtGenerator = new JwtGenerator(jwtEncoder);
		jwtGenerator.setJwtCustomizer(tokenCustomizer()); // Adiciona customização ao JWT
		OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
		return new DelegatingOAuth2TokenGenerator(jwtGenerator, accessTokenGenerator); // Configura múltiplos geradores de token
	}

	/**
     * Customização do Token JWT
     * Adiciona claims personalizados ao JWT, incluindo autoridades do usuário
     */
	@Bean
	public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
		return context -> {
			OAuth2ClientAuthenticationToken principal = context.getPrincipal();
			CustomUserAuthorities user = (CustomUserAuthorities) principal.getDetails();
			List<String> authorities = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
			if (context.getTokenType().getValue().equals("access_token")) {
				// @formatter:off
				context.getClaims()
					.claim("authorities", authorities) // Adiciona authorities ao token
					.claim("username", user.getUsername()); // Adiciona username ao token
				// @formatter:on
			}
		};
	}

	/**
     * Decodificador JWT
     * Configura como os tokens JWT são decodificados para validação
     */
	@Bean
	public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
		return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
	}

	/**
     * Fonte de Chaves de Assinatura JWT (JWK)
     * Define como os tokens JWT são assinados usando chaves RSA
     */
	@Bean
	public JWKSource<SecurityContext> jwkSource() {
		RSAKey rsaKey = generateRsa();
		JWKSet jwkSet = new JWKSet(rsaKey);
		return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
	}

	/**
	 * Gera uma **chave RSA** usada para assinar tokens JWT.
	 * Utiliza um par de chaves **pública e privada** para garantir segurança na comunicação.
	 * Cria um **UUID** como identificador único da chave.
	 */
	private static RSAKey generateRsa() {
		KeyPair keyPair = generateRsaKey(); // Gera o par de chaves RSA
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

		return new RSAKey.Builder(publicKey)
				.privateKey(privateKey)
				.keyID(UUID.randomUUID().toString()) // Adiciona um identificador único
				.build();
	}

	/**
	 * Cria um novo par de chaves RSA usando **2048 bits**, garantindo alta segurança.
	 * Usa **KeyPairGenerator** para gerar as chaves com base na criptografia RSA.
	 * Em caso de erro, lança uma **IllegalStateException** para garantir estabilidade do sistema.
	 */
	private static KeyPair generateRsaKey() {
		KeyPair keyPair;
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA"); // Configura gerador de chaves RSA
			keyPairGenerator.initialize(2048); // Define tamanho de 2048 bits para segurança elevada
			keyPair = keyPairGenerator.generateKeyPair(); // Gera o par de chaves
		} catch (Exception ex) {
			throw new IllegalStateException(ex); // Em caso de erro, lança uma exceção controlada
		}
		return keyPair;
	}
}
