package com.tassisfernando.chamegoapi.domain.compartilhado;

public sealed class ExcecaoDeDominio extends RuntimeException
        permits EntidadeNaoEncontrada, RegraDeNegocioViolada {

    public ExcecaoDeDominio(String mensagem) {
        super(mensagem);
    }
}
