package com.matheusvsdev.apipaymentauthentication.dto;

import java.util.HashSet;
import java.util.Set;

import com.matheusvsdev.apipaymentauthentication.entities.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CreateUserDTO {

	@NotBlank(message = "Campo requerido")
	@Size(min = 3, message = "Mínimo 3 caracteres")
	@Pattern(regexp = "^[A-Za-z\\s]+$", message = "O nome deve conter apenas letras")
    private String name;

	@NotBlank(message = "Campo requerido")
	@Pattern(regexp = "^[0-9]{11}$", message = "CPF deve ser válido")
	private String cpf;

	@NotBlank(message = "Campo requerido")
	@Pattern(regexp = ".+@.+\\..+", message = "Email deve ter um domínio válido")
    private String email;

	@NotBlank(message = "Campo requerido")
	@Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)[A-Za-z\\d]{8,}$"
			, message = "Senha deve ter pelo menos 8 caracteres, incluindo uma letra maiúscula, uma letra minúscula e um número")
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
