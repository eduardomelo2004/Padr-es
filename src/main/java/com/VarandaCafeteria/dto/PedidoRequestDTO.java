package com.VarandaCafeteria.dto;

import com.VarandaCafeteria.model.enums.TipoPagamento;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoRequestDTO {
    private TipoPagamento tipoPagamento;
    private List<ItemPedidoDTO> itens;

    // Getters e Setters
}
