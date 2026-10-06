package com.bizuinfo.venda.dao;

import com.bizuinfo.infra.dao.GenericoDAO;
import com.bizuinfo.venda.model.Pagamento;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PagamentoDAO extends GenericoDAO<Pagamento> {

    public PagamentoDAO() {
        super(Pagamento.class);
    }

}