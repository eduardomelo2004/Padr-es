package com.VarandaCafeteria.service.factory;

public class BebidaSimples implements Bebida {
    private final String descricao;
    private final double preco;

    public BebidaSimples(String descricao, double preco) {
        this.descricao = descricao;
        this.preco = preco;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    @Override
    public double getPreco() {
        return preco;
    }
}
