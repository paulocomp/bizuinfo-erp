package com.bizuinfo.usuario.bean;

import com.bizuinfo.acesso.bean.LoginBean;
import com.bizuinfo.usuario.dao.UsuarioDAO;
import com.bizuinfo.usuario.model.Role;
import com.bizuinfo.usuario.model.Usuario;
import com.bizuinfo.usuario.service.LogAuditoriaService;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.event.RowEditEvent;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class UsuarioGerenteBean implements Serializable {

    @Inject
    private UsuarioDAO usuarioDAO;

    @EJB
    private LogAuditoriaService logAuditoriaService;

    @Inject
    private LoginBean loginBean;

    private Long idUsuarioSelecionado;

    private List<Usuario> usuariosCache;

    public List<Usuario> getUsuarios() {

        if (usuariosCache == null) {
            try {
                usuariosCache = usuarioDAO.listarTodos()
                        .stream()
                        .filter(u -> u.getRole() != Role.ADMIN)
                        .collect(Collectors.toList());

            } catch (Exception e) {
                e.printStackTrace();
                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Erro ao carregar usuários", null)
                );
                usuariosCache = Collections.emptyList();
            }
        }

        return usuariosCache;
    }

    public void salvar(RowEditEvent<Usuario> event) {

        Usuario usuario = event.getObject();

        try {
            if (usuario == null || usuario.getId() == null) return;

            Usuario original = usuarioDAO.buscarPorId(usuario.getId())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            if (original.getRole() == Role.ADMIN) {
                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Gerente não pode editar ADMIN", null)
                );
                return;
            }

            if (usuario.getRole() == Role.ADMIN) {
                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Gerente não pode atribuir ADMIN", null)
                );
                return;
            }

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

            if (original.getRole() != usuario.getRole()) {
                alteracoes.append("Role: ")
                        .append(original.getRole())
                        .append(" -> ")
                        .append(usuario.getRole())
                        .append(" | ");
                original.setRole(usuario.getRole());
            }

            if (original.getEmailVerificado() != usuario.getEmailVerificado()) {
                alteracoes.append("Status: ")
                        .append(original.getEmailVerificado() ? "ATIVO" : "INATIVO")
                        .append(" -> ")
                        .append(usuario.getEmailVerificado() ? "ATIVO" : "INATIVO");
                original.setEmailVerificado(usuario.getEmailVerificado());
            }

            if (alteracoes.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN,
                                "Nenhuma alteração foi realizada", null)
                );
                return;
            }

            usuarioDAO.salvar(original);

            logAuditoriaService.registrar(
                    "EDITAR_USUARIO",
                    alteracoes.toString(),
                    loginBean.getUsuarioLogado().getNome()
            );

            usuariosCache = null;

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Usuário atualizado com sucesso", null)
            );

        } catch (Exception e) {
            e.printStackTrace();

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Erro ao salvar usuário: " + e.getMessage(),
                            null
                    )
            );
        }
    }
    public void prepararExclusao(Usuario usuario) {
        if (usuario != null) {
            idUsuarioSelecionado = usuario.getId();
        }
    }

    public void excluirSelecionado() {

        try {
            if (idUsuarioSelecionado == null) return;

            Usuario usuario = usuarioDAO.buscarPorId(idUsuarioSelecionado)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            if (usuario.getRole() == Role.ADMIN) {
                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Gerente não pode excluir ADMIN", null)
                );
                return;
            }

            if (loginBean.getUsuarioLogado().getId().equals(idUsuarioSelecionado)) {
                FacesContext.getCurrentInstance().addMessage(
                        null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Você não pode excluir seu próprio usuário", null)
                );
                return;
            }

            usuarioDAO.remover(idUsuarioSelecionado);

            logAuditoriaService.registrar(
                    "EXCLUIR_USUARIO",
                    "Excluiu usuário ID: " + idUsuarioSelecionado,
                    loginBean.getUsuarioLogado().getNome()
            );

            idUsuarioSelecionado = null;
            usuariosCache = null;

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Usuário removido com sucesso", null)
            );

        } catch (Exception e) {
            e.printStackTrace();

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Erro ao excluir usuário: " + e.getMessage(),
                            null
                    )
            );
        }
    }
}