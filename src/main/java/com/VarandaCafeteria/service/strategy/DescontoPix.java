package com.VarandaCafeteria.service.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;

// service/strategy/DescontoPix.java
public class DescontoPix implements DescontoStrategy {
    public double aplicarDesconto(double valor) {
        BigDecimal preco = BigDecimal.valueOf(valor);
        BigDecimal desconto = preco.multiply(BigDecimal.valueOf(0.05));
        BigDecimal finalComDesconto = preco.subtract(desconto).setScale(2, RoundingMode.HALF_UP);
        return finalComDesconto.doubleValue(); // 5% de desconto
    }
}

