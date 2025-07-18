package com.VarandaCafeteria.dto;

import lombok.Data;

import java.util.List;

@Data
public class SimulacaoItemRequestDTO {
    private String identificador; // Ex: "item1"
    private String bebidaBase;
    private List<String> adicionais;

    // Getters e Setters
}
