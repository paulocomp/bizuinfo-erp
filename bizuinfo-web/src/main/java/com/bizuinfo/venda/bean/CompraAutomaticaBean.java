package com.bizuinfo.venda.bean;

import jakarta.inject.Inject;
import com.bizuinfo.produto.dto.SugestaoCompraDTO;
import com.bizuinfo.venda.service.CompraAutomaticaService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class CompraAutomaticaBean implements Serializable {

    @Inject
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