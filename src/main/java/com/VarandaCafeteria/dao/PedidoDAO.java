package com.VarandaCafeteria.dao;

import com.VarandaCafeteria.model.entity.Pedido;
import com.VarandaCafeteria.model.enums.EstadoPedido;

import java.util.Optional;
import java.util.List;

public interface PedidoDAO {
    Pedido salvar(Pedido pedido);
    Optional<Pedido> buscarPorId(Long id);
    List<Pedido> buscarTodos();
    List<Pedido> buscarPorEstado(EstadoPedido estado);
    List<Pedido> buscarPorCliente(Long idCliente);
}
