package com.VarandaCafeteria.service.bo;

import com.VarandaCafeteria.dto.*;
import com.VarandaCafeteria.model.entity.Produto;
import com.VarandaCafeteria.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class SimulacaoPedidoBO {

    @Autowired
    private ProdutoRepository produtoRepository;

    public SimulacaoPedidoResponseDTO simularPedido(SimulacaoPedidoRequestDTO request) {
        List<SimulacaoItemResponseDTO> itensCalculados = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (SimulacaoItemRequestDTO item : request.getItens()) {
            BigDecimal valorItem = BigDecimal.ZERO;

            Produto bebida = produtoRepository.findByNome(item.getBebidaBase())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + item.getBebidaBase()));
            valorItem = valorItem.add(BigDecimal.valueOf(bebida.getPreco()));

            for (String adicional : item.getAdicionais()) {
                Produto prodAdicional = produtoRepository.findByNome(adicional)
                        .orElseThrow(() -> new RuntimeException("Adicional não encontrado: " + adicional));
                valorItem = valorItem.add(BigDecimal.valueOf(prodAdicional.getPreco()));
            }

            valorItem = valorItem.setScale(2, RoundingMode.HALF_UP);

            itensCalculados.add(new SimulacaoItemResponseDTO(item.getIdentificador(), valorItem.doubleValue()));
            total = total.add(valorItem);
        }

        total = total.setScale(2, RoundingMode.HALF_UP);

        BigDecimal desconto = calcularDesconto(total, request.getFormaPagamento());
        desconto = desconto.setScale(2, RoundingMode.HALF_UP);

        BigDecimal valorFinal = total.subtract(desconto).setScale(2, RoundingMode.HALF_UP);

        return new SimulacaoPedidoResponseDTO(itensCalculados, total.doubleValue(), desconto.doubleValue(), valorFinal.doubleValue());
    }

    private BigDecimal calcularDesconto(BigDecimal total, String formaPagamento) {
        return switch (formaPagamento.toUpperCase()) {
            case "PIX" -> total.multiply(BigDecimal.valueOf(0.05));
            case "FIDELIDADE" -> total.multiply(BigDecimal.valueOf(0.10));
            default -> BigDecimal.ZERO;
        };
    }
}
