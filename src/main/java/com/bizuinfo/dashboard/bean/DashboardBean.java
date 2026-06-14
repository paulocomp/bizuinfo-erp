package com.bizuinfo.dashboard.bean;

import com.bizuinfo.acesso.bean.LoginBean;
import com.bizuinfo.usuario.model.Role;
import com.bizuinfo.venda.dao.VendaDAO;
import com.bizuinfo.venda.model.ItemVenda;
import com.bizuinfo.venda.model.Venda;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.pie.PieChartModel;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

@Named
@ViewScoped
public class DashboardBean implements Serializable {

    @Inject
    private VendaDAO vendaDAO;

    @Inject
    private LoginBean loginBean;

    private List<Venda> vendas;

    // RESUMO
    private double faturamentoHoje;
    private double faturamentoSemana;
    private double faturamentoMes;

    private BarChartModel produtosChart;
    private PieChartModel pagamentoChart;

    @PostConstruct
    public void init() {

        carregarVendas();
        calcularResumo();
        montarGraficoProdutos();
        montarGraficoPagamento();
    }

    private void carregarVendas() {

        Long usuarioId = null;

        if (loginBean.getUsuarioLogado().getRole() == Role.FUNCIONARIO) {
            usuarioId = loginBean.getUsuarioLogado().getId();
        }

        vendas = vendaDAO.buscarVendasParaDashboard(usuarioId);
    }

    private void calcularResumo() {

        LocalDate hoje = LocalDate.now();
        LocalDate semana = hoje.minusDays(7);
        LocalDate mes = hoje.minusDays(30);

        faturamentoHoje = vendas.stream()
                .filter(v -> v.getDataVenda().toLocalDate().isEqual(hoje))
                .mapToDouble(Venda::getValorTotal)
                .sum();

        faturamentoSemana = vendas.stream()
                .filter(v -> v.getDataVenda().toLocalDate().isAfter(semana))
                .mapToDouble(Venda::getValorTotal)
                .sum();

        faturamentoMes = vendas.stream()
                .filter(v -> v.getDataVenda().toLocalDate().isAfter(mes))
                .mapToDouble(Venda::getValorTotal)
                .sum();
    }

    private void montarGraficoProdutos() {

        Map<String, Integer> map = new HashMap<>();

        for (Venda v : vendas) {
            for (ItemVenda item : v.getItens()) {

                String nome = item.getProduto().getNome();

                map.put(nome,
                        map.getOrDefault(nome, 0) + item.getQuantidade());
            }
        }

        List<Map.Entry<String, Integer>> ordenado = map.entrySet()
                .stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(5)
                .toList();

        ChartData data = new ChartData();

        BarChartDataSet dataset = new BarChartDataSet();
        dataset.setLabel("Produtos mais vendidos");

        List<Number> valores = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (var e : ordenado) {
            labels.add(e.getKey());
            valores.add(e.getValue());
        }

        dataset.setData(valores);
        List<String> cores = Arrays.asList(
                "#2196F3", "#4CAF50", "#FF9800", "#9C27B0", "#F44336"
        );

        dataset.setBackgroundColor(cores);
        data.setLabels(labels);
        data.addChartDataSet(dataset);

        produtosChart = new BarChartModel();
        produtosChart.setData(data);

    }

    private void montarGraficoPagamento() {

        Map<String, Double> map = new LinkedHashMap<>();

        for (Venda v : vendas) {

            if (v.getPagamento() == null) {
                continue;
            }

            String forma = v.getPagamento().getFormaPagamento().name();

            map.put(forma,
                    map.getOrDefault(forma, 0.0) + v.getValorTotal());
        }

        ChartData data = new ChartData();

        List<Number> valores = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (var e : map.entrySet()) {
            labels.add(e.getKey());
            valores.add(e.getValue());
        }

        data.setLabels(labels);

        org.primefaces.model.charts.pie.PieChartDataSet dataset =
                new org.primefaces.model.charts.pie.PieChartDataSet();

        dataset.setData(valores);
        List<String> cores = Arrays.asList(
                "#4CAF50", "#2196F3", "#FF9800", "#9C27B0"
        );

        dataset.setBackgroundColor(cores);
        data.addChartDataSet(dataset);

        pagamentoChart = new PieChartModel();
        pagamentoChart.setData(data);
    }

    // GETTERS

    public double getFaturamentoHoje() { return faturamentoHoje; }

    public double getFaturamentoSemana() { return faturamentoSemana; }

    public double getFaturamentoMes() { return faturamentoMes; }

    public BarChartModel getProdutosChart() { return produtosChart; }

    public PieChartModel getPagamentoChart() { return pagamentoChart; }
}