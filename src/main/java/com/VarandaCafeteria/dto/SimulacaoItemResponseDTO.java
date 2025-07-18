package com.VarandaCafeteria.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimulacaoItemResponseDTO {
    private String identificador;
    private double valor;

//    public SimulacaoItemResponseDTO(String identificador, double valor) {
//        this.identificador = identificador;
//        this.valor = valor;
//    }

    // Getters e Setters
}
