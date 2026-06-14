package com.bizuinfo.venda.bean;

import com.bizuinfo.venda.dto.SugestaoCompraDTO;
import com.bizuinfo.venda.service.CompraAutomaticaService;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB; // <- REGRA APLICADA
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class CompraAutomaticaBean implements Serializable {

    @EJB
    private CompraAutomaticaService simulacaoService;

    private List<SugestaoCompraDTO> sugestoes;

    @PostConstruct
    public void init() {
        carregarSugestoes();
    }

    public void carregarSugestoes() {
        sugestoes = simulacaoService.gerarSugestoesDeCompra();
    }

    public List<SugestaoCompraDTO> getSugestoes() { return sugestoes; }
}