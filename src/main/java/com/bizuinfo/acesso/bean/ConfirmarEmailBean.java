package com.bizuinfo.acesso.bean;

import com.bizuinfo.acesso.service.ConfirmarEmailService;
import com.bizuinfo.web.Paginas;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;

@Named
@SessionScoped
public class ConfirmarEmailBean implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String email;

    private long ultimoReenvio = 0;
    private static final long INTERVALO = 5000;

    @EJB
    private ConfirmarEmailService confirmarEmailService;

    public void confirmarEmail() {
        confirmarEmailService.enviarLink(email, Paginas.urlAbsoluta(Paginas.CONSUMIR_TOKEN_CONFIRMACAO));
    }

    public void reenviarLink() {

        if (getContadorAtivo()) {
            return;
        }

        ultimoReenvio = System.currentTimeMillis();

        confirmarEmailService.enviarLink(email, Paginas.urlAbsoluta(Paginas.CONSUMIR_TOKEN_CONFIRMACAO));

        FacesContext.getCurrentInstance().addMessage(
            null,
            new FacesMessage(
                FacesMessage.SEVERITY_INFO,
                "Email reenviado",
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

    public void setEmail(String email) {
        this.email = email;
    }
}