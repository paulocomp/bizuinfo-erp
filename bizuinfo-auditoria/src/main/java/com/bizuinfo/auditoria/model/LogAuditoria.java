package com.bizuinfo.auditoria.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "log_auditoria")
public class LogAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String acao; // Ex: "LOGIN", "LOGOUT"

    @Column(columnDefinition = "TEXT")
    private String detalhe;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(name = "ip_origem", length = 100)
    private String ipOrigem;

    private String usuarioResponsavel;

    public LogAuditoria() {
        this.dataHora = LocalDateTime.now();
    }

    public LogAuditoria(String acao, String detalhe, String usuarioResponsavel, String ipOrigem) {
        this.acao = acao;
        this.detalhe = detalhe;
        this.usuarioResponsavel = usuarioResponsavel;
        this.ipOrigem = ipOrigem;
        this.dataHora = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAcao() { return acao; }
    public void setAcao(String acao) { this.acao = acao; }
    public String getDetalhe() { return detalhe; }
    public void setDetalhe(String detalhe) { this.detalhe = detalhe; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public String getUsuarioResponsavel() { return usuarioResponsavel; }
    public void setUsuarioResponsavel(String usuarioResponsavel) { this.usuarioResponsavel = usuarioResponsavel; }
    public String getIpOrigem() { return ipOrigem; }
    public void setIpOrigem(String ipOrigem) { this.ipOrigem = ipOrigem; }
    public String getDataHoraFormatada() {
        if (dataHora == null) {
            return "";
        }

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        return dataHora.format(formatter);
    }
}