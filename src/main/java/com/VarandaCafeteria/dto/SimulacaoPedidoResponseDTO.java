package com.VarandaCafeteria.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimulacaoPedidoResponseDTO {
    private List<SimulacaoItemResponseDTO> itens;
    private double total;
    private double desconto;
    private double valorFinal;

//    public SimulacaoPedidoResponseDTO(List<SimulacaoItemResponseDTO> itens, double total, double desconto, double valorFinal) {
//        this.itens = itens;
//        this.total = total;
//        this.desconto = desconto;
//        this.valorFinal = valorFinal;
//    }

    // Getters e Setters
}
