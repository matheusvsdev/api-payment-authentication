package com.matheusvsdev.apipaymentauthentication.controllers;

import com.matheusvsdev.apipaymentauthentication.dto.CreateWalletDTO;
import com.matheusvsdev.apipaymentauthentication.dto.WalletDTO;
import com.matheusvsdev.apipaymentauthentication.services.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/wallet")
@Tag(name = "Carteira", description = "Operações relacionadas a carteiras digitais")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @PostMapping
    @Operation(summary = "Cria uma carteira digital", description = "Método para criar carteira digital")
    @ApiResponse(responseCode = "201", description = "Carteira criada com sucesso")
    @ApiResponse(responseCode = "403", description = "Usuário já possui o limite máximo de carteiras vinculadas ao CPF")
    @ApiResponse(responseCode = "409", description = "Usuário já possui uma carteira desse tipo")
    public ResponseEntity<WalletDTO> createWallet(@Valid @RequestBody CreateWalletDTO walletDTO) {
        WalletDTO newWallet = walletService.create(walletDTO);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newWallet.getId())
                .toUri();

        return ResponseEntity.created(uri).body(newWallet);
    }
}
