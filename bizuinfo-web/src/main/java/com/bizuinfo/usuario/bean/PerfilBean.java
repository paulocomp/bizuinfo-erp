package com.bizuinfo.usuario.bean;

import com.bizuinfo.usuario.service.UsuarioLogado;
import com.bizuinfo.usuario.service.UsuarioService;
import com.bizuinfo.infra.exception.RegraNegocioException;
import com.bizuinfo.usuario.model.Usuario;

import com.bizuinfo.web.Paginas;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;


import java.io.IOException;
import java.io.Serializable;

@Named
@ViewScoped
public class PerfilBean implements Serializable {

    @Inject
    private UsuarioLogado usuarioLogado;

    @Inject
    private UsuarioService usuarioService;

    private Usuario usuario;

    private String novaSenha;
    private String confirmarSenha;

    @PostConstruct
    public void init() {
        usuario = usuarioLogado.getUsuarioLogado();
    }

    public void salvar() {

        if (usuario == null) {
            return;
        }

        try {
            Usuario salvo = usuarioService.atualizarProprioPerfil(usuario, novaSenha, confirmarSenha);

            if (salvo == null) {
                mensagem(FacesMessage.SEVERITY_WARN, "Nenhuma alteração foi realizada");
                return;
            }

            usuarioLogado.getUsuarioLogado().setNome(salvo.getNome());
            usuarioLogado.getUsuarioLogado().setEmail(salvo.getEmail());

            usuario = salvo;

            novaSenha = null;
            confirmarSenha = null;

            mensagem(FacesMessage.SEVERITY_INFO, "Perfil atualizado com sucesso");

        } catch (RegraNegocioException e) {
            mensagem(FacesMessage.SEVERITY_ERROR, e.getMessage());

        } catch (Exception e) {
            mensagem(FacesMessage.SEVERITY_ERROR, "Erro ao atualizar perfil");
        }
    }

    public void excluirConta() {

        try {
            usuarioService.excluirPropriaConta(usuario);

            usuarioLogado.encerrarSessao();

        } catch (Exception e) {
            mensagem(FacesMessage.SEVERITY_ERROR, "Erro ao excluir conta");
        }
    }

    private void mensagem(FacesMessage.Severity severidade, String texto) {
        FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(severidade, texto, null)
        );
    }

    public void voltar() throws IOException {

        Usuario usuario = usuarioLogado.getUsuarioLogado();

        if (usuario == null) {
            return;
        }

        String ctx = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestContextPath();

        String destino = switch (usuario.getRole()) {

            case ADMIN -> ctx + Paginas.DASHBOARD_ADMIN;
            case GERENTE -> ctx + Paginas.DASHBOARD_GERENTE;
            default -> ctx + Paginas.DASHBOARD_FUNCIONARIO;
        };

        FacesContext.getCurrentInstance()
                .getExternalContext()
                .redirect(destino);
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public String getNovaSenha() {
        return novaSenha;
    }

    public void setNovaSenha(String novaSenha) {
        this.novaSenha = novaSenha;
    }

    public String getConfirmarSenha() {
        return confirmarSenha;
    }

    public void setConfirmarSenha(String confirmarSenha) {
        this.confirmarSenha = confirmarSenha;
    }
}