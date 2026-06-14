package com.bizuinfo.venda.dto;

import com.bizuinfo.produto.model.Produto;

public class SugestaoCompraDTO {

    private Produto produto;
    private int demandaUltimos30Dias;
    private int estoqueProjetado;
    private int quantidadeSugerida;

    public SugestaoCompraDTO(Produto produto, int demandaUltimos30Dias, int estoqueProjetado, int quantidadeSugerida) {
        this.produto = produto;
        this.demandaUltimos30Dias = demandaUltimos30Dias;
        this.estoqueProjetado = estoqueProjetado;
        this.quantidadeSugerida = quantidadeSugerida;
    }

    public Produto getProduto() {return produto;}
    public int getDemandaUltimos30Dias() {return demandaUltimos30Dias;}
    public int getEstoqueProjetado() { return estoqueProjetado; }
    public int getQuantidadeSugerida() { return quantidadeSugerida; }
}
