package com.bizuinfo.produto.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "fornecedor")
public class Fornecedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idfornecedor")
    private Long id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "cnpj", length = 20)
    private String cnpj;

    @Column(name = "telefone", length = 20)
    private String telefone;

    @Column(name = "email", length = 100)
    private String email;

    @OneToMany(mappedBy = "fornecedor")
    private List<Produto> produtosFornecidos = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<Produto> getProdutosFornecidos() { return produtosFornecidos; }
    public void setProdutosFornecidos(List<Produto> produtosFornecidos) { this.produtosFornecidos = produtosFornecidos; }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof Fornecedor fornecedor)) return false;

        return id != null && id.equals(fornecedor.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}