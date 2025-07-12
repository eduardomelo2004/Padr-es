package com.VarandaCafeteria.service.decorator;

import com.VarandaCafeteria.service.factory.Bebida;

public class AdicionalDecorator extends BebidaDecorator {

    private final String nomeAdicional;
    private final double precoAdicional;

    public AdicionalDecorator(Bebida bebida, String nomeAdicional, double precoAdicional) {
        super(bebida);
        this.nomeAdicional = nomeAdicional;
        this.precoAdicional = precoAdicional;
    }

    @Override
    public String getDescricao() {
        return bebida.getDescricao() + ", com " + nomeAdicional;
    }

    @Override
    public double getPreco() {
        return bebida.getPreco() + precoAdicional;
    }
}
