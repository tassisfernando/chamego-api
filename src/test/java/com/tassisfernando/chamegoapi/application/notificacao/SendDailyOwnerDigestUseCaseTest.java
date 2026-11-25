package com.tassisfernando.chamegoapi.application.notificacao;

import com.tassisfernando.chamegoapi.domain.cliente.Cliente;
import com.tassisfernando.chamegoapi.domain.cliente.ClienteRepository;
import com.tassisfernando.chamegoapi.domain.pedido.ItemPedido;
import com.tassisfernando.chamegoapi.domain.pedido.NaturezaPedido;
import com.tassisfernando.chamegoapi.domain.pedido.Pedido;
import com.tassisfernando.chamegoapi.domain.pedido.PedidoRepository;
import com.tassisfernando.chamegoapi.domain.pedido.StatusPedido;
import com.tassisfernando.chamegoapi.domain.produto.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.MonthDay;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.tassisfernando.chamegoapi.suporte.FixturesDominio.brl;
import static com.tassisfernando.chamegoapi.suporte.FixturesDominio.velaComEstoque;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SendDailyOwnerDigestUseCaseTest {

    private final LocalDate hoje = LocalDate.of(2026, 11, 24);

    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private ProdutoRepository produtoRepository;
    @Mock
    private WhatsAppNotificationPort whatsAppNotificationPort;
    @Mock
    private DestinoResumoDiarioPort destinoResumoDiarioPort;

    private SendDailyOwnerDigestUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new SendDailyOwnerDigestUseCase(
                clienteRepository,
                pedidoRepository,
                produtoRepository,
                whatsAppNotificationPort,
                destinoResumoDiarioPort);
        lenient().when(destinoResumoDiarioPort.nomeProprietaria()).thenReturn("Maressa");
        lenient().when(destinoResumoDiarioPort.whatsappProprietaria()).thenReturn("+5531989319621");
    }

    @Test
    void enviaQuandoHaAniversarioEEntrega() {
        Cliente aniversariante = Cliente.novo("Helena", "31988887777", "Ipatinga", "MG", LocalDate.of(1992, 11, 24));
        var produto = velaComEstoque(4);
        Pedido pedido = pedidoDe(aniversariante, produto.id(), hoje);
        when(clienteRepository.buscarAniversariantesDoDia(MonthDay.from(hoje))).thenReturn(List.of(aniversariante));
        when(pedidoRepository.listarPorDataEntrega(hoje, hoje)).thenReturn(List.of(pedido));
        when(clienteRepository.buscarPorId(aniversariante.id())).thenReturn(Optional.of(aniversariante));
        when(produtoRepository.buscarPorId(produto.id())).thenReturn(Optional.of(produto));
        when(whatsAppNotificationPort.enviarTexto(any(), any())).thenReturn(true);

        var resultado = useCase.executar(hoje);

        assertTrue(resultado.notificacaoEnviada());
        assertEquals(1, resultado.aniversariantes());
        assertEquals(1, resultado.entregas());
        ArgumentCaptor<String> mensagem = ArgumentCaptor.forClass(String.class);
        verify(whatsAppNotificationPort).enviarTexto(eq("+5531989319621"), mensagem.capture());
        assertTrue(mensagem.getValue().contains("*Aniversariantes de Hoje:*"));
        assertTrue(mensagem.getValue().contains("*Entregas Agendadas para Hoje:*"));
        assertTrue(mensagem.getValue().contains("LAVANDA 100G"));
    }

    @Test
    void enviaSoAniversarios() {
        Cliente aniversariante = Cliente.novo("Helena", "31988887777", "Ipatinga", "MG", LocalDate.of(1992, 11, 24));
        when(clienteRepository.buscarAniversariantesDoDia(MonthDay.from(hoje))).thenReturn(List.of(aniversariante));
        when(pedidoRepository.listarPorDataEntrega(hoje, hoje)).thenReturn(List.of());
        when(whatsAppNotificationPort.enviarTexto(any(), any())).thenReturn(false);

        var resultado = useCase.executar(hoje);

        assertTrue(resultado.notificacaoEnviada());
        assertEquals(0, resultado.entregas());
        assertFalse(resultado.entreguePelaApi());
    }

    @Test
    void enviaSoEntregas() {
        Cliente cliente = Cliente.novo("Lia", "31999990000", "Ipatinga", "MG", LocalDate.of(1990, 1, 2));
        var produto = velaComEstoque(2);
        Pedido pedido = pedidoDe(cliente, produto.id(), hoje);
        when(clienteRepository.buscarAniversariantesDoDia(MonthDay.from(hoje))).thenReturn(List.of());
        when(pedidoRepository.listarPorDataEntrega(hoje, hoje)).thenReturn(List.of(pedido));
        when(clienteRepository.buscarPorId(cliente.id())).thenReturn(Optional.of(cliente));
        when(produtoRepository.buscarPorId(produto.id())).thenReturn(Optional.of(produto));
        when(whatsAppNotificationPort.enviarTexto(any(), any())).thenReturn(true);

        var resultado = useCase.executar(hoje);

        assertTrue(resultado.notificacaoEnviada());
        assertEquals(0, resultado.aniversariantes());
        assertEquals(1, resultado.entregas());
    }

    @Test
    void naoEnviaQuandoODiaEstaVazio() {
        when(clienteRepository.buscarAniversariantesDoDia(MonthDay.from(hoje))).thenReturn(List.of());
        when(pedidoRepository.listarPorDataEntrega(hoje, hoje)).thenReturn(List.of());

        var resultado = useCase.executar(hoje);

        assertFalse(resultado.notificacaoEnviada());
        verify(whatsAppNotificationPort, never()).enviarTexto(any(), any());
    }

    private static Pedido pedidoDe(Cliente cliente, UUID produtoId, LocalDate dataEntrega) {
        return Pedido.reconstituir(
                UUID.randomUUID(),
                cliente.id(),
                LocalDate.of(2026, 11, 20),
                StatusPedido.CONFIRMADO,
                NaturezaPedido.VENDA,
                null,
                null,
                dataEntrega,
                List.of(new ItemPedido(produtoId, 2, brl("32.90"), brl("16.45"))));
    }
}
