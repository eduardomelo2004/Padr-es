package com.VarandaCafeteria.controller;

import com.VarandaCafeteria.service.bo.SimulacaoPedidoBO;
import com.VarandaCafeteria.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/simulacao")
public class SimulacaoPedidoController {

    @Autowired
    private SimulacaoPedidoBO simulacaoPedidoBO;

    @PostMapping
    public SimulacaoPedidoResponseDTO simularPedido(@RequestBody SimulacaoPedidoRequestDTO request) {
        return simulacaoPedidoBO.simularPedido(request);
    }
}
