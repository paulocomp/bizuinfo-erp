package com.bizuinfo.venda.bean;

import com.bizuinfo.produto.dao.ProdutoDAO;
import com.bizuinfo.produto.model.Produto;
import com.bizuinfo.venda.dto.ProdutoVendaDTO;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class VendaBean implements Serializable {

    @Inject
    private ProdutoDAO produtoDAO;

    @Inject
    private CarrinhoBean carrinhoBean;

    private List<ProdutoVendaDTO> produtos;

    private List<ProdutoVendaDTO> produtosFiltrados;

    private String filtro;

    @PostConstruct
    public void init() {
        carregarProdutos();
        produtosFiltrados = List.copyOf(produtos);
    }

    private void carregarProdutos() {

        produtos = produtoDAO.listarTodos()
                .stream()
                .map(ProdutoVendaDTO::new)
                .collect(Collectors.toList());
    }

    public void adicionarAoCarrinho(
            ProdutoVendaDTO dto
    ) {

        try {

            if (dto.getQuantidade() <= 0) {

                throw new RuntimeException(
                        "Quantidade inválida."
                );
            }

            carrinhoBean.adicionarProduto(
                    dto.getProduto(),
                    dto.getQuantidade()
            );

            dto.setQuantidade(1);

        } catch (Exception e) {

            FacesContext.getCurrentInstance()
                    .addMessage(
                            null,
                            new FacesMessage(
                                    FacesMessage.SEVERITY_ERROR,
                                    "Erro",
                                    e.getMessage()
                            )
                    );
        }
    }

    public void filtrar() {

        if (filtro == null || filtro.isBlank()) {

            produtosFiltrados = List.copyOf(produtos);
            return;
        }

        String termo = filtro.toLowerCase();

        produtosFiltrados = produtos.stream()
                .filter(dto -> {

                    Produto p = dto.getProduto();

                    boolean nomeMatch =
                            p.getNome() != null
                                    &&
                                    p.getNome()
                                            .toLowerCase()
                                            .contains(termo);

                    boolean categoriaMatch =
                            p.getCategoria() != null
                                    &&
                                    p.getCategoria().getNome() != null
                                    &&
                                    p.getCategoria()
                                            .getNome()
                                            .toLowerCase()
                                            .contains(termo);

                    return nomeMatch || categoriaMatch;
                })
                .toList();
    }

    public List<ProdutoVendaDTO> getProdutos() {
        return produtos;
    }

    public List<ProdutoVendaDTO> getProdutosFiltrados() {
        return produtosFiltrados;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

}
