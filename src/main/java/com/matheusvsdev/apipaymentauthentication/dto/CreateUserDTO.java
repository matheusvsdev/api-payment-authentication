package com.matheusvsdev.apipaymentauthentication.dto;

import java.util.HashSet;
import java.util.Set;

import com.matheusvsdev.apipaymentauthentication.entities.User;

public class CreateUserDTO {

    private String name;
	private String cpf;
    private String email;
	private String password;
    private Set<RoleDTO> roles = new HashSet<>();

	public CreateUserDTO() {
	}

	public CreateUserDTO(String name, String cpf, String email, String password) {
		this.name = name;
		this.cpf = cpf;
		this.email = email;
		this.password = password;
	}
    
	public CreateUserDTO(User entity) {
		name = entity.getName();
		cpf = entity.getCpf();
		email = entity.getEmail();
		password = entity.getPassword();
		entity.getRoles().forEach(role -> this.roles.add(new RoleDTO(role)));
	}

	public String getName() {
		return name;
	}

	public String getCpf() {
		return cpf;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public Set<RoleDTO> getRoles() {
		return roles;
	}
}
