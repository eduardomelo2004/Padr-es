package com.VarandaCafeteria.service.bo;

import com.VarandaCafeteria.dto.*;
import com.VarandaCafeteria.model.entity.Cliente;
import com.VarandaCafeteria.repository.ClienteRepository;
import com.VarandaCafeteria.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class ClienteBO {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public ClienteResponseDTO criarConta(ClienteRequestDTO dto) {
        if (clienteRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já cadastrado");
        }

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

    public LoginResponseDTO autenticarCliente(ClienteLoginDTO dto) {
        Cliente cliente = clienteRepository.findByEmailAndSenha(dto.getEmail(), dto.getSenha())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas"));

        if (cliente.getRole() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário sem role definida");
        }

        String token = jwtUtil.generateToken(cliente.getId(), cliente.getEmail(), cliente.getRole().name());
        return new LoginResponseDTO(token, cliente.getRole().name());
    }

    public CarteiraResponseDTO consultarCarteira(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        CarteiraResponseDTO dto = new CarteiraResponseDTO();
        dto.setIdCliente(cliente.getId());
        dto.setSaldo(cliente.getCarteiraDigital());

        return dto;
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    public CarteiraResponseDTO consultarCarteira(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Long id = jwtUtil.extractId(token);

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

        CarteiraResponseDTO dto = new CarteiraResponseDTO();
        dto.setIdCliente(cliente.getId());
        dto.setSaldo(cliente.getCarteiraDigital());

        return dto;
    }
}
