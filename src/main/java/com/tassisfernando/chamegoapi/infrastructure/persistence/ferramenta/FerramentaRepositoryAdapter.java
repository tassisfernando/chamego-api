package com.tassisfernando.chamegoapi.infrastructure.persistence.ferramenta;

import com.tassisfernando.chamegoapi.domain.compartilhado.Dinheiro;
import com.tassisfernando.chamegoapi.domain.compartilhado.UnidadeMedida;
import com.tassisfernando.chamegoapi.domain.ferramenta.Ferramenta;
import com.tassisfernando.chamegoapi.domain.ferramenta.FerramentaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FerramentaRepositoryAdapter implements FerramentaRepository {

    private final FerramentaJpaRepository jpaRepository;

    public FerramentaRepositoryAdapter(FerramentaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Ferramenta salvar(Ferramenta ferramenta) {
        return paraDominio(jpaRepository.save(paraJpa(ferramenta)));
    }

    @Override
    public Optional<Ferramenta> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(this::paraDominio);
    }

    @Override
    public List<Ferramenta> listarAtivas() {
        return jpaRepository.findByAtivoTrue().stream().map(this::paraDominio).toList();
    }

    private FerramentaJpaEntity paraJpa(Ferramenta ferramenta) {
        FerramentaJpaEntity entidade = new FerramentaJpaEntity();
        entidade.setId(ferramenta.id());
        entidade.setNome(ferramenta.nome());
        entidade.setQuantidade(ferramenta.quantidade());
        entidade.setUnidadeMedida(ferramenta.unidadeMedida().name());
        entidade.setValorAquisicao(ferramenta.valorAquisicao().valor());
        entidade.setDataCompra(ferramenta.dataCompra());
        entidade.setObservacao(ferramenta.observacao());
        entidade.setAtivo(ferramenta.ativo());
        return entidade;
    }

    private Ferramenta paraDominio(FerramentaJpaEntity entidade) {
        return Ferramenta.reconstituir(
                entidade.getId(),
                entidade.getNome(),
                entidade.getQuantidade(),
                UnidadeMedida.valueOf(entidade.getUnidadeMedida()),
                Dinheiro.de(entidade.getValorAquisicao()),
                entidade.getDataCompra(),
                entidade.getObservacao(),
                entidade.isAtivo());
    }
}
