package com.tassisfernando.chamegoapi.infrastructure.persistence.precificacao;

import com.tassisfernando.chamegoapi.domain.compartilhado.Dinheiro;
import com.tassisfernando.chamegoapi.domain.compartilhado.Pagina;
import com.tassisfernando.chamegoapi.domain.compartilhado.UnidadeMedida;
import com.tassisfernando.chamegoapi.domain.precificacao.ItemPrecificacao;
import com.tassisfernando.chamegoapi.domain.precificacao.ModoCalculoMaterial;
import com.tassisfernando.chamegoapi.domain.precificacao.Precificacao;
import com.tassisfernando.chamegoapi.domain.precificacao.PrecificacaoRepository;
import com.tassisfernando.chamegoapi.domain.precificacao.TipoItemPrecificacao;
import com.tassisfernando.chamegoapi.domain.precificacao.TipoPrecificacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PrecificacaoRepositoryAdapter implements PrecificacaoRepository {

    private final PrecificacaoJpaRepository jpaRepository;

    public PrecificacaoRepositoryAdapter(PrecificacaoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Precificacao salvar(Precificacao precificacao) {
        return paraDominio(jpaRepository.save(paraJpa(precificacao)));
    }

    @Override
    public Optional<Precificacao> buscarPorId(UUID id) {
        return jpaRepository.buscarComItens(id).map(this::paraDominio);
    }

    @Override
    public Pagina<Precificacao> listar(int pagina, int tamanho) {
        Page<PrecificacaoJpaEntity> page = jpaRepository.findAllByOrderByCriadoEmDesc(PageRequest.of(pagina, tamanho));
        return Pagina.de(
                page.getContent().stream().map(this::paraDominioSemItens).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements());
    }

    @Override
    public void excluir(UUID id) {
        jpaRepository.deleteById(id);
    }

    private PrecificacaoJpaEntity paraJpa(Precificacao precificacao) {
        PrecificacaoJpaEntity entidade =
                jpaRepository.findById(precificacao.id()).orElseGet(PrecificacaoJpaEntity::new);
        entidade.setId(precificacao.id());
        entidade.setTitulo(precificacao.titulo());
        entidade.setTipo(precificacao.tipo().name());
        entidade.setProdutoReferenciaId(precificacao.produtoReferenciaId());
        entidade.setProdutoPublicadoId(precificacao.produtoPublicadoId());
        entidade.setMargemPercentual(precificacao.margemPercentual());
        entidade.setPrecoVenda(precificacao.precoVenda().valor());
        entidade.setCustoTotal(precificacao.custoTotal().valor());
        entidade.setLucroUnitario(precificacao.lucroUnitario().valor());
        entidade.setObservacao(precificacao.observacao());
        entidade.setCriadoEm(precificacao.criadoEm());
        entidade.setAtualizadoEm(precificacao.atualizadoEm());
        entidade.getItens().clear();
        for (ItemPrecificacao item : precificacao.itens()) {
            PrecificacaoItemJpaEntity linha = new PrecificacaoItemJpaEntity();
            linha.setId(item.id());
            linha.setPrecificacao(entidade);
            linha.setTipoItem(item.tipo().name());
            linha.setProdutoId(item.produtoId());
            linha.setInsumoId(item.insumoId());
            linha.setDescricao(item.descricao());
            linha.setQuantidade(item.quantidade());
            linha.setUnidadeMedida(
                    item.unidadeMedida() == null ? null : item.unidadeMedida().name());
            linha.setCustoUnitario(item.custoUnitario().valor());
            linha.setModoCalculo(
                    item.modoCalculo() == null ? ModoCalculoMaterial.UNITARIO.name() : item.modoCalculo().name());
            linha.setValorEmbalagem(
                    item.valorEmbalagem() == null ? null : item.valorEmbalagem().valor());
            linha.setQuantidadeEmbalagem(item.quantidadeEmbalagem());
            linha.setUnidadeEmbalagem(
                    item.unidadeEmbalagem() == null ? null : item.unidadeEmbalagem().name());
            linha.setSubtotal(item.subtotal().valor());
            entidade.getItens().add(linha);
        }
        return entidade;
    }

    private Precificacao paraDominio(PrecificacaoJpaEntity entidade) {
        return Precificacao.reconstituir(
                entidade.getId(),
                entidade.getTitulo(),
                TipoPrecificacao.valueOf(entidade.getTipo()),
                entidade.getProdutoReferenciaId(),
                entidade.getProdutoPublicadoId(),
                entidade.getMargemPercentual(),
                Dinheiro.de(entidade.getPrecoVenda()),
                Dinheiro.de(entidade.getCustoTotal()),
                Dinheiro.de(entidade.getLucroUnitario()),
                entidade.getObservacao(),
                entidade.getCriadoEm(),
                entidade.getAtualizadoEm(),
                mapearItens(entidade));
    }

    private Precificacao paraDominioSemItens(PrecificacaoJpaEntity entidade) {
        return Precificacao.reconstituir(
                entidade.getId(),
                entidade.getTitulo(),
                TipoPrecificacao.valueOf(entidade.getTipo()),
                entidade.getProdutoReferenciaId(),
                entidade.getProdutoPublicadoId(),
                entidade.getMargemPercentual(),
                Dinheiro.de(entidade.getPrecoVenda()),
                Dinheiro.de(entidade.getCustoTotal()),
                Dinheiro.de(entidade.getLucroUnitario()),
                entidade.getObservacao(),
                entidade.getCriadoEm(),
                entidade.getAtualizadoEm(),
                List.of());
    }

    private List<ItemPrecificacao> mapearItens(PrecificacaoJpaEntity entidade) {
        return entidade.getItens().stream()
                .map(item -> ItemPrecificacao.reconstituir(
                        item.getId(),
                        TipoItemPrecificacao.valueOf(item.getTipoItem()),
                        item.getProdutoId(),
                        item.getInsumoId(),
                        item.getDescricao(),
                        item.getModoCalculo() == null
                                ? ModoCalculoMaterial.UNITARIO
                                : ModoCalculoMaterial.valueOf(item.getModoCalculo()),
                        item.getQuantidade(),
                        item.getUnidadeMedida() == null
                                ? null
                                : UnidadeMedida.valueOf(item.getUnidadeMedida()),
                        Dinheiro.de(item.getCustoUnitario()),
                        item.getValorEmbalagem() == null ? null : Dinheiro.de(item.getValorEmbalagem()),
                        item.getQuantidadeEmbalagem(),
                        item.getUnidadeEmbalagem() == null
                                ? null
                                : UnidadeMedida.valueOf(item.getUnidadeEmbalagem())))
                .toList();
    }
}