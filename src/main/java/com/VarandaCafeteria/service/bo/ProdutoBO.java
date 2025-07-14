package com.VarandaCafeteria.service.bo;

import com.VarandaCafeteria.dto.ProdutoRequestDTO;
import com.VarandaCafeteria.dto.ProdutoResponseDTO;
import com.VarandaCafeteria.model.entity.Produto;
import com.VarandaCafeteria.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProdutoBO {

    @Autowired
    private ProdutoRepository produtoRepository;

    public Produto criarProduto(ProdutoRequestDTO dto) {
        if (dto.getNome() == null || dto.getPreco() == null || dto.getIsAdicional() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados do produto incompletos");
        }

        Produto produto = new Produto();
        produto.setNome(dto.getNome().toUpperCase()); // garante consistência
        produto.setPreco(dto.getPreco());
        produto.setIsAdicional(dto.getIsAdicional());

        return produtoRepository.save(produto);
    }

    public List<ProdutoResponseDTO> listarBebidasBase() {
        return produtoRepository.findByIsAdicional(false)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<ProdutoResponseDTO> listarAdicionais() {
        return produtoRepository.findByIsAdicional(true)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private ProdutoResponseDTO toDTO(Produto produto) {
        ProdutoResponseDTO dto = new ProdutoResponseDTO();
        dto.setId(produto.getId());
        dto.setNome(produto.getNome());
        dto.setPreco(produto.getPreco());
        return dto;
    }
}
