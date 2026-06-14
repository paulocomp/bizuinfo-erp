package com.bizuinfo.auditoria.service;

import com.bizuinfo.infra.util.RequestUtil;
import com.bizuinfo.auditoria.dao.LogAuditoriaDAO;
import com.bizuinfo.auditoria.model.LogAuditoria;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

@Stateless
public class LogAuditoriaService {

    @Inject
    private LogAuditoriaDAO logAuditoriaDAO;

    /**
     * Registra uma ação no log de auditoria
     *
     * @param acao ex: "CADASTRO_USUARIO", "EDICAO_USUARIO"
     * @param detalhe descrição do que aconteceu
     * @param usuarioResponsavel email ou nome do usuário que executou a ação
     */
    public void registrar(String acao,
                          String detalhe,
                          String usuarioResponsavel) {

        String ipOrigem = RequestUtil.getIpUsuario();

        LogAuditoria log = new LogAuditoria(
                acao,
                detalhe,
                usuarioResponsavel,
                ipOrigem
        );

        logAuditoriaDAO.salvar(log);
    }
}