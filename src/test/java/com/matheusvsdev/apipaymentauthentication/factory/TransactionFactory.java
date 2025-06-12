package com.matheusvsdev.apipaymentauthentication.factory;

import com.matheusvsdev.apipaymentauthentication.dto.CreateTransactionDTO;
import com.matheusvsdev.apipaymentauthentication.dto.TransactionDTO;
import com.matheusvsdev.apipaymentauthentication.entities.Transaction;
import com.matheusvsdev.apipaymentauthentication.entities.Wallet;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionFactory {

    /**
     * Cria um `CreateTransactionDTO` padrão para simular requisições de transações nos testes.
     *
     * @return `CreateTransactionDTO` pronto para testes
     */
    public static CreateTransactionDTO createTransactionDTO() {
        return new CreateTransactionDTO(1L, 2L, BigDecimal.valueOf(100));
    }

    /**
     * Cria um `CreateCustomTransactionDTO` customizado para simular requisições de transações nos testes.
     *
     * @param senderId ID da carteira remetente
     * @param receiverId ID da carteira destinatária
     * @param amount Valor da transação
     * @return `CreateCustomTransactionDTO` pronto para testes
     */
    public static CreateTransactionDTO createCustomTransactionDTO(Long senderId, Long receiverId, BigDecimal amount) {
        return new CreateTransactionDTO(senderId, receiverId, amount);
    }

    /**
     * Cria uma `Transaction` baseada em duas carteiras.
     * Útil para validar lógica de persistência e testes de repositório.
     *
     * @param sender Wallet remetente
     * @param receiver Wallet destinatária
     * @param amount Valor da transação
     * @return `Transaction` pronta para testes
     */
    public static Transaction createCustomTransaction(Wallet sender, Wallet receiver, BigDecimal amount) {
        return new Transaction(1L, sender, receiver, amount, LocalDateTime.now());
    }

    /**
     * Converte uma transação para `TransactionDTO`, útil para validar retornos do serviço.
     *
     * @param transaction Objeto `Transaction`
     * @return `TransactionDTO` pronto para testes
     */
    public static TransactionDTO createTransactionDTOFromEntity(Transaction transaction) {
        return new TransactionDTO(transaction);
    }
}
