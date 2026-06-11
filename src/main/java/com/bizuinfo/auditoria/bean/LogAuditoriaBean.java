package com.bizuinfo.auditoria.bean;

import com.bizuinfo.usuario.dao.LogAuditoriaDAO;
import com.bizuinfo.usuario.model.LogAuditoria;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class LogAuditoriaBean implements Serializable {

    @Inject
    private LogAuditoriaDAO logAuditoriaDAO;

    private List<LogAuditoria> logs;

    @PostConstruct
    public void init() {
        logs = logAuditoriaDAO.listarTodos();
    }

    public List<LogAuditoria> getLogs() {
        return logs;
    }
}