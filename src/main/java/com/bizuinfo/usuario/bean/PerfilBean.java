package com.bizuinfo.usuario.bean;

import com.bizuinfo.acesso.bean.LoginBean;
import com.bizuinfo.usuario.dao.UsuarioDAO;
import com.bizuinfo.usuario.model.Usuario;
import com.bizuinfo.usuario.service.LogAuditoriaService;

import com.bizuinfo.web.Paginas;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.io.Serializable;

@Named
@ViewScoped
public class PerfilBean implements Serializable {

    @Inject
    private LoginBean loginBean;

    @Inject
    private UsuarioDAO usuarioDAO;

    @EJB
    private LogAuditoriaService logAuditoriaService;

    private Usuario usuario;

    private String novaSenha;
    private String confirmarSenha;

    @PostConstruct
    public void init() {
        usuario = loginBean.getUsuarioLogado();
    }

    public void salvar() {

        try {

            if (usuario == null) {
                return;
            }

            Usuario original = usuarioDAO.buscarPorId(usuario.getId())
                    .orElseThrow(() ->
                            new RuntimeException("Usuário não encontrado"));

            StringBuilder alteracoes = new StringBuilder();


            if (!original.getNome().equals(usuario.getNome())) {

                alteracoes.append("Nome: ")
                        .append(original.getNome())
                        .append(" -> ")
                        .append(usuario.getNome())
                        .append(" | ");

                original.setNome(usuario.getNome());
            }

            if (!original.getEmail().equals(usuario.getEmail())) {

                alteracoes.append("Email: ")
                        .append(original.getEmail())
                        .append(" -> ")
                        .append(usuario.getEmail())
                        .append(" | ");

                original.setEmail(usuario.getEmail());
            }

            if (novaSenha != null &&
                    !novaSenha.isBlank()) {

                if (!novaSenha.equals(confirmarSenha)) {

                    FacesContext.getCurrentInstance()
                            .addMessage(
                                    null,
                                    new FacesMessage(
                                            FacesMessage.SEVERITY_ERROR,
                                            "As senhas não coincidem",
                                            null
                                    )
                            );

                    return;
                }

                original.setSenha(
                        BCrypt.hashpw(
                                novaSenha,
                                BCrypt.gensalt()
                        )
                );

                alteracoes.append("Senha alterada | ");
            }

            if (alteracoes.isEmpty()) {

                FacesContext.getCurrentInstance()
                        .addMessage(
                                null,
                                new FacesMessage(
                                        FacesMessage.SEVERITY_WARN,
                                        "Nenhuma alteração foi realizada",
                                        null
                                )
                        );

                return;
            }

            usuarioDAO.salvar(original);

            loginBean.getUsuarioLogado()
                    .setNome(original.getNome());

            loginBean.getUsuarioLogado()
                    .setEmail(original.getEmail());

            usuario = original;

            logAuditoriaService.registrar(
                    "EDITAR_USUARIO",
                    alteracoes.toString(),
                    usuario.getNome()
            );

            novaSenha = null;
            confirmarSenha = null;

            FacesContext.getCurrentInstance()
                    .addMessage(
                            null,
                            new FacesMessage(
                                    FacesMessage.SEVERITY_INFO,
                                    "Perfil atualizado com sucesso",
                                    null
                            )
                    );

        } catch (Exception e) {

            FacesContext.getCurrentInstance()
                    .addMessage(
                            null,
                            new FacesMessage(
                                    FacesMessage.SEVERITY_ERROR,
                                    "Erro ao atualizar perfil",
                                    null
                            )
                    );
        }
    }

    public void excluirConta() {

        try {

            Long id = usuario.getId();

            logAuditoriaService.registrar(
                    "EXCLUIR_USUARIO",
                    "Usuário excluiu a própria conta",
                    usuario.getNome()
            );

            usuarioDAO.remover(id);

            loginBean.sair();

        } catch (Exception e) {

            FacesContext.getCurrentInstance()
                    .addMessage(
                            null,
                            new FacesMessage(
                                    FacesMessage.SEVERITY_ERROR,
                                    "Erro ao excluir conta",
                                    null
                            )
                    );
        }
    }

    public void voltar() throws IOException {

        Usuario usuario = loginBean.getUsuarioLogado();

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