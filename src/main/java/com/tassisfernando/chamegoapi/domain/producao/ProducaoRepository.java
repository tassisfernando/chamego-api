package com.tassisfernando.chamegoapi.domain.producao;

import com.tassisfernando.chamegoapi.domain.compartilhado.Pagina;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProducaoRepository {

    LoteProducao salvar(LoteProducao lote);

    Optional<LoteProducao> buscarPorId(UUID id);

    List<LoteProducao> listarPorPeriodo(LocalDate inicio, LocalDate fim);

    Pagina<LoteProducao> listarPorPeriodo(LocalDate inicio, LocalDate fim, int pagina, int tamanho);
}
