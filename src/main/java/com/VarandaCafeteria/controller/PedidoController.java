package com.VarandaCafeteria.controller;

import com.VarandaCafeteria.dto.PedidoRequestDTO;
import com.VarandaCafeteria.dto.PedidoResponseDTO;
import com.VarandaCafeteria.model.entity.Pedido;
import com.VarandaCafeteria.model.enums.EstadoPedido;
import com.VarandaCafeteria.security.JwtUtil;
import com.VarandaCafeteria.service.bo.PedidoBO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoBO pedidoBO;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private HttpServletRequest request;


    /**
     * Endpoint para clientes criarem novos pedidos
     */
    @PostMapping
    public PedidoResponseDTO criarPedido(@RequestBody PedidoRequestDTO dto) {
        return pedidoBO.criarPedido(dto); // Retorna DTO diretamente
    }

    /**
     * Endpoint para a cozinha avançar o estado de um pedido
     */
    @PutMapping("/{id}/avancar-estado")
    public PedidoResponseDTO avancarEstado(@PathVariable Long id) {
        return pedidoBO.avancarEstadoDTO(id);
    }

    /**
     * Endpoint para listar todos os pedidos (ex: painel da cozinha)
     */
    @GetMapping
    public List<PedidoResponseDTO> listarPedidos() {
        return pedidoBO.buscarTodosDTO();
    }

    /**
     * Endpoint para buscar um pedido específico por ID
     */
    @GetMapping("/{id}")
    public PedidoResponseDTO buscarPorId(@PathVariable Long id) {
        return pedidoBO.buscarPorIdDTO(id);
    }

    @GetMapping("/estado/{estado}")
    public List<PedidoResponseDTO> listarPorEstado(@PathVariable String estado) {
        return pedidoBO.buscarPorEstado(estado);
    }


    @GetMapping("/meus-pedidos")
    public List<PedidoResponseDTO> listarMeusPedidos() {
        return pedidoBO.buscarPedidosDoCliente(request);
    }



}
