package com.tassisfernando.chamegoapi.infrastructure.web.compartilhado;

import com.tassisfernando.chamegoapi.domain.compartilhado.Pagina;

import java.util.List;

public record PaginaResposta<T>(
        List<T> conteudo,
        int pagina,
        int tamanho,
        long totalElementos,
        int totalPaginas) {

    public static <T> PaginaResposta<T> de(Pagina<T> pagina) {
        return new PaginaResposta<>(
                pagina.conteudo(),
                pagina.pagina(),
                pagina.tamanho(),
                pagina.totalElementos(),
                pagina.totalPaginas());
    }

    public static <T, R> PaginaResposta<R> mapear(Pagina<T> pagina, java.util.function.Function<T, R> mapper) {
        return new PaginaResposta<>(
                pagina.conteudo().stream().map(mapper).toList(),
                pagina.pagina(),
                pagina.tamanho(),
                pagina.totalElementos(),
                pagina.totalPaginas());
    }
}
