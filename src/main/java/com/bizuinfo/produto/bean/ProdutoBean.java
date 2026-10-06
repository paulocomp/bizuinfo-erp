package com.bizuinfo.produto.bean;

import jakarta.inject.Inject;
import com.bizuinfo.produto.service.ProdutoService;
import com.bizuinfo.produto.model.Produto;
import com.bizuinfo.web.Paginas;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;

@Named
@ViewScoped
public class ProdutoBean implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Inject
    private ProdutoService produtoService;

    private Produto produto;

    @PostConstruct
    public void init() {
        produto = new Produto();
    }

    private boolean salvo;

    public String salvar() {
        produtoService.salvar(produto);
        produto = new Produto();
        salvo = true;

        return Paginas.GERENCIAR_ESTOQUE;
    }

    public boolean isSalvo() {
        return salvo;
    }

    public void setProduto(Produto p) {
        produto = p;
    }

    public Produto getProduto() {
        return produto;
    }


}
