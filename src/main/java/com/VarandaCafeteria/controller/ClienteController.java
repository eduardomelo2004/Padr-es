package com.VarandaCafeteria.controller;

import com.VarandaCafeteria.dto.*;
import com.VarandaCafeteria.model.entity.Cliente;
import com.VarandaCafeteria.dao.ClienteDAO;

import com.VarandaCafeteria.repository.ClienteRepository;
import com.VarandaCafeteria.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteDAO clienteDAO;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    JwtUtil jwtUtil;

    /**
     * Cadastro de novo cliente
     */
    @PostMapping
    public ClienteResponseDTO criarConta(@RequestBody ClienteRequestDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setEmail(dto.getEmail());
        cliente.setSenha(dto.getSenha());
        cliente.setCarteiraDigital(dto.getCarteiraDigital());
        cliente.setRole(dto.getRole());

        Cliente salvo = clienteRepository.save(cliente);

        ClienteResponseDTO response = new ClienteResponseDTO();
        response.setId(salvo.getId());
        response.setEmail(salvo.getEmail());
        return response;
    }

    /**
     * Login JWT
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody ClienteLoginDTO dto) {
        Cliente cliente = clienteRepository.findByEmailAndSenha(dto.getEmail(), dto.getSenha())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas"));

        if (cliente.getRole() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário sem role definida");
        }

        String token = jwtUtil.generateToken(cliente.getId(), cliente.getEmail(), cliente.getRole().name());
        return ResponseEntity.ok(new LoginResponseDTO(token, cliente.getRole().name()));
    }
    /**
     * Consulta o saldo da carteira digital de um cliente
     */
    @GetMapping("/{id}/carteira")
    public ResponseEntity<CarteiraResponseDTO> consultarCarteira(@PathVariable Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        CarteiraResponseDTO dto = new CarteiraResponseDTO();
        dto.setIdCliente(cliente.getId());
        dto.setSaldo(cliente.getCarteiraDigital());

        return ResponseEntity.ok(dto);
    }
}
