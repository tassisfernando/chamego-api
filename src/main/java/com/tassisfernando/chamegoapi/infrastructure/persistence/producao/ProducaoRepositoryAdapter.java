package com.tassisfernando.chamegoapi.infrastructure.persistence.producao;

import com.tassisfernando.chamegoapi.domain.compartilhado.Dinheiro;
import com.tassisfernando.chamegoapi.domain.compartilhado.Quantidade;
import com.tassisfernando.chamegoapi.domain.compartilhado.UnidadeMedida;
import com.tassisfernando.chamegoapi.domain.producao.ItemConsumoProducao;
import com.tassisfernando.chamegoapi.domain.producao.LoteProducao;
import com.tassisfernando.chamegoapi.domain.producao.ProducaoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ProducaoRepositoryAdapter implements ProducaoRepository {

    private final LoteProducaoJpaRepository jpaRepository;

    public ProducaoRepositoryAdapter(LoteProducaoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public LoteProducao salvar(LoteProducao lote) {
        return paraDominio(jpaRepository.save(paraJpa(lote)));
    }

    @Override
    public Optional<LoteProducao> buscarPorId(UUID id) {
        return jpaRepository.buscarComConsumos(id).map(this::paraDominio);
    }

    @Override
    public List<LoteProducao> listarPorPeriodo(LocalDate inicio, LocalDate fim) {
        return jpaRepository.listarPorPeriodo(inicio, fim).stream().map(this::paraDominio).toList();
    }

    private LoteProducaoJpaEntity paraJpa(LoteProducao lote) {
        LoteProducaoJpaEntity entidade = new LoteProducaoJpaEntity();
        entidade.setId(lote.id());
        entidade.setProdutoId(lote.produtoId());
        entidade.setDataProducao(lote.dataProducao());
        entidade.setQuantidadeProduzida(lote.quantidadeProduzida());
        entidade.setCustoUnitario(lote.custoUnitario().valor());
        entidade.setCustoTotal(lote.custoTotal().valor());
        entidade.setObservacao(lote.observacao());
        for (ItemConsumoProducao consumo : lote.consumos()) {
            ItemConsumoProducaoJpaEntity linha = new ItemConsumoProducaoJpaEntity();
            linha.setId(UUID.randomUUID());
            linha.setLote(entidade);
            linha.setInsumoId(consumo.insumoId());
            linha.setQuantidade(consumo.quantidade().valor());
            linha.setUnidadeMedida(consumo.quantidade().unidade().name());
            linha.setCustoUnitario(consumo.custoUnitario().valor());
            entidade.getConsumos().add(linha);
        }
        return entidade;
    }

    private LoteProducao paraDominio(LoteProducaoJpaEntity entidade) {
        List<ItemConsumoProducao> consumos = entidade.getConsumos().stream()
                .map(item -> new ItemConsumoProducao(
                        item.getInsumoId(),
                        Quantidade.de(item.getQuantidade(), UnidadeMedida.valueOf(item.getUnidadeMedida())),
                        Dinheiro.de(item.getCustoUnitario())))
                .toList();
        return LoteProducao.reconstituir(
                entidade.getId(),
                entidade.getProdutoId(),
                entidade.getDataProducao(),
                entidade.getQuantidadeProduzida(),
                consumos,
                entidade.getObservacao());
    }
}
