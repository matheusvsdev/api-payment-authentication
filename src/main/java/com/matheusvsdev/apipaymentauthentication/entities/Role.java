package com.matheusvsdev.apipaymentauthentication.entities;

import jakarta.persistence.*;

import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;

@SuppressWarnings("serial")
@Entity
@Table(name = "tb_role")
public class Role implements GrantedAuthority{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String authority;

    public Role() {
    }


    public Role(Long id, String authority) {
		this.id = id;
		this.authority = authority;
	}


	public Long getId() {
		return id;
	}

	public String getAuthority() {
		return authority;
	}


	public void setAuthority(String authority) {
		this.authority = authority;
	}


	@Override
	public int hashCode() {
		return Objects.hash(authority, id);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Role other = (Role) obj;
		return Objects.equals(authority, other.authority) && Objects.equals(id, other.id);
	}

    
}
