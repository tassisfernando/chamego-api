package com.tassisfernando.chamegoapi.domain.compartilhado;

import java.util.List;

public record Pagina<T>(List<T> conteudo, int pagina, int tamanho, long totalElementos) {

    public int totalPaginas() {
        if (tamanho <= 0) {
            return 0;
        }
        return (int) Math.ceil((double) totalElementos / (double) tamanho);
    }

    public static <T> Pagina<T> de(List<T> conteudo, int pagina, int tamanho, long totalElementos) {
        return new Pagina<>(List.copyOf(conteudo), pagina, tamanho, totalElementos);
    }
}
