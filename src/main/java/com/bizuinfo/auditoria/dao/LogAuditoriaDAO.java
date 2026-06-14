package com.bizuinfo.auditoria.dao;

import com.bizuinfo.infra.util.JPAutil;
import com.bizuinfo.auditoria.model.LogAuditoria;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.util.List;

@ApplicationScoped
public class LogAuditoriaDAO {

    public void salvar(LogAuditoria log) {

        EntityManager em = JPAutil.getEntityManager();

        try {
            em.getTransaction().begin();

            em.persist(log);

            em.getTransaction().commit();

        } catch (Exception e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new RuntimeException("Erro ao enviar email", e);
        } finally {
            em.close();
        }
    }

    public List<LogAuditoria> listarTodos() {

        try (EntityManager em = JPAutil.getEntityManager()) {
            return em.createQuery(
                    "SELECT l FROM LogAuditoria l ORDER BY l.dataHora DESC",
                    LogAuditoria.class
            ).getResultList();

        }
    }
}