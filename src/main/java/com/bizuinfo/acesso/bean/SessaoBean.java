package com.bizuinfo.acesso.bean;

import com.bizuinfo.auditoria.service.LogAuditoriaService;
import com.bizuinfo.usuario.model.Usuario;
import com.bizuinfo.usuario.service.UsuarioLogado;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

/**
 * Única fonte do usuário logado.
 *
 * O usuário fica guardado no atributo {@link #ATRIBUTO_USUARIO} da HttpSession,
 * que também é lido pelos filtros (AuthFilter, RoleFilter, PublicoFilter).
 * Este bean só lê dali, então não existe uma segunda cópia para sincronizar.
 */
@Named
@RequestScoped
public class SessaoBean implements UsuarioLogado {

    public static final String ATRIBUTO_USUARIO = "usuario";

    @EJB
    private LogAuditoriaService logAuditoriaService;

    @Override
    public Usuario getUsuarioLogado() {

        FacesContext context = FacesContext.getCurrentInstance();

        if (context == null) {
            return null;
        }

        return (Usuario) context
                .getExternalContext()
                .getSessionMap()
                .get(ATRIBUTO_USUARIO);
    }

    @Override
    public boolean isLogado() {
        return getUsuarioLogado() != null;
    }

    /**
     * Coloca o usuário na sessão depois de uma autenticação bem-sucedida.
     */
    public void iniciarSessao(Usuario usuario) {
        FacesContext.getCurrentInstance()
                .getExternalContext()
                .getSessionMap()
                .put(ATRIBUTO_USUARIO, usuario);
    }

    @Override
    public void encerrarSessao() {

        Usuario usuario = getUsuarioLogado();

        if (usuario != null) {
            logAuditoriaService.registrar(
                    "LOGOUT",
                    "Usuário saiu do sistema",
                    usuario.getNome()
            );
        }

        FacesContext.getCurrentInstance()
                .getExternalContext()
                .invalidateSession();
    }
}
