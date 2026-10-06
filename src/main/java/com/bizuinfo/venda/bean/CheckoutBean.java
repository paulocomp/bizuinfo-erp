package com.bizuinfo.venda.bean;

import com.bizuinfo.usuario.service.UsuarioLogado;
import com.bizuinfo.usuario.model.Usuario;
import com.bizuinfo.venda.model.*;
import com.bizuinfo.venda.service.VendaPDFService;
import com.bizuinfo.venda.service.VendaService;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Named
@ViewScoped
public class CheckoutBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private CarrinhoBean carrinhoBean;

    @Inject
    private UsuarioLogado usuarioLogado;

    @EJB
    private VendaPDFService vendaPDFService;

    @EJB
    private VendaService vendaService;

    private FormaPagamento formaPagamento;

    private Venda vendaFinalizada;

    private boolean pixGerado;

    private String chavePix;

    private String codigoPix;

    private String qrCodePix;

    public String finalizar() {

        try {

            if (carrinhoBean.getItens().isEmpty()) {
                throw new RuntimeException("Carrinho está vazio.");
            }

            if (formaPagamento == null) {
                throw new RuntimeException("Selecione uma forma de pagamento.");
            }

            if (formaPagamento == FormaPagamento.PIX && !pixGerado) {
                throw new RuntimeException("Gere o código PIX antes de finalizar.");
            }

            Usuario usuario = usuarioLogado.getUsuarioLogado();

            if (usuario == null) {
                throw new RuntimeException("Usuário não autenticado na sessão.");
            }

            Venda venda = new Venda();
            venda.setUsuario(usuario);
            venda.setDataVenda(LocalDateTime.now());

            Pagamento pagamento = new Pagamento();
            pagamento.setFormaPagamento(formaPagamento);
            pagamento.setStatusPagamento(StatusPagamento.APROVADO);
            pagamento.setValor(carrinhoBean.getValorTotal());

            vendaFinalizada = vendaService.finalizarVenda(
                    venda,
                    pagamento,
                    carrinhoBean.getItens()
            );

            carrinhoBean.limparCarrinho();

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_INFO,
                            "Sucesso",
                            "Venda finalizada com sucesso!"
                    )
            );

            return "/restrito/venda/recibo_venda.xhtml?faces-redirect=true&vendaId="
                    + vendaFinalizada.getId();

        } catch (Exception e) {

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Erro ao finalizar venda",
                            e.getMessage()
                    )
            );

            return null;
        }
    }

    public void gerarPix() {

        chavePix = "pix@bizuinfo.com.br";

        codigoPix = java.util.UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 24)
                .toUpperCase();

        qrCodePix =
                "https://api.qrserver.com/v1/create-qr-code/?size=250x250&data="
                        + codigoPix;

        pixGerado = true;

        FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(
                        FacesMessage.SEVERITY_INFO,
                        "PIX gerado",
                        "Simulação PIX pronta para pagamento."
                )
        );
    }

    public FormaPagamento getFormaPagamento() {  return formaPagamento;  }

    public void setFormaPagamento(FormaPagamento formaPagamento) {  this.formaPagamento = formaPagamento;  }

    public Venda getVendaFinalizada() {  return vendaFinalizada;  }

    public boolean isPixGerado() {  return pixGerado;  }

    public String getChavePix() {  return chavePix;  }

    public String getCodigoPix() {  return codigoPix;  }

    public String getQrCodePix() {  return qrCodePix;  }

}