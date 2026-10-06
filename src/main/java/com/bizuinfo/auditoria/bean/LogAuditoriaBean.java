package com.bizuinfo.auditoria.bean;

import com.bizuinfo.auditoria.service.LogAuditoriaService;
import com.bizuinfo.auditoria.model.LogAuditoria;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class LogAuditoriaBean implements Serializable {

    @EJB
    private LogAuditoriaService logAuditoriaService;

    private List<LogAuditoria> logs;

    @PostConstruct
    public void init() {
        logs = logAuditoriaService.listarTodos();
    }

    public List<LogAuditoria> getLogs() {
        return logs;
    }
}