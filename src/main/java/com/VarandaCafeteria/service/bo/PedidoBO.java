package com.VarandaCafeteria.service.bo;

import com.VarandaCafeteria.dto.*;
import com.VarandaCafeteria.model.entity.*;
import com.VarandaCafeteria.model.enums.EstadoPedido;
import com.VarandaCafeteria.model.enums.TipoPagamento;
import com.VarandaCafeteria.dao.ClienteDAO;
import com.VarandaCafeteria.dao.PedidoDAO;
import com.VarandaCafeteria.repository.PedidoRepository;
import com.VarandaCafeteria.repository.ProdutoRepository;
import com.VarandaCafeteria.security.JwtUtil;
import com.VarandaCafeteria.service.decorator.AdicionalApplier;
import com.VarandaCafeteria.service.factory.Bebida;
import com.VarandaCafeteria.service.factory.BebidaFactoryProvider;
import com.VarandaCafeteria.service.bo.PagamentoBO;
import com.VarandaCafeteria.service.observer.ClienteObserver;
import com.VarandaCafeteria.service.observer.CozinhaObserver;
import com.VarandaCafeteria.service.state.EstadoPedidoState;
import com.VarandaCafeteria.service.state.EstadoPedidoStateFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.VarandaCafeteria.service.command.AdicionarItemCommand;
import com.VarandaCafeteria.service.command.FinalizarPedidoCommand;
import com.VarandaCafeteria.service.command.PedidoInvoker;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;






import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoBO {

    @Autowired
    private BebidaFactoryProvider bebidaFactoryProvider;

    @Autowired
    private AdicionalApplier adicionalApplier;

    @Autowired
    private ClienteDAO clienteDAO;

    @Autowired
    private PedidoDAO pedidoDAO;

    @Autowired
    private PagamentoBO pagamentoBO;

    @Autowired
    private EstadoPedidoStateFactory estadoPedidoStateFactory;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private JwtUtil jwtUtil;


    public PedidoResponseDTO criarPedido(PedidoRequestDTO dto) {
        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7); // remove "Bearer "

        Long idCliente = jwtUtil.extractId(token);

        Cliente cliente = clienteDAO.buscarPorId(idCliente)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setItens(new ArrayList<>());
        pedido.setPagamento(dto.getTipoPagamento());

        double precoTotal = 0.0;

        // ⏺ Criamos o invocador de comandos
        PedidoInvoker invoker = new PedidoInvoker();

        for (ItemPedidoDTO itemDto : dto.getItens()) {
            Produto produtoBase = produtoRepository.findByNomeAndIsAdicional(itemDto.getBebidaBase().toUpperCase(), false)
                    .orElseThrow(() -> new IllegalArgumentException("Produto base não encontrado: " + itemDto.getBebidaBase()));

            // Cria a bebida base a partir do nome enviado pelo cliente (ex: "CAFÉ")
            Bebida bebida = bebidaFactoryProvider.criarBebida(itemDto.getBebidaBase());

            // Aplica os adicionais com o Decorator
            bebida = adicionalApplier.aplicarAdicionais(bebida, itemDto.getAdicionais());

            String descricaoAdicionais = String.join(", ",
                    itemDto.getAdicionais().stream()
                            .map(String::toUpperCase)
                            .toList()
            );

            // Cria o item do pedido
            ItemPedido item = new ItemPedido();
            item.setPedido(pedido);
            item.setDescricao(descricaoAdicionais);
            item.setPreco(bebida.getPreco());
            item.setProduto(produtoBase);

            // Adiciona os adicionais (entidade)
            List<Adicional> adicionais = new ArrayList<>();
            for (String nomeAdicional : itemDto.getAdicionais()) {
                Produto adicionalProduto = produtoRepository.findByNome(nomeAdicional.toUpperCase())
                        .orElseThrow(() -> new IllegalArgumentException("Adicional não encontrado: " + nomeAdicional));

                Adicional adicional = new Adicional();
                adicional.setItemPedido(item);
                adicional.setProduto(adicionalProduto);

                adicionais.add(adicional);
            }
            item.setAdicionais(adicionais);

            // Adiciona o item ao pedido
            AdicionarItemCommand cmdAdicionar = new AdicionarItemCommand(pedido, item);
            invoker.executarComando(cmdAdicionar);

            precoTotal += bebida.getPreco();
        }

        pedido.setPrecoInicial(precoTotal);

        // Aplica desconto com Strategy
        double precoFinal = pagamentoBO.calcularPrecoFinal(precoTotal, dto.getTipoPagamento());
        pedido.setPrecoFinal(precoFinal);
        pedido.setDesconto(precoTotal - precoFinal);

        // Define estado inicial
        pedido.setEstado(EstadoPedido.REALIZADO);

        // Atualiza a carteira do cliente
        cliente.setCarteiraDigital(cliente.getCarteiraDigital() - precoFinal);
        clienteDAO.salvar(cliente);


        // Adiciona observers
        pedido.adicionarObservador(new CozinhaObserver(messagingTemplate, this));
        pedido.adicionarObservador(new ClienteObserver(cliente.getId()));

        //  Finaliza o pedido usando Command
        FinalizarPedidoCommand cmdFinalizar = new FinalizarPedidoCommand(pedido, pedidoDAO);
        invoker.executarComando(cmdFinalizar);

        // notifica apenas após adicionar o pedido no banco de dados para ter acesso ao id
        pedido.notificarObservadores();

        //notifica o Observer
        messagingTemplate.convertAndSend(
                "/topic/pedidos/" + pedido.getCliente().getId(),
                pedido.getEstado().name()
        );

        messagingTemplate.convertAndSend(
                "/topic/cozinha",
                "Novo pedido #" + pedido.getId() + " - " + pedido.getEstado().name()
        );


        return toResponseDTO(pedido);
    }

    public Bebida montarBebida(String tipoBebida, List<String> adicionais) {
        // Factory Method
        Bebida base = bebidaFactoryProvider.criarBebida(tipoBebida);

        // Decorator
        Bebida completa = adicionalApplier.aplicarAdicionais(base, adicionais);

        return completa;
    }

    public Pedido avancarEstado(Long id){
        Pedido pedido = pedidoDAO.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

        EstadoPedido estadoAtual = pedido.getEstado();
        EstadoPedidoState estado = estadoPedidoStateFactory.getEstado(estadoAtual);

        estado.proximo(pedido); // muda o estado no pedido
        pedidoDAO.salvar(pedido);

        pedido.notificarObservadores(); // notifica cliente e/ou cozinha

        messagingTemplate.convertAndSend(
                "/topic/pedidos/" + pedido.getCliente().getId(),
                pedido.getEstado().name()
        );

        return pedido;
    }

    public PedidoResponseDTO toResponseDTO(Pedido pedido) {
        PedidoResponseDTO dto = new PedidoResponseDTO();
        dto.setId(pedido.getId());
        dto.setPrecoFinal(pedido.getPrecoFinal());
        dto.setEstado(pedido.getEstado().name());

        ClienteResponseDTO clienteDTO = new ClienteResponseDTO();
        clienteDTO.setId(pedido.getCliente().getId());
        clienteDTO.setEmail(pedido.getCliente().getEmail());
        dto.setCliente(clienteDTO);

        List<ItemPedidoResponseDTO> itens = pedido.getItens().stream().map(item -> {
            ItemPedidoResponseDTO i = new ItemPedidoResponseDTO();

            // Agora pegamos a bebida base a partir do produto vinculado
            i.setBebidaBase(item.getProduto() != null ? item.getProduto().getNome() : "DESCONHECIDO");

            // Adicionais continuam sendo lidos da entidade adicional
            List<String> adicionais = item.getAdicionais().stream()
                    .map(ad -> ad.getProduto().getNome())
                    .toList();

            i.setAdicionais(adicionais);
            i.setPreco(item.getPreco());

            return i;
        }).toList();

        dto.setItens(itens);
        return dto;
    }

    public List<Pedido> buscarTodos() {
        return pedidoRepository.findAll();
    }

    public Pedido buscarPorId(Long id) {
        return pedidoDAO.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));
    }

    public PedidoResponseDTO buscarPorIdDTO(Long id) {
        Pedido pedido = pedidoDAO.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));
        return toResponseDTO(pedido);
    }

    public PedidoResponseDTO avancarEstadoDTO(Long id) {
        Pedido pedido = avancarEstado(id);
        return toResponseDTO(pedido);
    }

    public List<PedidoResponseDTO> buscarTodosDTO() {
        return buscarTodos().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<Pedido> buscarPorEstado(EstadoPedido estado) {
        return pedidoDAO.buscarPorEstado(estado);
    }

    public List<Pedido> buscarPorCliente(Long idCliente) {
        return pedidoDAO.buscarPorCliente(idCliente);
    }


}
