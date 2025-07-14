package com.VarandaCafeteria.service.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;

// service/strategy/DescontoFidelidade.java
public class DescontoFidelidade implements DescontoStrategy {
    public double aplicarDesconto(double valor) {
        BigDecimal preco = BigDecimal.valueOf(valor);
        BigDecimal desconto = preco.multiply(BigDecimal.valueOf(0.10));
        BigDecimal finalComDesconto = preco.subtract(desconto).setScale(2, RoundingMode.HALF_UP);
        return finalComDesconto.doubleValue();
    }
}
