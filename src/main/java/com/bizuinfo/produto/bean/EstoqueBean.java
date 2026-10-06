package com.bizuinfo.produto.bean;

import com.bizuinfo.usuario.service.UsuarioLogado;
import com.bizuinfo.produto.service.ProdutoService;
import com.bizuinfo.produto.model.Produto;
import com.bizuinfo.produto.service.EstoqueService;
import com.bizuinfo.produto.service.ProdutoFiltroService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class EstoqueBean implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Inject
    private UsuarioLogado usuarioLogado;

    @Inject
    private ProdutoFiltroService produtoFiltroService;

    @Inject
    private EstoqueService estoqueService;

    @Inject
    private ProdutoService produtoService;

    private List<Produto> produtos;
    private List<Produto> produtosFiltrados;

    private String filtro;
    private Produto produtoSelecionado;
    private int quantidadeMovimentacao;

    @PostConstruct
    public void init() {
        carregarProdutos();
    }

    public void carregarProdutos() {
        produtos = produtoService.listarTodos();
        produtosFiltrados = produtos;
    }

    public void prepararMovimentacao(Produto p) {
        this.produtoSelecionado = p;
        this.quantidadeMovimentacao = 0;
    }

    public void salvarMovimentacao() {

        try {
            estoqueService.movimentarEstoque(
                    produtoSelecionado.getId(),
                    quantidadeMovimentacao,
                    usuarioLogado.getUsuarioLogado()
            );

            carregarProdutos();
            filtrar();

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_INFO,
                            "Sucesso",
                            "Estoque atualizado com sucesso."
                    )
            );

        } catch (Exception e) {

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Erro",
                            "Não foi possível atualizar o estoque."
                    )
            );
        }
    }

    public void filtrar() {

        produtosFiltrados = produtoFiltroService.filtrarPorNomeOuCategoria(
                produtos,
                filtro
        );
    }

    public void excluirProduto(Produto produto) {

        try {

            produtoService.remover(produto.getId());

            carregarProdutos();
            filtrar();

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_INFO,
                            "Sucesso",
                            "Produto removido com sucesso."
                    )
            );

        } catch (Exception e) {

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Erro",
                            "Não foi possível remover o produto."
                    )
            );
        }
    }

    // Getters e Setters
    public List<Produto> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<Produto> produtos) {
        this.produtos = produtos;
    }

    public Produto getProdutoSelecionado() {
        return produtoSelecionado;
    }

    public void setProdutoSelecionado(Produto produtoSelecionado) {
        this.produtoSelecionado = produtoSelecionado;
    }

    public int getQuantidadeMovimentacao() {
        return quantidadeMovimentacao;
    }

    public void setQuantidadeMovimentacao(int quantidadeMovimentacao) {
        this.quantidadeMovimentacao = quantidadeMovimentacao;
    }

    public List<Produto> getProdutosFiltrados() {
        return produtosFiltrados;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

}