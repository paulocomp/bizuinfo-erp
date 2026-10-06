package com.bizuinfo.infra.persistencia;

import jakarta.interceptor.InterceptorBinding;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Executa o método (ou todos os métodos da classe) dentro de uma transação.
 *
 * Se já houver uma transação aberta nesta requisição, o método participa dela;
 * só quem abriu a transação faz commit ou rollback. Qualquer exceção desfaz tudo.
 *
 * @see TransacionalInterceptor
 */
@Inherited
@InterceptorBinding
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Transacional {
}
