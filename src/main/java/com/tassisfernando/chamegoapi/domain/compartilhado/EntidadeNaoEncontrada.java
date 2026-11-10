package com.tassisfernando.chamegoapi.domain.compartilhado;

public final class EntidadeNaoEncontrada extends ExcecaoDeDominio {

    public EntidadeNaoEncontrada(String mensagem) {
        super(mensagem);
    }

    public static EntidadeNaoEncontrada de(String entidade, Object id) {
        return new EntidadeNaoEncontrada(entidade + " não encontrado(a): " + id);
    }
}
