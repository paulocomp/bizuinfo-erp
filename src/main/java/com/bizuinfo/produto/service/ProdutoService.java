package com.bizuinfo.produto.service;

import com.bizuinfo.produto.dao.ProdutoDAO;
import com.bizuinfo.produto.model.Produto;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.List;

@Stateless
public class ProdutoService {

    @Inject
    private ProdutoDAO produtoDAO;

    public List<Produto> listarTodos() {
        return produtoDAO.listarTodos();
    }

    public void salvar(Produto produto) {
        produtoDAO.salvar(produto);
    }

    public void remover(Long idProduto) {
        produtoDAO.remover(idProduto);
    }
}
