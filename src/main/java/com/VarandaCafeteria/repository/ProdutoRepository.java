package com.VarandaCafeteria.repository;

import com.VarandaCafeteria.model.entity.Produto;
import com.VarandaCafeteria.model.enums.TipoProduto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findByIsAdicional(Boolean isAdicional);
    Optional<Produto> findByNome(String nome);
    Optional<Produto> findByNomeAndIsAdicional(String nome, boolean isAdicional);
}
