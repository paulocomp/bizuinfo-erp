package com.bizuinfo.venda.service;

import jakarta.enterprise.context.ApplicationScoped;
import com.bizuinfo.venda.model.Venda;
import com.bizuinfo.venda.model.ItemVenda;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;


import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@ApplicationScoped
public class VendaPDFService {

    public byte[] gerarRecibo(Venda venda) {

        try {

            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            PdfWriter.getInstance(document, out);
            document.open();

            // ===== TÍTULO =====
            Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);

            Paragraph title = new Paragraph("RECIBO DE VENDA", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph(" "));

            // ===== DADOS GERAIS =====
            Font normal = new Font(Font.HELVETICA, 12);

            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            document.add(new Paragraph(
                    "ID Venda: " + venda.getId(),
                    normal
            ));

            document.add(new Paragraph(
                    "Data: " + venda.getDataVenda().format(formatter),
                    normal
            ));

            document.add(new Paragraph(
                    "Cliente/Usuário: " + venda.getUsuario().getNome(),
                    normal
            ));

            document.add(new Paragraph(" "));

            // ===== ITENS =====
            Font sectionTitle = new Font(Font.HELVETICA, 14, Font.BOLD);

            document.add(new Paragraph("ITENS", sectionTitle));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);

            addHeader(table, "Produto");
            addHeader(table, "Quantidade");
            addHeader(table, "Valor Unitário");
            addHeader(table, "Subtotal");

            double total = 0.0;

            for (ItemVenda item : venda.getItens()) {

                table.addCell(item.getProduto().getNome());
                table.addCell(String.valueOf(item.getQuantidade()));
                table.addCell(String.format("%.2f", item.getValorUnitario()));
                table.addCell(String.format("%.2f", item.getSubtotal()));

                total += item.getSubtotal();
            }

            document.add(table);

            document.add(new Paragraph(" "));

            // ===== FORMA DE PAGAMENTO =====

           String formaPagamento;

            switch (venda.getPagamento().getFormaPagamento()) {

                case PIX ->
                        formaPagamento = "PIX";

                case CARTAO_CREDITO ->
                        formaPagamento = "Cartão de Crédito";

                case CARTAO_DEBITO ->
                        formaPagamento = "Cartão de Débito";

                case BOLETO ->
                        formaPagamento = "Boleto";

                default ->
                        formaPagamento = "Não informado";
            }

            Font pagamentoLabelFont =
                    new Font(Font.HELVETICA, 12, Font.BOLD);

            Font pagamentoValueFont =
                    new Font(Font.HELVETICA, 12);

            Paragraph pagamentoParagraph = new Paragraph();

            pagamentoParagraph.add(
                    new Chunk(
                            "FORMA DE PAGAMENTO: ",
                            pagamentoLabelFont
                    )
            );

            pagamentoParagraph.add(
                    new Chunk(
                            formaPagamento,
                            pagamentoValueFont
                    )
            );

            pagamentoParagraph.setAlignment(Element.ALIGN_RIGHT);

            document.add(pagamentoParagraph);

            // ===== TOTAL DESTACADO =====

            Font totalFont =
                    new Font(Font.HELVETICA, 14, Font.BOLD);

            Paragraph totalParagraph = new Paragraph(
                    "TOTAL: R$ " + String.format("%.2f", total),
                    totalFont
            );

            totalParagraph.setAlignment(Element.ALIGN_RIGHT);
            document.add(totalParagraph);
            document.close();

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF da venda", e);
        }
    }

    public byte[] gerarRelatorioVendas(List<Venda> vendas) {

        try {

            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
            Paragraph title = new Paragraph("RELATÓRIO DE VENDAS", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);

            addHeader(table, "Data");
            addHeader(table, "Usuário");
            addHeader(table, "Forma Pagamento");
            addHeader(table, "Total");

            double totalGeral = 0;

            for (Venda v : vendas) {

                table.addCell(v.getDataVendaFormatada());
                table.addCell(v.getUsuario().getNome());
                table.addCell(
                        v.getPagamento() != null
                                ? v.getPagamento().getFormaPagamento().name()
                                : "-"
                );

                table.addCell(String.format("R$ %.2f", v.getValorTotal()));

                totalGeral += v.getValorTotal();
            }

            document.add(table);

            document.add(new Paragraph(" "));

            Font totalFont = new Font(Font.HELVETICA, 14, Font.BOLD);

            Paragraph total = new Paragraph(
                    "TOTAL GERAL: R$ " + String.format("%.2f", totalGeral),
                    totalFont
            );

            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);

            document.close();

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar relatório de vendas", e);
        }
    }

    private void addHeader(PdfPTable table, String text) {

        Font font = new Font(Font.HELVETICA, 12, Font.BOLD);

        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }
}