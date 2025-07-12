//package com.VarandaCafeteria.service.decorator;
//
//// service/decorator/AdicionalApplier.java
//import com.VarandaCafeteria.service.factory.Bebida;
//import org.springframework.stereotype.Component;
//import java.util.List;
//
//@Component
//public class AdicionalApplier {
//
//    public Bebida aplicarAdicionais(Bebida bebidaBase, List<String> adicionais) {
//        for (String adicional : adicionais) {
//            switch (adicional.toUpperCase()) {
//                case "LEITE":
//                    bebidaBase = new ComLeite(bebidaBase);
//                    break;
//                case "CANELA":
//                    bebidaBase = new ComCanela(bebidaBase);
//                    break;
//                case "ACUCAR":
//                    bebidaBase = new ComAcucar(bebidaBase);
//                    break;
//                default:
//                    throw new IllegalArgumentException("Adicional inválido: " + adicional);
//            }
//        }
//        return bebidaBase;
//    }
//}


package com.VarandaCafeteria.service.decorator;

import com.VarandaCafeteria.model.entity.Produto;
import com.VarandaCafeteria.repository.ProdutoRepository;
import com.VarandaCafeteria.service.factory.Bebida;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdicionalApplier {

    @Autowired
    private ProdutoRepository produtoRepository;

    public Bebida aplicarAdicionais(Bebida bebida, List<String> adicionais) {
        for (String nome : adicionais) {
            Produto adicionalProduto = produtoRepository.findByNome(nome.toUpperCase())
                    .orElseThrow(() -> new IllegalArgumentException("Adicional não encontrado: " + nome));

            bebida = new AdicionalDecorator(bebida, adicionalProduto.getNome(), adicionalProduto.getPreco());
        }

        return bebida;
    }
}

