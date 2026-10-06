package com.bizuinfo.venda.service;

import com.bizuinfo.infra.persistencia.Transacional;
import jakarta.enterprise.context.ApplicationScoped;
import com.bizuinfo.infra.persistencia.ContextoPersistencia;
import com.bizuinfo.produto.dao.ProdutoDAO;
import com.bizuinfo.produto.model.Produto;
import com.bizuinfo.produto.dto.SugestaoCompraDTO;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@Transacional
public class CompraAutomaticaService {

    @Inject
    private ProdutoDAO produtoDAO;

    @Inject
    private ContextoPersistencia contexto;

    public List<SugestaoCompraDTO> gerarSugestoesDeCompra() {
        List<SugestaoCompraDTO> sugestoes = new ArrayList<>();
        List<Produto> todosProdutos = produtoDAO.listarTodos();

        LocalDateTime trintaDiasAtras = LocalDateTime.now().minusDays(30);

        EntityManager em = contexto.em();

        for (Produto p : todosProdutos) {
            Long demanda = em.createQuery(
                            "SELECT SUM(iv.quantidade) FROM ItemVenda iv WHERE iv.produto.id = :idProduto AND iv.venda.dataVenda >= :dataCorte", Long.class)
                    .setParameter("idProduto", p.getId())
                    .setParameter("dataCorte", trintaDiasAtras)
                    .getSingleResult();

            int demanda30d = (demanda != null) ? demanda.intValue() : 0;
            int estoqueProjetado = p.getEstoqueAtual() - demanda30d;

            if (estoqueProjetado <= p.getEstoqueMinimo()) {

                int quantidadeParaComprar = (demanda30d + p.getEstoqueMinimo()) - p.getEstoqueAtual();

                if (quantidadeParaComprar > 0) {
                    sugestoes.add(new SugestaoCompraDTO(p, demanda30d, estoqueProjetado, quantidadeParaComprar));
                }
            }
        }


        return sugestoes;
    }
}