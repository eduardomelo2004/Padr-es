// service/factory/BebidaFactoryProvider.java
package com.VarandaCafeteria.service.factory;

import com.VarandaCafeteria.model.entity.Produto;
import com.VarandaCafeteria.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BebidaFactoryProvider {

    @Autowired
    private ProdutoRepository produtoRepository;

    public Bebida criarBebida(String tipo) {
        Produto produto = produtoRepository.findByNomeAndIsAdicional(tipo.toUpperCase(), false)
                .orElseThrow(() -> new IllegalArgumentException("Bebida base não encontrada: " + tipo));

        return new BebidaSimples(produto);
    }
}
