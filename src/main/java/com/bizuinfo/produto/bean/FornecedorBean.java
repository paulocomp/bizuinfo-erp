package com.bizuinfo.produto.bean;

import com.bizuinfo.produto.model.Fornecedor;
import com.bizuinfo.produto.service.FornecedorService;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB; // <- REGRA APLICADA: Injeção do EJB
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped; // <- CDI para a tela
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class FornecedorBean implements Serializable {

    @EJB
    private FornecedorService fornecedorService;

    private List<Fornecedor> fornecedores;
    private Fornecedor fornecedorSelecionado;

    @PostConstruct
    public void init() {
        fornecedores = fornecedorService.listarTodos();
        fornecedorSelecionado = new Fornecedor();
    }

    public void prepararNovo() {
        fornecedorSelecionado = new Fornecedor();
    }

    public void salvar() {
        try {
            fornecedorService.salvarOuAtualizar(fornecedorSelecionado);
            fornecedores = fornecedorService.listarTodos(); // Recarrega a tabela

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Fornecedor salvo com sucesso."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Não foi possível salvar."));
        }
    }

    public void remover(Fornecedor f) {
        try {
            fornecedorService.remover(f.getId());
            fornecedores.remove(f);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Fornecedor removido."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Este fornecedor possui produtos vinculados."));
        }
    }

    public List<Fornecedor> getFornecedores() { return fornecedores; }
    public Fornecedor getFornecedorSelecionado() { return fornecedorSelecionado; }
    public void setFornecedorSelecionado(Fornecedor fornecedorSelecionado) { this.fornecedorSelecionado = fornecedorSelecionado; }
}