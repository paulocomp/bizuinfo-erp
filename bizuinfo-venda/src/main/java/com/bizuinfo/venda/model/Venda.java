package com.bizuinfo.venda.model;

import com.bizuinfo.usuario.model.Usuario;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "venda")
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idvenda")
    private Long id;

    @Column(name = "dataVenda", nullable = false)
    private LocalDateTime dataVenda;

    @Column(name = "valorTotal", nullable = false)
    private double valorTotal;

    @OneToMany(
            mappedBy = "venda",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ItemVenda> itens = new ArrayList<>();

    @OneToOne(mappedBy = "venda",
            cascade = CascadeType.ALL)
    private Pagamento pagamento;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Venda() {
    }

    public Long getId() { return id; }

    public LocalDateTime getDataVenda() {  return dataVenda;  }

    public void setDataVenda(LocalDateTime dataVenda) {  this.dataVenda = dataVenda;  }

    public double getValorTotal() {  return valorTotal;  }

    public void setValorTotal(double valorTotal) {  this.valorTotal = valorTotal;  }

    public List<ItemVenda> getItens() {  return itens;  }

    public void setItens(List<ItemVenda> itens) {  this.itens = itens;  }

    public Usuario getUsuario() {  return usuario;  }

    public void setUsuario(Usuario usuario) {  this.usuario = usuario;  }

    public Pagamento getPagamento() {  return pagamento;  }

    public void setPagamento(Pagamento pagamento) {  this.pagamento = pagamento;  }

    public String getDataVendaFormatada() {
        if (dataVenda == null) {
            return "";
        }

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        return dataVenda.format(formatter);
    }
}