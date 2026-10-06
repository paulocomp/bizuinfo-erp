package com.bizuinfo.produto.service;

import com.bizuinfo.infra.persistencia.Transacional;
import jakarta.enterprise.context.ApplicationScoped;
import com.bizuinfo.produto.dao.CategoriaDAO;
import com.bizuinfo.produto.dao.ProdutoDAO;
import com.bizuinfo.produto.model.Categoria;
import com.bizuinfo.produto.model.Produto;
import com.bizuinfo.produto.dto.SugestaoCompraDTO;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transacional
public class CategoriaService {
    @Inject
    private CategoriaDAO categoriaDAO;

    @Inject
    private ProdutoDAO produtoDAO;

    public Optional<Categoria> buscarPorId(Long id) {
        return categoriaDAO.buscarPorId(id);
    }

    public List<Categoria> listarTodos() {
        return categoriaDAO.listarTodos();
    }

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