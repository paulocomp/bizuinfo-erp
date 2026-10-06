package com.bizuinfo.venda.dao;

import com.bizuinfo.infra.dao.GenericoDAO;
import com.bizuinfo.venda.model.ItemVenda;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ItemVendaDAO extends GenericoDAO<ItemVenda> {

    public ItemVendaDAO() {
        super(ItemVenda.class);
    }

}