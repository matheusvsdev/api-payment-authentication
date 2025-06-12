package com.matheusvsdev.apipaymentauthentication.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matheusvsdev.apipaymentauthentication.dto.CreateWalletDTO;
import com.matheusvsdev.apipaymentauthentication.dto.WalletDTO;
import com.matheusvsdev.apipaymentauthentication.entities.User;
import com.matheusvsdev.apipaymentauthentication.entities.Wallet;
import com.matheusvsdev.apipaymentauthentication.entities.enums.WalletType;
import com.matheusvsdev.apipaymentauthentication.exceptions.DuplicateWalletException;
import com.matheusvsdev.apipaymentauthentication.exceptions.MaxWalletsLimitException;
import com.matheusvsdev.apipaymentauthentication.factory.WalletFactory;
import com.matheusvsdev.apipaymentauthentication.services.WalletService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WalletService walletService;

    @Autowired
    private ObjectMapper objectMapper;

    private Wallet walletPersonal, walletCompany;
    private CreateWalletDTO createWallet;
    private WalletDTO walletPersonalResponse, walletCompanyResponse;

    @BeforeEach
    void setUp() {

        User user = new User(1L, "John Doe", "12345678900", "johndoe@email.com", "Abc123456");

        walletPersonal = WalletFactory.createCustomWallet(1L, user, WalletType.PERSONAL);
        walletCompany = WalletFactory.createCustomWallet(2L, user, WalletType.COMPANY);

        createWallet = new CreateWalletDTO(walletPersonal);

        walletPersonalResponse = new WalletDTO(walletPersonal);
        walletCompanyResponse = new WalletDTO(walletCompany);
    }

    @Test
    public void createShouldReturn201WhenWalletCreatedSuccessfully() throws Exception {

        Mockito.when(walletService.create(any(CreateWalletDTO.class)))
                .thenReturn(walletPersonalResponse);

        ResultActions result =
                mockMvc.perform(post("/wallet")
                        .content(objectMapper.writeValueAsString(createWallet))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.walletType").exists());
        result.andExpect(jsonPath("$.balance").exists());
        result.andExpect(jsonPath("$.user").exists());
    }

    @Test
    public void shouldReturn403WhenUserExceedsWalletLimit() throws Exception {

        Mockito.when(walletService.create(any(CreateWalletDTO.class)))
                .thenReturn(walletPersonalResponse) // Primeira chamada
                .thenReturn(walletCompanyResponse) // Segunda chamada
                .thenThrow(new MaxWalletsLimitException("Usuário já possui o limite máximo de carteiras vinculadas ao CPF"));

        CreateWalletDTO walletPersonalDTO = WalletFactory.createWalletDTO("12345678900", WalletType.PERSONAL);
        CreateWalletDTO walletCompanyDTO = WalletFactory.createWalletDTO("12345678900", WalletType.COMPANY);

        // Primeira Wallet
        mockMvc.perform(post("/wallet")
                        .content(objectMapper.writeValueAsString(walletPersonalDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        // Segunda Wallet
        mockMvc.perform(post("/wallet")
                        .content(objectMapper.writeValueAsString(walletCompanyDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        // Terceira Wallet (Limite atingido)
        mockMvc.perform(post("/wallet")
                        .content(objectMapper.writeValueAsString(walletCompanyDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Usuário já possui o limite máximo de carteiras vinculadas ao CPF"));
    }

    @Test
    public void shouldReturn409WhenUserTryCreateWalletSameType() throws Exception {

        Mockito.when(walletService.create(any(CreateWalletDTO.class)))
                .thenReturn(walletPersonalResponse) // Primeira chamada
                .thenThrow(new DuplicateWalletException("Usuário já possui uma carteira do tipo " + walletPersonalResponse.getWalletType()));

        CreateWalletDTO walletPersonalDTO = WalletFactory.createWalletDTO("12345678900", WalletType.PERSONAL);
        CreateWalletDTO walletPersonalTwoDTO = WalletFactory.createWalletDTO("12345678900", WalletType.PERSONAL);

        // Primeira Wallet
        mockMvc.perform(post("/wallet")
                        .content(objectMapper.writeValueAsString(walletPersonalDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/wallet")
                        .content(objectMapper.writeValueAsString(walletPersonalTwoDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Usuário já possui uma carteira do tipo " + walletPersonalTwoDTO.getWalletType()));
    }
}
