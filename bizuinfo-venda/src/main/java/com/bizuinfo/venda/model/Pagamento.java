package com.bizuinfo.venda.model;

import jakarta.persistence.*;

@Entity
@Table(name = "pagamento")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idpagamento")
    private Long id;

    @Column(name = "valor", nullable = false)
    private double valor;

    @Enumerated(EnumType.STRING)
    @Column(name = "formaPagamento", nullable = false)
    private FormaPagamento formaPagamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "statusPagamento", nullable = false)
    private StatusPagamento statusPagamento;

    @OneToOne
    @JoinColumn(name = "venda_idvenda")
    private Venda venda;

    public Pagamento() {
    }

    public Long getId() {
        return id;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public StatusPagamento getStatusPagamento() {
        return statusPagamento;
    }

    public void setStatusPagamento(StatusPagamento statusPagamento) {
        this.statusPagamento = statusPagamento;
    }

    public Venda getVenda() {
        return venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }
}