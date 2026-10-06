package com.bizuinfo.auditoria.dao;

import com.bizuinfo.auditoria.model.LogAuditoria;
import com.bizuinfo.infra.dao.GenericoDAO;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class LogAuditoriaDAO extends GenericoDAO<LogAuditoria> {

    public LogAuditoriaDAO() {
        super(LogAuditoria.class);
    }

    @Override
    public List<LogAuditoria> listarTodos() {
        return em().createQuery(
                "SELECT l FROM LogAuditoria l ORDER BY l.dataHora DESC",
                LogAuditoria.class
        ).getResultList();
    }
}
