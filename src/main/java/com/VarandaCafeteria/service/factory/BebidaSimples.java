package com.VarandaCafeteria.service.factory;

import com.VarandaCafeteria.model.entity.Produto;

public class BebidaSimples implements Bebida {

    private final Produto produto;

    public BebidaSimples(Produto produto) {
        this.produto = produto;
    }

    @Override
    public String getDescricao() {
        return produto.getNome();
    }

    @Override
    public double getPreco() {
        return produto.getPreco();
    }
}
