package com.VarandaCafeteria.repository;

import com.VarandaCafeteria.model.entity.Pedido;
import com.VarandaCafeteria.model.enums.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByEstado(EstadoPedido estado);
    List<Pedido> findByClienteId(Long idCliente);
}
