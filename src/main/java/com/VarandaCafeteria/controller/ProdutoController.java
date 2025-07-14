package com.VarandaCafeteria.controller;

import com.VarandaCafeteria.dto.ProdutoRequestDTO;
import com.VarandaCafeteria.dto.ProdutoResponseDTO;
import com.VarandaCafeteria.model.entity.Produto;
import com.VarandaCafeteria.service.bo.ProdutoBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoBO produtoBO;

    @PostMapping
    public ResponseEntity<Produto> criarProduto(@RequestBody ProdutoRequestDTO dto) {
        Produto produtoCriado = produtoBO.criarProduto(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoCriado);
    }

    @GetMapping("/bebidas-base")
    public ResponseEntity<List<ProdutoResponseDTO>> listarBebidaBase() {
        return ResponseEntity.ok(produtoBO.listarBebidasBase());
    }

    @GetMapping("/adicionais")
    public ResponseEntity<List<ProdutoResponseDTO>> listarAdicionais() {
        return ResponseEntity.ok(produtoBO.listarAdicionais());
    }
}
