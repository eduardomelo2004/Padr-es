package com.VarandaCafeteria.dto;

import lombok.Data;

import java.util.List;

@Data
public class SimulacaoPedidoRequestDTO {
    private List<SimulacaoItemRequestDTO> itens;
    private String formaPagamento; // "NORMAL", "PIX", "FIDELIDADE"

    // Getters e Setters
}
