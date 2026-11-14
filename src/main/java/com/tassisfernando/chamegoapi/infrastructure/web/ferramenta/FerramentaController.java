package com.tassisfernando.chamegoapi.infrastructure.web.ferramenta;

import com.tassisfernando.chamegoapi.domain.compartilhado.Dinheiro;
import com.tassisfernando.chamegoapi.domain.compartilhado.UnidadeMedida;
import com.tassisfernando.chamegoapi.domain.ferramenta.Ferramenta;
import com.tassisfernando.chamegoapi.domain.ferramenta.FerramentaRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/ferramentas")
public class FerramentaController {

    private final FerramentaRepository ferramentaRepository;

    public FerramentaController(FerramentaRepository ferramentaRepository) {
        this.ferramentaRepository = ferramentaRepository;
    }

    @GetMapping
    public List<FerramentaResposta> listar() {
        return ferramentaRepository.listarAtivas().stream().map(FerramentaResposta::de).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FerramentaResposta cadastrar(@Valid @RequestBody Requisicao requisicao) {
        Ferramenta ferramenta = Ferramenta.nova(
                requisicao.nome(),
                requisicao.quantidade() == null ? BigDecimal.ONE : requisicao.quantidade(),
                requisicao.unidadeMedida() == null ? UnidadeMedida.UN : requisicao.unidadeMedida(),
                Dinheiro.de(requisicao.valorAquisicao() == null ? BigDecimal.ZERO : requisicao.valorAquisicao()),
                requisicao.dataCompra(),
                requisicao.observacao());
        return FerramentaResposta.de(ferramentaRepository.salvar(ferramenta));
    }

    public record Requisicao(
            @NotBlank String nome,
            BigDecimal quantidade,
            UnidadeMedida unidadeMedida,
            BigDecimal valorAquisicao,
            LocalDate dataCompra,
            String observacao) {
    }

    public record FerramentaResposta(
            UUID id,
            String nome,
            BigDecimal quantidade,
            UnidadeMedida unidadeMedida,
            BigDecimal valorAquisicao,
            LocalDate dataCompra,
            String observacao,
            boolean ativo) {

        static FerramentaResposta de(Ferramenta ferramenta) {
            return new FerramentaResposta(
                    ferramenta.id(),
                    ferramenta.nome(),
                    ferramenta.quantidade(),
                    ferramenta.unidadeMedida(),
                    ferramenta.valorAquisicao().valor(),
                    ferramenta.dataCompra(),
                    ferramenta.observacao(),
                    ferramenta.ativo());
        }
    }
}
