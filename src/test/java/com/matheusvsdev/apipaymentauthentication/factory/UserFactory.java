package com.matheusvsdev.apipaymentauthentication.factory;

import com.matheusvsdev.apipaymentauthentication.entities.Role;
import com.matheusvsdev.apipaymentauthentication.entities.User;

public class UserFactory {
	
	public static User createClientUser() {
		User user = new User(1L, "John Doe", "johndoe@example.com", "password123");
		user.addRole(new Role(1L, "ROLE_CLIENT"));		
		return user;
	}
	
	public static User createAdminUser() {
		User admin = new User(2L, "Jane Doe", "janedoe@example.com", "password123");
		admin.addRole(new Role(2L, "ROLE_ADMIN"));		
		return admin;
	}
	
	public static User createCustomClientUser(Long id, String username) {
		User user = new User(id, "John Doe", username, "password123");
		user.addRole(new Role(1L, "ROLE_CLIENT"));		
		return user;
	}
	
	public static User createCustomAdminUser(Long id, String username) {
		User admin = new User(id, "Jane Doe", username, "password123");
		admin.addRole(new Role(2L, "ROLE_ADMIN"));
		return admin;
	}	

}
