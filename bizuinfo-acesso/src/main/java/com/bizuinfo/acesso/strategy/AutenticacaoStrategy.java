package com.bizuinfo.acesso.strategy;

import com.bizuinfo.acesso.dto.LoginResultado;
import com.bizuinfo.acesso.model.ResultadoLogin;
import com.bizuinfo.auditoria.service.LogAuditoriaService;
import com.bizuinfo.usuario.dao.UsuarioDAO;
import com.bizuinfo.usuario.model.Usuario;
import jakarta.inject.Inject;

/**
 * Estratégia de autenticação. Cada forma de login (senha, Google, token)
 * herda desta classe e implementa {@link #autenticar(CredenciaisLogin)}.
 */
public abstract class AutenticacaoStrategy {

    @Inject
    protected UsuarioDAO usuarioDAO;

    @Inject
    protected LogAuditoriaService logAuditoriaService;

    public abstract LoginResultado autenticar(CredenciaisLogin credenciais);

    /** Registra a falha na auditoria e devolve o resultado sem usuário. */
    protected LoginResultado falha(ResultadoLogin resultado, String motivo, String email) {

        logAuditoriaService.registrar("LOGIN_FALHA", motivo, email);

        return new LoginResultado(resultado, null);
    }

    /** Registra o login na auditoria e devolve o resultado de sucesso. */
    protected LoginResultado sucesso(Usuario usuario) {

        logAuditoriaService.registrar("LOGIN", "Usuário logou", usuario.getNome());

        return new LoginResultado(ResultadoLogin.SUCESSO, usuario);
    }
}