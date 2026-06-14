package com.bizuinfo.produto.model;

import jakarta.persistence.*;

@Entity
@Table(name = "produto")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idproduto")
    private Long id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "preco")
    private double preco;

    @Column(name = "estoqueAtual")
    private int estoqueAtual;

    @Column(name = "estoqueMinimo", columnDefinition = "int default 5")
    private int estoqueMinimo;

    @Column(name = "descricao")
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "fornecedor_idfornecedor")
    private Fornecedor fornecedor;

    @ManyToOne
    @JoinColumn(name = "categoria_categoria_id")
    private Categoria categoria;

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public int getEstoqueAtual() {
        return estoqueAtual;
    }

    public void setEstoqueAtual(int estoqueAtual) {
        this.estoqueAtual = estoqueAtual;
    }

    public int getEstoqueMinimo() {
        return estoqueMinimo;
    }

    public void setEstoqueMinimo(int estoqueMinimo) {
        this.estoqueMinimo = estoqueMinimo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Fornecedor getFornecedor() {return fornecedor;}
    public void setFornecedor(Fornecedor fornecedor) {this.fornecedor = fornecedor;}

    public Categoria getCategoria() {
        return categoria;
    }
    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
}