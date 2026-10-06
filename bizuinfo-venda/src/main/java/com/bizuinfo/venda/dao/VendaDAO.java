package com.bizuinfo.venda.dao;

import com.bizuinfo.infra.dao.GenericoDAO;
import com.bizuinfo.venda.model.Venda;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class VendaDAO extends GenericoDAO<Venda> {

    public VendaDAO() {
        super(Venda.class);
    }

    public Optional<Venda> buscarCompletamente(Long id) {

        EntityManager em = em();

        try {

            Venda venda = em.createQuery("""
                SELECT v FROM Venda v
                LEFT JOIN FETCH v.itens i
                LEFT JOIN FETCH i.produto
                LEFT JOIN FETCH v.pagamento
                LEFT JOIN FETCH v.usuario
                WHERE v.id = :id
            """, Venda.class)
                    .setParameter("id", id)
                    .getSingleResult();

            return Optional.of(venda);

        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<Venda> buscarPorUsuario(Long usuarioId) {

        EntityManager em = em();

        return em.createQuery("""
        SELECT DISTINCT v FROM Venda v
        LEFT JOIN FETCH v.itens i
        LEFT JOIN FETCH i.produto
        LEFT JOIN FETCH v.pagamento
        WHERE v.usuario.id = :usuarioId
        ORDER BY v.dataVenda DESC
    """, Venda.class)
                .setParameter("usuarioId", usuarioId)
                .getResultList();
    }

    public List<Venda> buscarVendasParaDashboard(Long usuarioId) {

        EntityManager em = em();


        String jpql = """
        SELECT DISTINCT v
        FROM Venda v
        LEFT JOIN FETCH v.itens i
        LEFT JOIN FETCH i.produto
        LEFT JOIN FETCH v.pagamento
        LEFT JOIN FETCH v.usuario
    """;

        if (usuarioId != null) {
            jpql += " WHERE v.usuario.id = :usuarioId";
        }

        jpql += " ORDER BY v.dataVenda DESC";

        var query = em.createQuery(jpql, Venda.class);

        if (usuarioId != null) {
            query.setParameter("usuarioId", usuarioId);
        }

        return query.getResultList();
    }

    public List<Venda> buscarTodas() {

        EntityManager em = em();

        return em.createQuery("""
        SELECT DISTINCT v FROM Venda v
        LEFT JOIN FETCH v.itens i
        LEFT JOIN FETCH i.produto
        LEFT JOIN FETCH v.pagamento
        LEFT JOIN FETCH v.usuario
        ORDER BY v.dataVenda DESC
    """, Venda.class).getResultList();
    }

    public List<Venda> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {

        EntityManager em = em();


        return em.createQuery("""
        SELECT DISTINCT v
        FROM Venda v
        LEFT JOIN FETCH v.itens i
        LEFT JOIN FETCH i.produto
        LEFT JOIN FETCH v.pagamento
        LEFT JOIN FETCH v.usuario
        WHERE v.dataVenda BETWEEN :inicio AND :fim
        ORDER BY v.dataVenda DESC
    """, Venda.class)
                .setParameter("inicio", inicio)
                .setParameter("fim", fim)
                .getResultList();
    }
}