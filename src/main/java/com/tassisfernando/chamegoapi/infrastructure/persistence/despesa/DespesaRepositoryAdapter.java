package com.tassisfernando.chamegoapi.infrastructure.persistence.despesa;

import com.tassisfernando.chamegoapi.domain.compartilhado.Dinheiro;
import com.tassisfernando.chamegoapi.domain.compartilhado.Quantidade;
import com.tassisfernando.chamegoapi.domain.compartilhado.UnidadeMedida;
import com.tassisfernando.chamegoapi.domain.despesa.Despesa;
import com.tassisfernando.chamegoapi.domain.despesa.DespesaRepository;
import com.tassisfernando.chamegoapi.domain.despesa.TipoDespesa;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class DespesaRepositoryAdapter implements DespesaRepository {

    private final DespesaJpaRepository jpaRepository;

    public DespesaRepositoryAdapter(DespesaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Despesa salvar(Despesa despesa) {
        return paraDominio(jpaRepository.save(paraJpa(despesa)));
    }

    @Override
    public Optional<Despesa> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(this::paraDominio);
    }

    @Override
    public List<Despesa> listarPorPeriodo(LocalDate inicio, LocalDate fim) {
        return jpaRepository.findByDataDespesaBetween(inicio, fim).stream()
                .map(this::paraDominio)
                .toList();
    }

    private DespesaJpaEntity paraJpa(Despesa despesa) {
        DespesaJpaEntity entidade = new DespesaJpaEntity();
        entidade.setId(despesa.id());
        entidade.setDescricao(despesa.descricao());
        entidade.setDataDespesa(despesa.dataDespesa());
        entidade.setValor(despesa.valor().valor());
        entidade.setQuantidade(despesa.quantidade() == null ? null : despesa.quantidade().valor());
        entidade.setUnidadeMedida(
                despesa.quantidade() == null ? null : despesa.quantidade().unidade().name());
        entidade.setTipo(despesa.tipo().name());
        entidade.setInsumoId(despesa.insumoId());
        entidade.setFerramentaId(despesa.ferramentaId());
        entidade.setObservacao(despesa.observacao());
        return entidade;
    }

    private Despesa paraDominio(DespesaJpaEntity entidade) {
        Quantidade quantidade = entidade.getQuantidade() == null || entidade.getUnidadeMedida() == null
                ? null
                : Quantidade.de(
                        entidade.getQuantidade(), UnidadeMedida.valueOf(entidade.getUnidadeMedida()));
        return Despesa.reconstituir(
                entidade.getId(),
                entidade.getDescricao(),
                entidade.getDataDespesa(),
                Dinheiro.de(entidade.getValor()),
                quantidade,
                TipoDespesa.valueOf(entidade.getTipo()),
                entidade.getInsumoId(),
                entidade.getFerramentaId(),
                entidade.getObservacao());
    }
}
