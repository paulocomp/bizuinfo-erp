package com.bizuinfo.produto.dao;

import com.bizuinfo.infra.dao.GenericoDAO;
import com.bizuinfo.infra.util.JPAutil;
import com.bizuinfo.produto.model.Fornecedor;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.Optional;

@ApplicationScoped
public class FornecedorDAO extends GenericoDAO<Fornecedor> {

    public FornecedorDAO() {
        super(Fornecedor.class);
    }

    public Optional<Fornecedor> buscarPorCnpj(String cnpj) {
        try (EntityManager em = JPAutil.getEntityManager()) {

            Fornecedor f = em.createQuery(
                            "SELECT f FROM Fornecedor f WHERE f.cnpj = :cnpj",
                            Fornecedor.class
                    )
                    .setParameter("cnpj", cnpj)
                    .getSingleResult();

            return Optional.of(f);

        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}