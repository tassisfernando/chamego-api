package com.tassisfernando.chamegoapi.infrastructure.persistence.pedido;

import com.tassisfernando.chamegoapi.domain.compartilhado.Dinheiro;
import com.tassisfernando.chamegoapi.domain.compartilhado.Pagina;
import com.tassisfernando.chamegoapi.domain.pedido.ItemPedido;
import com.tassisfernando.chamegoapi.domain.pedido.NaturezaPedido;
import com.tassisfernando.chamegoapi.domain.pedido.Pedido;
import com.tassisfernando.chamegoapi.domain.pedido.PedidoRepository;
import com.tassisfernando.chamegoapi.domain.pedido.StatusPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PedidoRepositoryAdapter implements PedidoRepository {

    private final PedidoJpaRepository jpaRepository;

    public PedidoRepositoryAdapter(PedidoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Pedido salvar(Pedido pedido) {
        return paraDominio(jpaRepository.save(paraJpa(pedido)));
    }

    @Override
    public Optional<Pedido> buscarPorId(UUID id) {
        return jpaRepository.buscarComItens(id).map(this::paraDominio);
    }

    @Override
    public List<Pedido> listarPorPeriodo(LocalDate inicio, LocalDate fim) {
        return jpaRepository.listarPorPeriodo(inicio, fim).stream().map(this::paraDominio).toList();
    }

    @Override
    public Pagina<Pedido> listarPorPeriodo(LocalDate inicio, LocalDate fim, int pagina, int tamanho) {
        Page<PedidoJpaEntity> page = jpaRepository.listarPorPeriodo(
                inicio, fim, PageRequest.of(pagina, tamanho, Sort.by("dataPedido").descending()));
        List<Pedido> conteudo = page.getContent().stream()
                .map(entidade -> jpaRepository.buscarComItens(entidade.getId()).orElse(entidade))
                .map(this::paraDominio)
                .toList();
        return Pagina.de(conteudo, page.getNumber(), page.getSize(), page.getTotalElements());
    }

    @Override
    public List<Pedido> listarPorCliente(UUID clienteId) {
        return jpaRepository.findByClienteId(clienteId).stream().map(this::paraDominio).toList();
    }

    private PedidoJpaEntity paraJpa(Pedido pedido) {
        PedidoJpaEntity entidade = jpaRepository.findById(pedido.id()).orElseGet(PedidoJpaEntity::new);
        entidade.setId(pedido.id());
        entidade.setClienteId(pedido.clienteId());
        entidade.setDataPedido(pedido.dataPedido());
        entidade.setStatus(pedido.status().name());
        entidade.setNatureza(pedido.natureza().name());
        entidade.setObservacao(pedido.observacao());
        entidade.getItens().clear();
        for (ItemPedido item : pedido.itens()) {
            ItemPedidoJpaEntity linha = new ItemPedidoJpaEntity();
            linha.setId(UUID.randomUUID());
            linha.setPedido(entidade);
            linha.setProdutoId(item.produtoId());
            linha.setQuantidade(item.quantidade());
            linha.setPrecoUnitario(item.precoUnitario().valor());
            linha.setCustoUnitario(item.custoUnitario().valor());
            entidade.getItens().add(linha);
        }
        return entidade;
    }

    private Pedido paraDominio(PedidoJpaEntity entidade) {
        List<ItemPedido> itens = entidade.getItens().stream()
                .map(item -> new ItemPedido(
                        item.getProdutoId(),
                        item.getQuantidade(),
                        Dinheiro.de(item.getPrecoUnitario()),
                        Dinheiro.de(item.getCustoUnitario())))
                .toList();
        return Pedido.reconstituir(
                entidade.getId(),
                entidade.getClienteId(),
                entidade.getDataPedido(),
                StatusPedido.valueOf(entidade.getStatus()),
                NaturezaPedido.valueOf(entidade.getNatureza()),
                entidade.getObservacao(),
                itens);
    }
}
