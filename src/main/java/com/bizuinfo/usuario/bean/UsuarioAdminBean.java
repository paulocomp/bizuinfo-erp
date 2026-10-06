package com.bizuinfo.usuario.bean;

import com.bizuinfo.infra.exception.RegraNegocioException;
import com.bizuinfo.usuario.model.Usuario;
import com.bizuinfo.usuario.service.UsuarioLogado;
import com.bizuinfo.usuario.service.UsuarioService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.event.RowEditEvent;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class UsuarioAdminBean implements Serializable {

    @Inject
    private UsuarioService usuarioService;

    @Inject
    private UsuarioLogado usuarioLogado;

    private Long idUsuarioSelecionado;

    private List<Usuario> usuarios;

    @PostConstruct
    public void init() {
        recarregar();
    }

    public List<Usuario> getUsuarios() {
        if (usuarios == null || usuarios.isEmpty()) {
            recarregar();
        }
        return usuarios;
    }

    private void recarregar() {
        usuarios = usuarioService.listarGerenciaveisPor(usuarioLogado.getUsuarioLogado());
    }

    public void salvar(RowEditEvent<Usuario> event) {

        try {
            boolean alterou = usuarioService.editar(event.getObject(), usuarioLogado.getUsuarioLogado());

            if (!alterou) {
                mensagem(FacesMessage.SEVERITY_WARN, "Nenhuma alteração foi realizada");
                return;
            }

            recarregar();

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

            recarregar();

            mensagem(FacesMessage.SEVERITY_INFO, "Usuário removido com sucesso");

        } catch (RegraNegocioException e) {
            mensagem(FacesMessage.SEVERITY_ERROR, e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            mensagem(FacesMessage.SEVERITY_ERROR, "Erro ao excluir usuário: " + e.getMessage());
        }
    }

    public Long getIdUsuarioSelecionado() {
        return idUsuarioSelecionado;
    }

    private void mensagem(FacesMessage.Severity severidade, String texto) {
        FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(severidade, texto, null)
        );
    }
}
