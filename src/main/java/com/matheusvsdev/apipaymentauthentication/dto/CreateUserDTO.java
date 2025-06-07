package com.matheusvsdev.apipaymentauthentication.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import com.matheusvsdev.apipaymentauthentication.entities.User;

public class CreateUserDTO {

    private String name;
    private String email;
	private String password;
    private List<String> roles = new ArrayList<>();
    
	public CreateUserDTO(String name, String email, String password) {
		this.name = name;
		this.email = email;
		this.password = password;
	}
    
	public CreateUserDTO(User entity) {
		name = entity.getName();
		email = entity.getEmail();
		password = entity.getPassword();
		for (GrantedAuthority role : entity.getAuthorities()) {
			roles.add(role.getAuthority());
		}
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public List<String> getRoles() {
		return roles;
	}
}
