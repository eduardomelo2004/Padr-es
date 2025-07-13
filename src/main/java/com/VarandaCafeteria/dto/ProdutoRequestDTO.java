package com.VarandaCafeteria.dto;

import lombok.Data;

@Data
public class ProdutoRequestDTO {
    private String nome;
    private Double preco;
    private Boolean isAdicional;
}
