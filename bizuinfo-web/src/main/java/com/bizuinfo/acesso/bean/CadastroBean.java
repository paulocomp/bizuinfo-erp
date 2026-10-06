package com.bizuinfo.acesso.bean;

import com.bizuinfo.usuario.service.UsuarioService;
import com.bizuinfo.web.Paginas;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;

@Named
@SessionScoped
public class CadastroBean implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String nome;
    private String email;
    private String senha;

    @Inject
    private ConfirmarEmailBean confirmarEmailBean;

    @Inject
    private UsuarioService usuarioService;

    public String cadastrar() {

        boolean cadastrado = usuarioService.cadastrar(nome, email, senha);

        if (!cadastrado) {

            FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Email já cadastrado",
                    null
                )
            );

            return null;
        }

        confirmarEmailBean.setEmail(email);
        confirmarEmailBean.confirmarEmail();

        return Paginas.CONFIRMAR_EMAIL + "?faces-redirect=true";
    }

    // getters/setters
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }

    public void setNome(String nome) { this.nome = nome; }
    public void setEmail(String email) { this.email = email; }
    public void setSenha(String senha) { this.senha = senha; }
}