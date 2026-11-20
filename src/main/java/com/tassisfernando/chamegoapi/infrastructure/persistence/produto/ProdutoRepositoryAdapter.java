package com.tassisfernando.chamegoapi.infrastructure.persistence.produto;

import com.tassisfernando.chamegoapi.domain.compartilhado.Dinheiro;
import com.tassisfernando.chamegoapi.domain.compartilhado.Quantidade;
import com.tassisfernando.chamegoapi.domain.compartilhado.UnidadeMedida;
import com.tassisfernando.chamegoapi.domain.produto.CategoriaProduto;
import com.tassisfernando.chamegoapi.domain.produto.ItemReceita;
import com.tassisfernando.chamegoapi.domain.compartilhado.Pagina;
import com.tassisfernando.chamegoapi.domain.produto.Produto;
import com.tassisfernando.chamegoapi.domain.produto.ProdutoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public class ProdutoRepositoryAdapter implements ProdutoRepository {

    private final ProdutoJpaRepository jpaRepository;
    private final com.tassisfernando.chamegoapi.infrastructure.persistence.pedido.ItemPedidoJpaRepository
            itemPedidoJpaRepository;
    private final com.tassisfernando.chamegoapi.infrastructure.persistence.producao.LoteProducaoJpaRepository
            loteProducaoJpaRepository;

    public ProdutoRepositoryAdapter(
            ProdutoJpaRepository jpaRepository,
            com.tassisfernando.chamegoapi.infrastructure.persistence.pedido.ItemPedidoJpaRepository
                    itemPedidoJpaRepository,
            com.tassisfernando.chamegoapi.infrastructure.persistence.producao.LoteProducaoJpaRepository
                    loteProducaoJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.itemPedidoJpaRepository = itemPedidoJpaRepository;
        this.loteProducaoJpaRepository = loteProducaoJpaRepository;
    }

    @Override
    public Produto salvar(Produto produto) {
        return paraDominio(jpaRepository.save(paraJpa(produto)));
    }

    @Override
    public Optional<Produto> buscarPorId(UUID id) {
        return jpaRepository.buscarComReceita(id).map(this::paraDominio);
    }

    @Override
    public List<Produto> listarAtivos() {
        return jpaRepository.listarAtivosComReceita().stream().map(this::paraDominio).toList();
    }

    @Override
    public Pagina<Produto> listarAtivos(int pagina, int tamanho) {
        Page<ProdutoJpaEntity> page =
                jpaRepository.findByAtivoTrue(PageRequest.of(pagina, tamanho, Sort.by("nome").ascending()));
        return mapearPaginaComReceita(page);
    }

    @Override
    public Pagina<Produto> buscarAtivos(String busca, int pagina, int tamanho) {
        String termo = busca == null ? "" : busca.trim();
        if (termo.isBlank()) {
            return listarAtivos(pagina, tamanho);
        }
        Page<ProdutoJpaEntity> page = jpaRepository.buscarAtivos(
                termo, PageRequest.of(pagina, tamanho, Sort.by("nome").ascending()));
        return mapearPaginaComReceita(page);
    }

    private Pagina<Produto> mapearPaginaComReceita(Page<ProdutoJpaEntity> page) {
        List<Produto> conteudo = page.getContent().stream()
                .map(entidade -> jpaRepository.buscarComReceita(entidade.getId()).orElse(entidade))
                .map(this::paraDominio)
                .toList();
        return Pagina.de(conteudo, page.getNumber(), page.getSize(), page.getTotalElements());
    }

    @Override
    public List<Produto> salvarTodos(List<Produto> produtos) {
        return jpaRepository.saveAll(produtos.stream().map(this::paraJpa).toList()).stream()
                .map(this::paraDominio)
                .toList();
    }

    @Override
    public void excluir(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean possuiReferencias(UUID id) {
        return itemPedidoJpaRepository.existsByProdutoId(id) || loteProducaoJpaRepository.existsByProdutoId(id);
    }

    private ProdutoJpaEntity paraJpa(Produto produto) {
        ProdutoJpaEntity entidade =
                jpaRepository.buscarComReceita(produto.id()).orElseGet(ProdutoJpaEntity::new);
        entidade.setId(produto.id());
        entidade.setNome(produto.nome());
        entidade.setDescricao(produto.descricao());
        entidade.setCategoria(produto.categoria().name());
        entidade.setPesoGramas(produto.pesoGramas());
        entidade.setPrecoVenda(produto.precoVenda().valor());
        entidade.setCustoPadrao(produto.custoPadrao().valor());
        entidade.setEstoqueAtual(produto.estoqueAtual());
        entidade.setAtivo(produto.ativo());
        sincronizarReceita(entidade, produto.receita());
        return entidade;
    }

    /**
     * Atualiza a BOM sem apagar e recriar linhas: evita INSERT duplicado em {@code uq_produto_insumo}
     * quando o produto é salvo após produção/pedido.
     */
    private static void sincronizarReceita(ProdutoJpaEntity entidade, List<ItemReceita> receitaDominio) {
        Map<UUID, ProdutoInsumoJpaEntity> linhasPorInsumo = new HashMap<>();
        for (ProdutoInsumoJpaEntity existente : entidade.getReceita()) {
            linhasPorInsumo.put(existente.getInsumoId(), existente);
        }
        Set<UUID> insumosDesejados = new HashSet<>();
        for (ItemReceita item : receitaDominio) {
            insumosDesejados.add(item.insumoId());
            ProdutoInsumoJpaEntity linha = linhasPorInsumo.get(item.insumoId());
            if (linha == null) {
                linha = new ProdutoInsumoJpaEntity();
                linha.setId(UUID.randomUUID());
                linha.setProduto(entidade);
                entidade.getReceita().add(linha);
                linhasPorInsumo.put(item.insumoId(), linha);
            }
            linha.setInsumoId(item.insumoId());
            linha.setQuantidade(item.quantidadePorUnidade().valor());
            linha.setUnidadeMedida(item.quantidadePorUnidade().unidade().name());
        }
        entidade.getReceita().removeIf(l -> !insumosDesejados.contains(l.getInsumoId()));
    }

    private Produto paraDominio(ProdutoJpaEntity entidade) {
        List<ItemReceita> receita = entidade.getReceita().stream()
                .map(item -> new ItemReceita(
                        item.getInsumoId(),
                        Quantidade.de(
                                item.getQuantidade(), UnidadeMedida.valueOf(item.getUnidadeMedida()))))
                .toList();
        return Produto.reconstituir(
                entidade.getId(),
                entidade.getNome(),
                entidade.getDescricao(),
                CategoriaProduto.valueOf(entidade.getCategoria()),
                entidade.getPesoGramas(),
                Dinheiro.de(entidade.getPrecoVenda()),
                Dinheiro.de(entidade.getCustoPadrao()),
                entidade.getEstoqueAtual(),
                entidade.isAtivo(),
                receita);
    }
}
