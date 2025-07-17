package com.VarandaCafeteria.service.observer;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.VarandaCafeteria.model.entity.Pedido;

public class ClienteObserver implements PedidoObserver {

    private final Long idCliente;
    private final SimpMessagingTemplate messagingTemplate;

    public ClienteObserver(Long idCliente, SimpMessagingTemplate messagingTemplate) {
        this.idCliente = idCliente;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void atualizar(Pedido pedido) {
        messagingTemplate.convertAndSendToUser(
                idCliente.toString(),
                "/queue/pedidos",
                pedido.getEstado().name()
        );
        System.out.println("Cliente " + idCliente + " foi notificado via /user/queue/pedidos: Pedido #" + pedido.getId()
                + " está agora em: " + pedido.getEstado());
    }

}