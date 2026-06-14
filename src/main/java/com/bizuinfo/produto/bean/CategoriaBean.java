package com.bizuinfo.produto.bean;

import com.bizuinfo.produto.dao.CategoriaDAO;
import com.bizuinfo.produto.model.Categoria;
import com.bizuinfo.produto.service.CategoriaService;
import com.bizuinfo.venda.dto.SugestaoCompraDTO;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class CategoriaBean implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Inject
    private CategoriaDAO categoriaDAO;

    @EJB
    private CategoriaService categoriaService;

    private Categoria categoriaSelecionada;
    private String novoNomeCategoria;
    private String nomeNovaCategoria;
    private List<Categoria> categorias;

    @PostConstruct
    public void init() {
        carregarCategorias();
    }

    private void carregarCategorias() {
        categorias = categoriaDAO.listarTodos();
    }

    public void prepararEdicao(Categoria categoria) {

        System.out.println("EDITAR: " + categoria.getId());

        this.categoriaSelecionada = categoria;
        this.novoNomeCategoria = categoria.getNome();
    }

    public void salvarEdicao() {

        try {

            categoriaService.atualizarNome(
                    categoriaSelecionada.getId(),
                    novoNomeCategoria
            );

            carregarCategorias();

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_INFO,
                            "Sucesso",
                            "Categoria atualizada."
                    )
            );

        } catch (Exception e) {

            e.printStackTrace();

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Erro",
                            e.getMessage()
                    )
            );
        }
    }

    public void prepararExclusao(Categoria categoria) {

        System.out.println(
                "PREPARAR EXCLUSÃO ID = "
                        + categoria.getId()
                        + " NOME = "
                        + categoria.getNome()
        );

        this.categoriaSelecionada = categoria;
    }

    public void excluirSelecionada() {

        try {

            if (categoriaSelecionada == null) {
                throw new RuntimeException("Nenhuma categoria selecionada.");
            }

            System.out.println("EXCLUINDO ID = " + categoriaSelecionada.getId());

            categoriaService.excluirCategoria(categoriaSelecionada.getId());
            carregarCategorias();
            categoriaSelecionada = null;

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_INFO,
                            "Sucesso",
                            "Categoria removida."
                    )
            );

        } catch (Exception e) {

            e.printStackTrace();

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Erro ao excluir",
                            e.getMessage()
                    )
            );
        }
    }

    public void prepararNovaCategoria() {
        nomeNovaCategoria = "";
    }

    public void salvarNovaCategoria() {

        try {

            categoriaService.criarCategoria(nomeNovaCategoria);
            carregarCategorias();
            nomeNovaCategoria = "";

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_INFO,
                            "Sucesso",
                            "Categoria criada."
                    )
            );

        } catch (Exception e) {

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Erro",
                            e.getMessage()
                    )
            );
        }
    }

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public Categoria getCategoriaSelecionada() {
        return categoriaSelecionada;
    }

    public String getNovoNomeCategoria() {
        return novoNomeCategoria;
    }

    public void setNovoNomeCategoria(String novoNomeCategoria) {
        this.novoNomeCategoria = novoNomeCategoria;
    }

    public String getNomeNovaCategoria() {
        return nomeNovaCategoria;
    }

    public void setNomeNovaCategoria(String nomeNovaCategoria) {
        this.nomeNovaCategoria = nomeNovaCategoria;
    }
}