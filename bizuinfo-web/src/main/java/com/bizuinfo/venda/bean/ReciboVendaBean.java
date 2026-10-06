package com.bizuinfo.venda.bean;

import com.bizuinfo.usuario.service.UsuarioLogado;
import com.bizuinfo.venda.model.Venda;
import com.bizuinfo.venda.service.VendaPDFService;
import com.bizuinfo.venda.service.VendaService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.Map;

@Named
@ViewScoped
public class ReciboVendaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private UsuarioLogado usuarioLogado;

    @Inject
    private VendaService vendaService;

    @Inject
    private VendaPDFService vendaPDFService;

    private Venda venda;

    @PostConstruct
    public void init() {

        try {
            Map<String, String> params =
                    FacesContext.getCurrentInstance()
                            .getExternalContext()
                            .getRequestParameterMap();

            Long vendaId = Long.parseLong(params.get("vendaId"));

            this.venda = vendaService.buscarReciboPara(vendaId, usuarioLogado.getUsuarioLogado());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Venda getVenda() {  return venda;  }

    public void baixarPdf() {

        try {

            if (venda == null) {
                throw new RuntimeException("Venda não encontrada.");
            }

            byte[] pdf = vendaPDFService.gerarRecibo(venda);

            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();

            externalContext.responseReset();
            externalContext.setResponseContentType("application/pdf");
            externalContext.setResponseHeader(
                    "Content-Disposition",
                    "attachment; filename=recibo_venda_" + venda.getId() + ".pdf"
            );

            try (OutputStream output =
                         externalContext.getResponseOutputStream()) {

                output.write(pdf);
                output.flush();
            }

            facesContext.responseComplete();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}