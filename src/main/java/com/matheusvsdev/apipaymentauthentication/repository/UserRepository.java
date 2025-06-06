package com.matheusvsdev.apipaymentauthentication.repository;

import com.matheusvsdev.apipaymentauthentication.entities.User;
import com.matheusvsdev.apipaymentauthentication.projections.UserDetailsProjection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// 3.1
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	/**
     * Busca um usuário pelo e-mail.
     * Retorna um `Optional<User>`, evitando `NullPointerException` caso não exista no banco.
     *
     * @param email O e-mail do usuário a ser buscado.
     * @return Optional contendo o usuário, se encontrado.
     */
    Optional<User> findByEmail(String email);

    /**
     * Busca um usuário e suas roles associadas usando projeção.
     * Retorna uma lista de `UserDetailsProjection`, garantindo eficiência na consulta.
     *
     * @param email O e-mail do usuário a ser buscado.
     * @return Lista de projeções contendo detalhes do usuário e suas roles.
     */
    @Query(nativeQuery = true, value = """
			SELECT tb_user.email AS username, tb_user.password, tb_role.id AS roleId, tb_role.authority
			FROM tb_user
			INNER JOIN tb_user_role ON tb_user.id = tb_user_role.user_id
			INNER JOIN tb_role ON tb_role.id = tb_user_role.role_id
			WHERE tb_user.email = :email
		""")
	List<UserDetailsProjection> searchUserAndRolesByEmail(String email);
}
