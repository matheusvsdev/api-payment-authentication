package com.matheusvsdev.apipaymentauthentication.repositories;

import com.matheusvsdev.apipaymentauthentication.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
