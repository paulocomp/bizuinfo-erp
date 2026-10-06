package com.bizuinfo.produto.service;

import com.bizuinfo.infra.persistencia.Transacional;
import jakarta.enterprise.context.ApplicationScoped;
import com.bizuinfo.produto.dao.FornecedorDAO;
import com.bizuinfo.produto.model.Fornecedor;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transacional
public class FornecedorService {

    @Inject
    private com.bizuinfo.produto.dao.FornecedorDAO fornecedorDAO;

    public void salvarOuAtualizar(Fornecedor fornecedor) {
        fornecedorDAO.salvar(fornecedor);
    }

    public void remover(Long id) {
        fornecedorDAO.remover(id);
    }

    public Optional<Fornecedor> buscarPorId(Long id) {
        return fornecedorDAO.buscarPorId(id);
    }

    public List<Fornecedor> listarTodos() {
        return fornecedorDAO.listarTodos();
    }
}