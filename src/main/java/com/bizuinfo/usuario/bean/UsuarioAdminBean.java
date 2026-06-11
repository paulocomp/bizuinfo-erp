package com.bizuinfo.usuario.bean;

import com.bizuinfo.acesso.bean.LoginBean;
import com.bizuinfo.usuario.dao.UsuarioDAO;
import com.bizuinfo.usuario.model.Role;
import com.bizuinfo.usuario.model.Usuario;
import com.bizuinfo.usuario.service.LogAuditoriaService;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
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
    private UsuarioDAO usuarioDAO;

    @EJB
    private LogAuditoriaService logAuditoriaService;

    @Inject
    private LoginBean loginBean;

    private Long idUsuarioSelecionado;

    private List<Usuario> usuarios;

    @PostConstruct
    public void init() {
        usuarios = usuarioDAO.listarTodos();
    }

    public List<Usuario> getUsuarios() {
        if (usuarios == null || usuarios.isEmpty()) {
            usuarios = usuarioDAO.listarTodos();
        }
        return usuarios;
    }

    private void recarregar() {
        usuarios = usuarioDAO.listarTodos();
    }

    public void salvar(RowEditEvent<Usuario> event) {

        Usuario editado = event.getObject();

        try {
            Usuario original = usuarioDAO.buscarPorId(editado.getId())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            if (loginBean.getUsuarioLogado() != null &&
                    loginBean.getUsuarioLogado().getId().equals(editado.getId()) &&
                    editado.getRole() != Role.ADMIN) {

                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Você não pode remover seu próprio ADMIN", null));
                return;
            }

            StringBuilder alteracoes = new StringBuilder();

            if (!original.getNome().equals(editado.getNome())) {
                alteracoes.append("Nome: ")
                        .append(original.getNome())
                        .append(" -> ")
                        .append(editado.getNome())
                        .append(" | ");
                original.setNome(editado.getNome());
            }

            if (!original.getEmail().equals(editado.getEmail())) {
                alteracoes.append("Email: ")
                        .append(original.getEmail())
                        .append(" -> ")
                        .append(editado.getEmail())
                        .append(" | ");
                original.setEmail(editado.getEmail());
            }

            if (original.getRole() != editado.getRole()) {
                alteracoes.append("Role: ")
                        .append(original.getRole())
                        .append(" -> ")
                        .append(editado.getRole())
                        .append(" | ");
                original.setRole(editado.getRole());
            }

            if (original.getEmailVerificado() != editado.getEmailVerificado()) {
                alteracoes.append("Status: ")
                        .append(original.getEmailVerificado() ? "ATIVO" : "INATIVO")
                        .append(" -> ")
                        .append(editado.getEmailVerificado() ? "ATIVO" : "INATIVO")
                        .append(" | ");

                original.setEmailVerificado(editado.getEmailVerificado());
            }

            if (alteracoes.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN,
                                "Nenhuma alteração foi realizada", null));
                return;
            }

            usuarioDAO.salvar(original);

            logAuditoriaService.registrar(
                    "EDITAR_USUARIO",
                    alteracoes.toString(),
                    loginBean.getUsuarioLogado().getNome()
            );

            recarregar();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Usuário atualizado com sucesso", null));

        } catch (Exception e) {
            e.printStackTrace();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Erro ao salvar usuário: " + e.getMessage(), null));
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

            if (loginBean.getUsuarioLogado() != null &&
                    loginBean.getUsuarioLogado().getId().equals(idUsuarioSelecionado)) {

                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Você não pode excluir seu próprio usuário", null));
                return;
            }

            usuarioDAO.remover(idUsuarioSelecionado);

            logAuditoriaService.registrar(
                    "EXCLUIR_USUARIO",
                    "Excluiu usuário ID: " + idUsuarioSelecionado,
                    loginBean.getUsuarioLogado().getNome()
            );

            idUsuarioSelecionado = null;

            recarregar();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Usuário removido com sucesso", null));

        } catch (Exception e) {
            e.printStackTrace();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Erro ao excluir usuário: " + e.getMessage(), null));
        }
    }

    public Long getIdUsuarioSelecionado() {
        return idUsuarioSelecionado;
    }
}