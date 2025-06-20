package com.matheusvsdev.apipaymentauthentication.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matheusvsdev.apipaymentauthentication.dto.CreateTransactionDTO;
import com.matheusvsdev.apipaymentauthentication.dto.TransactionDTO;
import com.matheusvsdev.apipaymentauthentication.entities.Transaction;
import com.matheusvsdev.apipaymentauthentication.entities.Wallet;
import com.matheusvsdev.apipaymentauthentication.entities.enums.WalletType;
import com.matheusvsdev.apipaymentauthentication.exceptions.InvalidTransactionException;
import com.matheusvsdev.apipaymentauthentication.exceptions.NotFoundException;
import com.matheusvsdev.apipaymentauthentication.factory.TransactionFactory;
import com.matheusvsdev.apipaymentauthentication.factory.UserFactory;
import com.matheusvsdev.apipaymentauthentication.factory.WalletFactory;
import com.matheusvsdev.apipaymentauthentication.services.TransactionService;
import com.matheusvsdev.apipaymentauthentication.services.UserService;
import com.matheusvsdev.apipaymentauthentication.util.TokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TokenUtil tokenUtil;

    private Long nonExistingId;
    private String senderUsername, senderPassword, receiverUsername, receiverPassword;
    private String senderToken, invalidToken;
    private Wallet sender, receiver;

    @BeforeEach
    void setUp() throws Exception {

        senderUsername = "johndoe@example.com";
        senderPassword = "Abc12345";

        Mockito.when(userService.loadUserByUsername(senderUsername)).thenReturn(UserFactory.createClientUser());

        senderToken = tokenUtil.obtainAccessToken(mockMvc, senderUsername, senderPassword);
        invalidToken = senderToken + "xpto"; // invalid token

        nonExistingId = 100L;

        sender = WalletFactory.createCustomWallet(1L, UserFactory.createClientUser(), WalletType.PERSONAL);
        sender.setBalance(BigDecimal.valueOf(100));

        receiver = WalletFactory.createCustomWallet(2L, UserFactory.createAdminUser(), WalletType.COMPANY);

        Mockito.doAnswer(invocation -> {
            CreateTransactionDTO dto = invocation.getArgument(0);

            // Valida se remetente e destinatário são iguais
            if (dto.getSenderId().equals(dto.getReceiverId())) {
                throw new InvalidTransactionException("O remetente e o destinatário não podem ser iguais");
            }

            // Valida se o saldo é suficiente
            if (dto.getAmount().compareTo(sender.getBalance()) > 0) {
                throw new InvalidTransactionException("Saldo insuficiente");
            }

            Transaction transaction = TransactionFactory.createCustomTransaction(
                    sender, receiver, dto.getAmount()
            );

            return new TransactionDTO(transaction); // Retorna normalmente se todas as regras forem satisfeitas
        }).when(transactionService).createTransaction(any(CreateTransactionDTO.class));
    }

    @Test
    public void shouldReturn201WhenTransactionCreatedSuccessfully() throws Exception {

        CreateTransactionDTO transactionDTO = TransactionFactory.createCustomTransactionDTO(
                sender.getId(), receiver.getId(), BigDecimal.valueOf(50)
        );

        ResultActions result =
                mockMvc.perform(post("/transaction")
                        .header("Authorization", "Bearer " + senderToken)
                        .content(objectMapper.writeValueAsString(transactionDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.senderId").exists());
        result.andExpect(jsonPath("$.receiverId").exists());
        result.andExpect(jsonPath("$.amount").exists());
        result.andExpect(jsonPath("$.moment").exists());

        // Verifica que o controlador realmente chamou o serviço
        Mockito.verify(transactionService, times(1)).createTransaction(any(CreateTransactionDTO.class));
    }

    @Test
    public void shouldReturn400WhenSenderIdAndReceiverIdIsSame() throws Exception {
        Wallet senderWallet = WalletFactory.createCustomWallet(1L, UserFactory.createClientUser(), WalletType.PERSONAL);

        CreateTransactionDTO transactionDTO = TransactionFactory.createCustomTransactionDTO(
                senderWallet.getId(), senderWallet.getId(), BigDecimal.valueOf(50)
        );

        // Loga o JSON gerado para verificar se os valores estão corretos
        System.out.println("JSON enviado: " + objectMapper.writeValueAsString(transactionDTO));

        mockMvc.perform(post("/transaction")
                        .header("Authorization", "Bearer " + senderToken)
                        .content(objectMapper.writeValueAsString(transactionDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("O remetente e o destinatário não podem ser iguais"));
    }

    @Test
    public void shouldReturn400WhenSenderHasInsufficientBalance() throws Exception {

        CreateTransactionDTO transactionDTO = TransactionFactory.createCustomTransactionDTO(
                sender.getId(), receiver.getId(), BigDecimal.valueOf(500));

        System.out.println("JSON enviado: " + objectMapper.writeValueAsString(transactionDTO));

        mockMvc.perform(post("/transaction")
                        .header("Authorization", "Bearer " + senderToken)
                        .content(objectMapper.writeValueAsString(transactionDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Saldo insuficiente"));

        Mockito.verify(transactionService, times(1)).createTransaction(any(CreateTransactionDTO.class));
    }

    @Test
    public void shouldReturn404WhenSenderIdNotFound() throws Exception {

        Mockito.when(transactionService.createTransaction(Mockito.argThat(dto -> dto.getSenderId().equals(nonExistingId))))
                .thenThrow(new NotFoundException("Carteira do remetente não encontrada"));

        CreateTransactionDTO transactionDTO = TransactionFactory.createCustomTransactionDTO(
                nonExistingId, receiver.getId(), BigDecimal.valueOf(50));

        mockMvc.perform(post("/transaction")
                        .header("Authorization", "Bearer " + senderToken)
                        .content(objectMapper.writeValueAsString(transactionDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Carteira do remetente não encontrada"));

        Mockito.verify(transactionService, times(1)).createTransaction(any(CreateTransactionDTO.class));
    }

    @Test
    public void shouldReturn404WhenReceiverIdNotFound() throws Exception {

        Mockito.when(transactionService.createTransaction(Mockito.argThat(dto -> dto.getReceiverId().equals(nonExistingId))))
                .thenThrow(new NotFoundException("Carteira do destinatário não encontrada"));

        CreateTransactionDTO transactionDTO = TransactionFactory.createCustomTransactionDTO(
                sender.getId(), nonExistingId, BigDecimal.valueOf(50));

        mockMvc.perform(post("/transaction")
                        .header("Authorization", "Bearer " + senderToken)
                        .content(objectMapper.writeValueAsString(transactionDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Carteira do destinatário não encontrada"));

        Mockito.verify(transactionService, times(1)).createTransaction(any(CreateTransactionDTO.class));
    }
}
