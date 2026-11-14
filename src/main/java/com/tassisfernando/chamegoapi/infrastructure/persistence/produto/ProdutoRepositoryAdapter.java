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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ProdutoRepositoryAdapter implements ProdutoRepository {

    private final ProdutoJpaRepository jpaRepository;

    public ProdutoRepositoryAdapter(ProdutoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
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

    private ProdutoJpaEntity paraJpa(Produto produto) {
        ProdutoJpaEntity entidade = jpaRepository.findById(produto.id()).orElseGet(ProdutoJpaEntity::new);
        entidade.setId(produto.id());
        entidade.setNome(produto.nome());
        entidade.setDescricao(produto.descricao());
        entidade.setCategoria(produto.categoria().name());
        entidade.setPesoGramas(produto.pesoGramas());
        entidade.setPrecoVenda(produto.precoVenda().valor());
        entidade.setCustoPadrao(produto.custoPadrao().valor());
        entidade.setEstoqueAtual(produto.estoqueAtual());
        entidade.setAtivo(produto.ativo());
        entidade.getReceita().clear();
        for (ItemReceita item : produto.receita()) {
            ProdutoInsumoJpaEntity linha = new ProdutoInsumoJpaEntity();
            linha.setId(UUID.randomUUID());
            linha.setProduto(entidade);
            linha.setInsumoId(item.insumoId());
            linha.setQuantidade(item.quantidadePorUnidade().valor());
            linha.setUnidadeMedida(item.quantidadePorUnidade().unidade().name());
            entidade.getReceita().add(linha);
        }
        return entidade;
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
