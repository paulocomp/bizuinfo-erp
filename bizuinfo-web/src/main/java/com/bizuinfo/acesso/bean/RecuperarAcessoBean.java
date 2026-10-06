package com.bizuinfo.acesso.bean;

import jakarta.inject.Inject;
import com.bizuinfo.acesso.service.RecuperarAcessoService;
import com.bizuinfo.web.Paginas;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;

@Named
@SessionScoped
public class RecuperarAcessoBean implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String email;

    private boolean linkEnviado = false;

    private long ultimoReenvio = 0;
    private static final long INTERVALO = 30000;

    @Inject
    private RecuperarAcessoService recuperarAcessoService;

    public void enviarLink() {

        ultimoReenvio = System.currentTimeMillis();

        recuperarAcessoService.enviarLink(email, Paginas.urlAbsoluta(Paginas.CONSUMIR_TOKEN_RECUPERACAO));

        linkEnviado = true;

        FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(
                        FacesMessage.SEVERITY_INFO,
                        "Link enviado",
                        "Verifique sua caixa de entrada."
                )
        );
    }

    public void reenviarLink() {

        if (getContadorAtivo()) {
            return;
        }

        ultimoReenvio = System.currentTimeMillis();

        recuperarAcessoService.enviarLink(email, Paginas.urlAbsoluta(Paginas.CONSUMIR_TOKEN_RECUPERACAO));

        FacesContext.getCurrentInstance().addMessage(
            null,
            new FacesMessage(
                FacesMessage.SEVERITY_INFO,
                "Link reenviado",
                "Verifique sua caixa de entrada."
            )
        );
    }

    public int getSegundosRestantes() {

        long restante = INTERVALO - (System.currentTimeMillis() - ultimoReenvio);
        return Math.max(0, (int) (restante / 1000));
    }

    public boolean getContadorAtivo() {
        return (System.currentTimeMillis() - ultimoReenvio) < INTERVALO;
    }

    public String getEmail() {
        return email;
    }

    public boolean isLinkEnviado() {
        return linkEnviado;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLinkEnviado(boolean linkEnviado) {
        this.linkEnviado = linkEnviado;
    }
}