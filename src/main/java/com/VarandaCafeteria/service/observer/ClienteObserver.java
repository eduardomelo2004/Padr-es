package com.VarandaCafeteria.service.observer;

import com.VarandaCafeteria.model.entity.Pedido;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class ClienteObserver implements PedidoObserver {

    private final String idCliente; // id convertido para string
    private final SimpMessagingTemplate messagingTemplate;

    public ClienteObserver(Long idCliente, SimpMessagingTemplate messagingTemplate) {
        this.idCliente = idCliente.toString();
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void atualizar(Pedido pedido) {
        // Monta a mensagem para enviar ao cliente
        String mensagem = "Pedido #" + pedido.getId() + " está agora em: " + pedido.getEstado().name();

        // Envia a mensagem para o destino user específico
        messagingTemplate.convertAndSendToUser(
                idCliente,           // usuário (id cliente como string)
                "/queue/pedidos",    // destino após /user/{idCliente}
                mensagem             // conteúdo da mensagem
        );

        System.out.println("Cliente " + idCliente + " foi notificado via /user/queue/pedidos: " + mensagem);
    }
}
