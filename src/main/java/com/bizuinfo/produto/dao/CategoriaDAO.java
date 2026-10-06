package com.bizuinfo.produto.dao;

import com.bizuinfo.infra.dao.GenericoDAO;
import com.bizuinfo.produto.model.Categoria;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CategoriaDAO extends GenericoDAO<Categoria> {

    public CategoriaDAO() {
        super(Categoria.class);
    }

    public Optional<Categoria> buscarPorNome(String nome) {

        try {
            EntityManager em = em();

            Categoria c = em.createQuery(
                            "SELECT c FROM Categoria c WHERE c.nome = :nome",
                            Categoria.class
                    )
                    .setParameter("nome", nome)
                    .getSingleResult();

            return Optional.of(c);

        } catch (NoResultException e) {

            return Optional.empty();
        }
    }

    @Override
    public List<Categoria> listarTodos() {

        EntityManager em = em();

        return em.createQuery(
                "SELECT c FROM Categoria c",
                Categoria.class
        ).getResultList();
    }
}