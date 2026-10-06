package com.bizuinfo.infra.persistencia;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.io.Serializable;

@Transacional
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class TransacionalInterceptor implements Serializable {

    @Inject
    private ContextoPersistencia contexto;

    @AroundInvoke
    public Object executar(InvocationContext ic) throws Exception {

        if (contexto.emTransacao()) {
            return ic.proceed();
        }

        EntityManager em = contexto.em();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        Object resultado;

        try {
            resultado = ic.proceed();
            tx.commit();

        } catch (Exception e) {

            if (tx.isActive()) {
                tx.rollback();
            }

            contexto.descartarAcoesAposCommit();
            throw e;

        } finally {
            // Entidades devolvidas pelo service saem desanexadas,
            // como acontecia quando cada DAO abria e fechava seu EntityManager.
            em.clear();
        }

        contexto.executarAcoesAposCommit();

        return resultado;
    }
}
