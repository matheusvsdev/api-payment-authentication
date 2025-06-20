package com.matheusvsdev.apipaymentauthentication.dto;

import com.matheusvsdev.apipaymentauthentication.entities.Transaction;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CreateTransactionDTO {

    private Long senderId;

    @NotNull(message = "O ID do destinatário é obrigatório")
    private Long receiverId;

    @NotNull(message = "O valor da transação é obrigatório")
    @Positive(message = "O valor deve ser maior que R$0.00")
    private BigDecimal amount;

    private LocalDateTime moment;

    public CreateTransactionDTO() {
    }

    public CreateTransactionDTO(Long senderId, Long receiverId, BigDecimal amount) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.amount = amount;
    }

    public CreateTransactionDTO(Transaction entity) {
        senderId = entity.getSenderId().getId();
        receiverId = entity.getReceiverId().getId();
        amount = entity.getAmount();
        moment = entity.getMoment();
    }

    public Long getSenderId() {
        return senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getMoment() {
        return moment;
    }
}
