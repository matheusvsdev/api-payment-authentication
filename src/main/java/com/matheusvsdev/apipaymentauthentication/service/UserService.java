package com.matheusvsdev.apipaymentauthentication.service;
import com.matheusvsdev.apipaymentauthentication.dto.CreateUserDTO;
import com.matheusvsdev.apipaymentauthentication.dto.UserDTO;
import com.matheusvsdev.apipaymentauthentication.entities.Role;
import com.matheusvsdev.apipaymentauthentication.entities.User;
import com.matheusvsdev.apipaymentauthentication.projections.UserDetailsProjection;
import com.matheusvsdev.apipaymentauthentication.repository.UserRepository;
import com.matheusvsdev.apipaymentauthentication.utils.CustomUserUtil;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
	private CustomUserUtil customUserUtil;



    /**
     * Carrega um usuário pelo e-mail e retorna suas credenciais
     * Se o usuário não for encontrado, lança uma `UsernameNotFoundException`
     */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		// Busca usuário e roles associadas no banco de dados
		List<UserDetailsProjection> result = userRepository.searchUserAndRolesByEmail(username);
		
		// Se não encontrar registros, lança exceção
		if (result.size() == 0) {
			throw new UsernameNotFoundException("Email not found");
		}
		
		// Cria objeto `User` com os dados do primeiro registro encontrado
		User user = new User();
		user.setEmail(result.get(0).getUsername());
		user.setPassword(result.get(0).getPassword());
		
		// Adiciona todas as roles associadas ao usuário
		for (UserDetailsProjection projection : result) {
			user.addRole(new Role(projection.getRoleId(), projection.getAuthority()));
		}
		
		return user;
	}
	
	
	/**
     * Obtém o usuário autenticado a partir do token JWT
     * Se não encontrar um usuário válido, lança `UsernameNotFoundException`
     */
	public User authenticated() {
		try {
			String username = customUserUtil.getLoggedUsername();
			return userRepository.findByEmail(username).get();
		}
		catch (Exception e) {
			throw new UsernameNotFoundException("Invalid user");
		}
	}
	
	/**
     * Retorna um objeto `UserDTO` com os dados do usuário autenticado
     * Usa `@Transactional(readOnly = true)` para otimizar a consulta
     */
	@Transactional(readOnly = true)
	public UserDTO getMe() {
		User entity = authenticated();
		return new UserDTO(entity);
	}
}
