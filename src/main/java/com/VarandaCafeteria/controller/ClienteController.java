package com.VarandaCafeteria.controller;

import com.VarandaCafeteria.dto.*;
import com.VarandaCafeteria.service.bo.ClienteBO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteBO clienteBO;

    /**
     * Cadastro de novo cliente
     */
    @PostMapping
    public ClienteResponseDTO criarConta(@RequestBody ClienteRequestDTO dto) {
        return clienteBO.criarConta(dto);
    }

    /**
     * Login JWT
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody ClienteLoginDTO dto) {
        return ResponseEntity.ok(clienteBO.autenticarCliente(dto));
    }

    /**
     * Consulta o saldo da carteira digital de um cliente
     */
    @GetMapping("/carteira")
    public ResponseEntity<CarteiraResponseDTO> consultarCarteira(HttpServletRequest request) {
        return ResponseEntity.ok(clienteBO.consultarCarteira(request));
    }

}