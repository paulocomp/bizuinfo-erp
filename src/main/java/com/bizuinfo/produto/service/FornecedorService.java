package com.bizuinfo.produto.service;

import com.bizuinfo.produto.dao.FornecedorDAO;
import com.bizuinfo.produto.model.Fornecedor;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.List;

@Stateless
public class FornecedorService {

    @Inject
    private com.bizuinfo.produto.dao.FornecedorDAO fornecedorDAO;

    public void salvarOuAtualizar(Fornecedor fornecedor) {
        fornecedorDAO.salvar(fornecedor);
    }

    public void remover(Long id) {
        fornecedorDAO.remover(id);
    }

    public List<Fornecedor> listarTodos() {
        return fornecedorDAO.listarTodos();
    }
}