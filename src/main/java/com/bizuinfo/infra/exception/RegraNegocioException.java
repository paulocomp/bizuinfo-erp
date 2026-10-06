package com.bizuinfo.infra.exception;

import jakarta.ejb.ApplicationException;

/**
 * Violação de uma regra de negócio, com mensagem pronta para mostrar ao usuário.
 *
 * Marcada como {@link ApplicationException} para que o container EJB não a
 * embrulhe em EJBException: o bean recebe esta exceção como foi lançada.
 */
@ApplicationException(rollback = true)
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
