package com.bizuinfo.venda.model;

import com.bizuinfo.produto.model.Produto;
import jakarta.persistence.*;

@Entity
@Table(name = "itemvenda")
public class ItemVenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "iditemVenda")
    private Long id;

    @Column(name = "quantidade", nullable = false)
    private int quantidade;

    @Column(name = "valorUnitario", nullable = false)
    private double valorUnitario;

    @Column(name = "subtotal", nullable = false)
    private double subtotal;

    @ManyToOne
    @JoinColumn(name = "venda_idvenda")
    private Venda venda;

    @ManyToOne
    @JoinColumn(name = "produto_idproduto")
    private Produto produto;

    public ItemVenda() {
    }

    public Long getId() {
        return id;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public double getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(double valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public Venda getVenda() {
        return venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }
}