package com.VarandaCafeteria.service.observer;

import com.VarandaCafeteria.model.entity.Pedido;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import com.VarandaCafeteria.service.bo.PedidoBO;

public class CozinhaObserver implements PedidoObserver {

    private final SimpMessagingTemplate messagingTemplate;
    private final PedidoBO pedidoBO;

    public CozinhaObserver(SimpMessagingTemplate messagingTemplate, PedidoBO pedidoBO) {
        this.messagingTemplate = messagingTemplate;
        this.pedidoBO = pedidoBO;
    }

    @Override
    public void atualizar(Pedido pedido) {
        messagingTemplate.convertAndSend(
                "/topic/cozinha",
                "Novo pedido #" + pedido.getId() + " - " + pedido.getEstado().name()
        );
    }
}
