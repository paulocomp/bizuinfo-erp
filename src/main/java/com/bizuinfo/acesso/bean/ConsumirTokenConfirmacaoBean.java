package com.bizuinfo.acesso.bean;

import jakarta.inject.Inject;
import com.bizuinfo.acesso.service.ConsumirTokenConfirmacaoService;
import com.bizuinfo.usuario.model.Usuario;
import com.bizuinfo.web.Paginas;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import java.io.Serializable;

@Named
@RequestScoped
public class ConsumirTokenConfirmacaoBean implements Serializable {

    @Inject
    private ConsumirTokenConfirmacaoService consumirTokenService;

    private String token;

    public String consumir() {

        Usuario usuario = consumirTokenService.validarToken(token);

        if (usuario == null) {
            return Paginas.ACESSO_NEGADO + "?faces-redirect=true";
        }

        FacesContext.getCurrentInstance()
                .getExternalContext()
                .getSessionMap()
                .put(SessaoBean.ATRIBUTO_USUARIO, usuario);

        return switch (usuario.getRole()) {

            case ADMIN -> Paginas.DASHBOARD_ADMIN
                    + "?faces-redirect=true";

            case GERENTE -> Paginas.DASHBOARD_GERENTE
                    + "?faces-redirect=true";

            default -> Paginas.DASHBOARD_FUNCIONARIO
                    + "?faces-redirect=true";
        };
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}