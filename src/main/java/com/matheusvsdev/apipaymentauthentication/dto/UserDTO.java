package com.matheusvsdev.apipaymentauthentication.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import com.matheusvsdev.apipaymentauthentication.entities.User;


// 1.3
public class UserDTO {

    private Long id;
    private String name;
    private String email;
    private List<String> roles = new ArrayList<>();
    
	public UserDTO(Long id, String name, String email) {
		this.id = id;
		this.name = name;
		this.email = email;
	}
    
	public UserDTO(User entity) {
		id = entity.getId();
		name = entity.getName();
		email = entity.getEmail();
		for (GrantedAuthority role : entity.getAuthorities()) {
			roles.add(role.getAuthority());
		}
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public List<String> getRoles() {
		return roles;
	}
}
