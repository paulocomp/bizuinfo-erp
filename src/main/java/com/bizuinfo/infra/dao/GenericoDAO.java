package com.bizuinfo.infra.dao;

import com.bizuinfo.infra.persistencia.ContextoPersistencia;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;
import java.util.Optional;

/**
 * Base dos DAOs. Não abre transação: usa o EntityManager da requisição,
 * e a transação é aberta pelo service (@Transacional).
 */
public abstract class GenericoDAO<T> {

    private final Class<T> classeEntidade;

    @Inject
    protected ContextoPersistencia contexto;

    protected GenericoDAO(Class<T> classeEntidade) {
        this.classeEntidade = classeEntidade;
    }

    /** EntityManager para consultas. */
    protected EntityManager em() {
        return contexto.em();
    }

    /** EntityManager para escritas; exige transação ativa. */
    protected EntityManager emEscrita() {
        return contexto.emTransacional();
    }

    public Optional<T> buscarPorId(Long id) {
        return Optional.ofNullable(em().find(classeEntidade, id));
    }

    public T salvar(T entidade) {
        EntityManager em = emEscrita();
        T gerenciada = em.merge(entidade);
        em.flush();
        return gerenciada;
    }

    /** Insere uma entidade nova (persist); ela passa a ter id depois do flush. */
    public void inserir(T entidade) {
        EntityManager em = emEscrita();
        em.persist(entidade);
        em.flush();
    }

    public void remover(Long id) {
        EntityManager em = emEscrita();
        em.remove(em.getReference(classeEntidade, id));
        em.flush();
    }

    public List<T> listarTodos() {

        CriteriaBuilder cb = em().getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(classeEntidade);
        Root<T> root = cq.from(classeEntidade);
        cq.select(root);
        return em().createQuery(cq).getResultList();
    }
}
