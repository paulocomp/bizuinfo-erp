package com.bizuinfo.infra.persistencia;

import com.bizuinfo.infra.util.JPAutil;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Um EntityManager por requisição, compartilhado por todos os DAOs.
 *
 * Assim, várias operações feitas por um service (ex.: baixar estoque e
 * registrar a venda) entram na mesma transação, controlada por
 * {@link TransacionalInterceptor}.
 */
@RequestScoped
public class ContextoPersistencia {

    private static final Logger LOG = Logger.getLogger(ContextoPersistencia.class.getName());

    private EntityManager em;

    private final List<Runnable> acoesAposCommit = new ArrayList<>();

    public EntityManager em() {
        if (em == null) {
            em = JPAutil.getEntityManager();
        }
        return em;
    }

    public boolean emTransacao() {
        return em != null && em.getTransaction().isActive();
    }

    /**
     * Para operações de escrita: falha cedo se o DAO for chamado fora de
     * um método {@link Transacional}, em vez de a alteração nunca ser gravada.
     */
    public EntityManager emTransacional() {
        if (!emTransacao()) {
            throw new IllegalStateException(
                    "Escrita no banco fora de transação. Chame o DAO a partir de um service @Transacional.");
        }
        return em;
    }

    /**
     * Agenda algo que só deve acontecer se a transação atual for confirmada,
     * como enviar um email. Se houver rollback, a ação é descartada.
     */
    public void aposCommit(Runnable acao) {
        acoesAposCommit.add(acao);
    }

    void executarAcoesAposCommit() {

        List<Runnable> acoes = new ArrayList<>(acoesAposCommit);
        acoesAposCommit.clear();

        for (Runnable acao : acoes) {
            try {
                acao.run();
            } catch (Exception e) {
                // A transação já foi confirmada: uma falha aqui (ex.: SMTP fora do ar)
                // não pode desfazer a operação nem virar erro para o usuário.
                LOG.log(Level.WARNING, "Falha em ação pós-commit", e);
            }
        }
    }

    void descartarAcoesAposCommit() {
        acoesAposCommit.clear();
    }

    @PreDestroy
    void fechar() {
        if (em != null && em.isOpen()) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }
}
