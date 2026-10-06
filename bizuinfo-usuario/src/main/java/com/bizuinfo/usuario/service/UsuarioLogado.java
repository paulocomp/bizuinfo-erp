package com.bizuinfo.usuario.service;

import com.bizuinfo.usuario.model.Usuario;

/**
 * Contrato para saber quem está usando o sistema.
 *
 * Os módulos de negócio dependem desta interface, e não do módulo de acesso,
 * que é quem fornece a implementação (SessaoBean).
 */
public interface UsuarioLogado {

    /**
     * @return o usuário da sessão atual, ou null se ninguém estiver logado
     */
    Usuario getUsuarioLogado();

    boolean isLogado();

    /**
     * Registra o logout na auditoria e invalida a sessão.
     */
    void encerrarSessao();
}
