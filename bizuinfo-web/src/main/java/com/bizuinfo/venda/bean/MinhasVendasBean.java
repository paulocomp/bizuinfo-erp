package com.bizuinfo.venda.bean;

import com.bizuinfo.usuario.service.UsuarioLogado;
import com.bizuinfo.usuario.model.Usuario;
import com.bizuinfo.venda.service.VendaService;
import com.bizuinfo.venda.model.Venda;
import com.bizuinfo.venda.service.VendaPDFService;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.OutputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.List;

@Named
@ViewScoped
public class MinhasVendasBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private UsuarioLogado usuarioLogado;

    @Inject
    private VendaService vendaService;

    @Inject
    private VendaPDFService vendaPDFService;

    private List<Venda> vendas;

    private LocalDate dataInicial;
    private LocalDate dataFinal;

    @PostConstruct
    public void init() {
        carregar();
    }

    public void carregar() {

        vendas = vendaService.listarVisiveisPara(usuarioLogado.getUsuarioLogado());
    }

    public void filtrar() {

        if (dataInicial == null || dataFinal == null) {
            carregar();
            return;
        }

        LocalDateTime inicio = dataInicial.atStartOfDay();
        LocalDateTime fim = dataFinal.atTime(23, 59, 59);

        vendas = vendaService.listarPorPeriodoVisiveisPara(
                usuarioLogado.getUsuarioLogado(), inicio, fim);
    }

    public void exportarPDF() {

        try {

            byte[] pdf = vendaPDFService.gerarRelatorioVendas(vendas);

            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();

            externalContext.responseReset();
            externalContext.setResponseContentType("application/pdf");
            externalContext.setResponseHeader(
                    "Content-Disposition",
                    "attachment; filename=relatorio_vendas.pdf"
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

    public void exportarCSV() {

        try {

            Usuario usuario = usuarioLogado.getUsuarioLogado();

            StringBuilder csv = new StringBuilder();

            csv.append("DATA;USUARIO;PAGAMENTO;TOTAL\n");

            for (Venda v : vendas) {

                csv.append(v.getDataVendaFormatada()).append(";")
                        .append(v.getUsuario().getNome()).append(";")
                        .append(v.getPagamento() != null
                                ? v.getPagamento().getFormaPagamento().name()
                                : "-").append(";")
                        .append(v.getValorTotal())
                        .append("\n");
            }

            FacesContext fc = FacesContext.getCurrentInstance();

            fc.getExternalContext().responseReset();
            fc.getExternalContext().setResponseContentType("text/csv");
            fc.getExternalContext().setResponseHeader(
                    "Content-Disposition",
                    "attachment; filename=vendas.csv"
            );

            fc.getExternalContext()
                    .getResponseOutputStream()
                    .write(csv.toString().getBytes());

            fc.responseComplete();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao exportar CSV", e);
        }
    }

    public List<Venda> getVendas() {
        return vendas;
    }

    public LocalDate getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(LocalDate dataInicial) {
        this.dataInicial = dataInicial;
    }

    public LocalDate getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(LocalDate dataFinal) {
        this.dataFinal = dataFinal;
    }
}