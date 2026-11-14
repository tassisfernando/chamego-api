package com.tassisfernando.chamegoapi.infrastructure.web.insumo;

import com.tassisfernando.chamegoapi.domain.compartilhado.UnidadeMedida;
import com.tassisfernando.chamegoapi.domain.insumo.CategoriaInsumo;
import com.tassisfernando.chamegoapi.domain.insumo.Insumo;
import com.tassisfernando.chamegoapi.domain.insumo.InsumoRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/insumos")
public class InsumoController {

    private final InsumoRepository insumoRepository;

    public InsumoController(InsumoRepository insumoRepository) {
        this.insumoRepository = insumoRepository;
    }

    @GetMapping
    public List<InsumoResposta> listar() {
        return insumoRepository.listarAtivos().stream().map(InsumoResposta::de).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InsumoResposta cadastrar(@Valid @RequestBody Requisicao requisicao) {
        Insumo insumo = Insumo.novo(
                requisicao.nome(),
                requisicao.categoria(),
                requisicao.marca(),
                requisicao.unidadeMedida());
        return InsumoResposta.de(insumoRepository.salvar(insumo));
    }

    public record Requisicao(
            @NotBlank String nome,
            @NotNull CategoriaInsumo categoria,
            String marca,
            @NotNull UnidadeMedida unidadeMedida) {
    }

    public record InsumoResposta(
            UUID id,
            String nome,
            CategoriaInsumo categoria,
            String marca,
            UnidadeMedida unidadeMedida,
            BigDecimal estoqueAtual,
            BigDecimal custoMedio,
            boolean ativo) {

        static InsumoResposta de(Insumo insumo) {
            return new InsumoResposta(
                    insumo.id(),
                    insumo.nome(),
                    insumo.categoria(),
                    insumo.marca(),
                    insumo.unidadeMedida(),
                    insumo.estoqueAtual().valor(),
                    insumo.custoMedio().valor(),
                    insumo.ativo());
        }
    }
}
