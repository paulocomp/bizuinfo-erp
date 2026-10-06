package com.bizuinfo.venda.dto;

import com.bizuinfo.produto.model.Produto;

public class ProdutoVendaDTO {

    private Produto produto;
    private int quantidade = 1;

    public ProdutoVendaDTO() {
    }

    public ProdutoVendaDTO(Produto produto) {
        this.produto = produto;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
}