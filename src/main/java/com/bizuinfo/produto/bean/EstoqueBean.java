package com.bizuinfo.produto.bean;

import com.bizuinfo.acesso.bean.SessaoBean;
import com.bizuinfo.produto.dao.ProdutoDAO;
import com.bizuinfo.produto.model.Produto;
import com.bizuinfo.produto.service.EstoqueService;
import com.bizuinfo.produto.service.ProdutoFiltroService;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class EstoqueBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ProdutoDAO produtoDAO;

    @Inject
    private SessaoBean sessaoBean;

    @EJB
    private ProdutoFiltroService produtoFiltroService;

    @EJB
    private EstoqueService estoqueService;

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
        produtos = produtoDAO.listarTodos();
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
                    sessaoBean.getUsuarioLogado()
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