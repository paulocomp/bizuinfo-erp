package com.bizuinfo.infra.exception;

/**
 * Violação de uma regra de negócio, com mensagem pronta para mostrar ao usuário.
 *
 * Lançada pelos services; o @Transacional desfaz a transação e o bean
 * mostra {@link #getMessage()} na tela.
 */
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
