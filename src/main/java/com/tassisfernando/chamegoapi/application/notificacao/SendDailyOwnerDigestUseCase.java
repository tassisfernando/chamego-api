package com.tassisfernando.chamegoapi.application.notificacao;

import com.tassisfernando.chamegoapi.domain.cliente.Cliente;
import com.tassisfernando.chamegoapi.domain.cliente.ClienteRepository;
import com.tassisfernando.chamegoapi.domain.pedido.ItemPedido;
import com.tassisfernando.chamegoapi.domain.pedido.Pedido;
import com.tassisfernando.chamegoapi.domain.pedido.PedidoRepository;
import com.tassisfernando.chamegoapi.domain.produto.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.MonthDay;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class SendDailyOwnerDigestUseCase {

    public static final ZoneId FUSO_OPERACAO = ZoneId.of("America/Sao_Paulo");

    private final ClienteRepository clienteRepository;
    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final WhatsAppNotificationPort whatsAppNotificationPort;
    private final DestinoResumoDiarioPort destinoResumoDiarioPort;

    public SendDailyOwnerDigestUseCase(
            ClienteRepository clienteRepository,
            PedidoRepository pedidoRepository,
            ProdutoRepository produtoRepository,
            WhatsAppNotificationPort whatsAppNotificationPort,
            DestinoResumoDiarioPort destinoResumoDiarioPort) {
        this.clienteRepository = clienteRepository;
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
        this.whatsAppNotificationPort = whatsAppNotificationPort;
        this.destinoResumoDiarioPort = destinoResumoDiarioPort;
    }

    @Transactional(readOnly = true)
    public Resultado executar() {
        return executar(LocalDate.now(FUSO_OPERACAO));
    }

    @Transactional(readOnly = true)
    public Resultado executar(LocalDate referencia) {
        List<Cliente> aniversariantes = clienteRepository
                .buscarAniversariantesDoDia(MonthDay.from(referencia))
                .stream()
                .filter(Cliente::ativo)
                .toList();
        List<Pedido> entregas = pedidoRepository.listarPorDataEntrega(referencia, referencia);

        var linhasAniversario = aniversariantes.stream()
                .map(cliente -> new MensagemResumoDiarioProprietaria.AniversarianteLinha(
                        cliente.nome(), cliente.telefone()))
                .toList();
        var linhasEntrega = resumirEntregas(entregas);
        var mensagem = MensagemResumoDiarioProprietaria.montar(
                destinoResumoDiarioPort.nomeProprietaria(), referencia, linhasAniversario, linhasEntrega);
        if (mensagem.isEmpty()) {
            return new Resultado(referencia, linhasAniversario.size(), linhasEntrega.size(), false, false);
        }
        boolean entregue = whatsAppNotificationPort.enviarTexto(
                destinoResumoDiarioPort.whatsappProprietaria(), mensagem.get());
        return new Resultado(referencia, linhasAniversario.size(), linhasEntrega.size(), true, entregue);
    }

    private List<MensagemResumoDiarioProprietaria.EntregaLinha> resumirEntregas(List<Pedido> entregas) {
        Map<UUID, String> nomes = new HashMap<>();
        List<MensagemResumoDiarioProprietaria.EntregaLinha> linhas = new ArrayList<>();
        for (Pedido pedido : entregas) {
            Cliente cliente = clienteRepository.buscarPorId(pedido.clienteId()).orElse(null);
            linhas.add(new MensagemResumoDiarioProprietaria.EntregaLinha(
                    pedido.id(),
                    cliente == null ? "Cliente" : cliente.nome(),
                    cliente == null ? null : cliente.telefone(),
                    resumoItens(pedido, nomes),
                    pedido.valorTotal().valor()));
        }
        return linhas;
    }

    private String resumoItens(Pedido pedido, Map<UUID, String> nomes) {
        List<String> partes = new ArrayList<>();
        for (ItemPedido item : pedido.itens()) {
            String nome = nomes.computeIfAbsent(item.produtoId(), id -> produtoRepository
                    .buscarPorId(id)
                    .map(produto -> produto.nome())
                    .orElse("Produto"));
            partes.add(item.quantidade() + "× " + nome);
        }
        return partes.isEmpty() ? "itens" : String.join(", ", partes);
    }

    public record Resultado(
            LocalDate data,
            int aniversariantes,
            int entregas,
            boolean notificacaoEnviada,
            boolean entreguePelaApi) {}
}
