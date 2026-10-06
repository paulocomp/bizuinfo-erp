package com.bizuinfo.usuario.bean;

import com.bizuinfo.infra.exception.RegraNegocioException;
import com.bizuinfo.usuario.model.Usuario;
import com.bizuinfo.usuario.service.UsuarioLogado;
import com.bizuinfo.usuario.service.UsuarioService;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.event.RowEditEvent;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Named
@ViewScoped
public class UsuarioGerenteBean implements Serializable {

    @Inject
    private UsuarioService usuarioService;

    @Inject
    private UsuarioLogado usuarioLogado;

    private Long idUsuarioSelecionado;

    private List<Usuario> usuariosCache;

    public List<Usuario> getUsuarios() {

        if (usuariosCache == null) {
            try {
                usuariosCache = usuarioService.listarGerenciaveisPor(usuarioLogado.getUsuarioLogado());

            } catch (Exception e) {
                e.printStackTrace();
                mensagem(FacesMessage.SEVERITY_ERROR, "Erro ao carregar usuários");
                usuariosCache = Collections.emptyList();
            }
        }

        return usuariosCache;
    }

    public void salvar(RowEditEvent<Usuario> event) {

        Usuario usuario = event.getObject();

        if (usuario == null || usuario.getId() == null) return;

        try {
            boolean alterou = usuarioService.editar(usuario, usuarioLogado.getUsuarioLogado());

            if (!alterou) {
                mensagem(FacesMessage.SEVERITY_WARN, "Nenhuma alteração foi realizada");
                return;
            }

            usuariosCache = null;

            mensagem(FacesMessage.SEVERITY_INFO, "Usuário atualizado com sucesso");

        } catch (RegraNegocioException e) {
            mensagem(FacesMessage.SEVERITY_ERROR, e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            mensagem(FacesMessage.SEVERITY_ERROR, "Erro ao salvar usuário: " + e.getMessage());
        }
    }

    public void prepararExclusao(Usuario usuario) {
        if (usuario != null) {
            idUsuarioSelecionado = usuario.getId();
        }
    }

    public void excluirSelecionado() {

        if (idUsuarioSelecionado == null) return;

        try {
            usuarioService.excluir(idUsuarioSelecionado, usuarioLogado.getUsuarioLogado());

            idUsuarioSelecionado = null;
            usuariosCache = null;

            mensagem(FacesMessage.SEVERITY_INFO, "Usuário removido com sucesso");

        } catch (RegraNegocioException e) {
            mensagem(FacesMessage.SEVERITY_ERROR, e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            mensagem(FacesMessage.SEVERITY_ERROR, "Erro ao excluir usuário: " + e.getMessage());
        }
    }

    private void mensagem(FacesMessage.Severity severidade, String texto) {
        FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(severidade, texto, null)
        );
    }
}
