package com.matheusvsdev.apipaymentauthentication.repositories;

import com.matheusvsdev.apipaymentauthentication.entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    List<Wallet> findAllByUserId(Long userId);
}
