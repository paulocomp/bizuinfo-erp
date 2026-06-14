package com.bizuinfo.produto.service;

import com.bizuinfo.produto.dao.CategoriaDAO;
import com.bizuinfo.produto.dao.ProdutoDAO;
import com.bizuinfo.produto.model.Categoria;
import com.bizuinfo.produto.model.Produto;
import com.bizuinfo.venda.dto.SugestaoCompraDTO;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class CategoriaService {
    @Inject
    private CategoriaDAO categoriaDAO;

    @Inject
    private ProdutoDAO produtoDAO;

    public void atualizarNome(Long categoriaId, String novoNome) {

        Categoria categoria = categoriaDAO
                .buscarPorId(categoriaId)
                .orElseThrow();

        categoria.setNome(novoNome);

        categoriaDAO.salvar(categoria);
    }

    public void excluirCategoria(Long categoriaId) {

        produtoDAO.removerCategoriaDosProdutos(categoriaId);
        categoriaDAO.remover(categoriaId);
    }

    public void criarCategoria(String nome) {

        if (nome == null || nome.isBlank()) {
            throw new RuntimeException("O nome da categoria é obrigatório.");
        }

        Categoria categoria = new Categoria();
        categoria.setNome(nome.trim());
        categoriaDAO.salvar(categoria);
    }

}