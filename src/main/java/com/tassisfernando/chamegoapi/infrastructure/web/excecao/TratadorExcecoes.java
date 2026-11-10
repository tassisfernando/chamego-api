package com.tassisfernando.chamegoapi.infrastructure.web.excecao;

import com.tassisfernando.chamegoapi.domain.compartilhado.EntidadeNaoEncontrada;
import com.tassisfernando.chamegoapi.domain.compartilhado.RegraDeNegocioViolada;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class TratadorExcecoes {

    @ExceptionHandler(EntidadeNaoEncontrada.class)
    public ResponseEntity<ErroResposta> naoEncontrado(EntidadeNaoEncontrada excecao) {
        return resposta(HttpStatus.NOT_FOUND, excecao.getMessage());
    }

    @ExceptionHandler(RegraDeNegocioViolada.class)
    public ResponseEntity<ErroResposta> regra(RegraDeNegocioViolada excecao) {
        return resposta(HttpStatus.UNPROCESSABLE_ENTITY, excecao.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResposta> validacao(MethodArgumentNotValidException excecao) {
        String mensagem = excecao.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .orElse("requisição inválida");
        return resposta(HttpStatus.BAD_REQUEST, mensagem);
    }

    private ResponseEntity<ErroResposta> resposta(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status)
                .body(new ErroResposta(Instant.now(), status.value(), mensagem));
    }
}
