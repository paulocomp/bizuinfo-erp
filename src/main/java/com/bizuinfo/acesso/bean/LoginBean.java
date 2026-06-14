package com.bizuinfo.acesso.bean;

import com.bizuinfo.acesso.dto.LoginResultado;
import com.bizuinfo.acesso.service.LoginService;
import com.bizuinfo.usuario.model.Usuario;
import com.bizuinfo.usuario.service.LogAuditoriaService;
import com.bizuinfo.web.Paginas;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;

@Named
@SessionScoped
public class LoginBean implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String email;
    private String senha;

    @EJB
    private LoginService loginService;

    public String entrar() {

        LoginResultado resultado = loginService.autenticar(email, senha);

        switch (resultado.getResultado()) {

            case EMAIL_NAO_ENCONTRADO,
                 SENHA_INVALIDA -> {

                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_ERROR,
                                "Email ou senha inválidos",
                                null
                        )
                );

                return null;
            }

            case EMAIL_NAO_CONFIRMADO -> {
                return Paginas.CONFIRMAR_EMAIL
                        + "?faces-redirect=true";
            }

            case SUCESSO -> {

                Usuario usuario = resultado.getUsuario();

                FacesContext.getCurrentInstance()
                        .getExternalContext()
                        .getSessionMap()
                        .put("usuario", usuario);

                return switch (usuario.getRole()) {

                    case ADMIN -> Paginas.DASHBOARD_ADMIN
                            + "?faces-redirect=true";

                    case GERENTE -> Paginas.DASHBOARD_GERENTE
                            + "?faces-redirect=true";

                    default -> Paginas.DASHBOARD_FUNCIONARIO
                            + "?faces-redirect=true";
                };
            }
        }

        return null;
    }

    @EJB
    private LogAuditoriaService logAuditoriaService;

    @Inject
    private SessaoBean sessaoBean;

    public String sair() {

        Usuario usuario = getUsuarioLogado();

        if (usuario != null) {

            logAuditoriaService.registrar(
                    "LOGOUT",
                    "Usuário saiu do sistema",
                    usuario.getNome()
            );
        }

        usuario = null;
        sessaoBean.logout();

        FacesContext.getCurrentInstance()
                .getExternalContext()
                .invalidateSession();

        return Paginas.LOGIN + "?faces-redirect=true";
    }


    public boolean logado() {
        return getUsuarioLogado() != null;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public Usuario getUsuarioLogado() {

        FacesContext context = FacesContext.getCurrentInstance();

        if (context == null) {
            return null;
        }

        return (Usuario) context
                .getExternalContext()
                .getSessionMap()
                .get("usuario");
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

}