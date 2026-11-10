package com.tassisfernando.chamegoapi.infrastructure.persistence.insumo;

import com.tassisfernando.chamegoapi.domain.compartilhado.Dinheiro;
import com.tassisfernando.chamegoapi.domain.compartilhado.Quantidade;
import com.tassisfernando.chamegoapi.domain.compartilhado.UnidadeMedida;
import com.tassisfernando.chamegoapi.domain.insumo.CategoriaInsumo;
import com.tassisfernando.chamegoapi.domain.insumo.Insumo;
import com.tassisfernando.chamegoapi.domain.insumo.InsumoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class InsumoRepositoryAdapter implements InsumoRepository {

    private final InsumoJpaRepository jpaRepository;

    public InsumoRepositoryAdapter(InsumoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Insumo salvar(Insumo insumo) {
        return paraDominio(jpaRepository.save(paraJpa(insumo)));
    }

    @Override
    public Optional<Insumo> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(this::paraDominio);
    }

    @Override
    public List<Insumo> listarAtivos() {
        return jpaRepository.findByAtivoTrue().stream().map(this::paraDominio).toList();
    }

    @Override
    public List<Insumo> salvarTodos(List<Insumo> insumos) {
        return jpaRepository.saveAll(insumos.stream().map(this::paraJpa).toList()).stream()
                .map(this::paraDominio)
                .toList();
    }

    private InsumoJpaEntity paraJpa(Insumo insumo) {
        InsumoJpaEntity entidade = new InsumoJpaEntity();
        entidade.setId(insumo.id());
        entidade.setNome(insumo.nome());
        entidade.setCategoria(insumo.categoria().name());
        entidade.setMarca(insumo.marca());
        entidade.setUnidadeMedida(insumo.unidadeMedida().name());
        entidade.setEstoqueAtual(insumo.estoqueAtual().valor());
        entidade.setCustoMedio(insumo.custoMedio().valor());
        entidade.setAtivo(insumo.ativo());
        return entidade;
    }

    private Insumo paraDominio(InsumoJpaEntity entidade) {
        UnidadeMedida unidade = UnidadeMedida.valueOf(entidade.getUnidadeMedida());
        return Insumo.reconstituir(
                entidade.getId(),
                entidade.getNome(),
                CategoriaInsumo.valueOf(entidade.getCategoria()),
                entidade.getMarca(),
                unidade,
                Quantidade.de(entidade.getEstoqueAtual(), unidade),
                Dinheiro.de(entidade.getCustoMedio()),
                entidade.isAtivo());
    }
}
