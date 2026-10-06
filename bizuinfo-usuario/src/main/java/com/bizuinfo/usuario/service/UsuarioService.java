package com.bizuinfo.usuario.service;

import com.bizuinfo.infra.persistencia.Transacional;
import jakarta.enterprise.context.ApplicationScoped;
import com.bizuinfo.auditoria.service.LogAuditoriaService;
import com.bizuinfo.infra.exception.RegraNegocioException;
import com.bizuinfo.usuario.dao.UsuarioDAO;
import com.bizuinfo.usuario.model.Usuario;
import com.bizuinfo.usuario.model.Role;
import jakarta.inject.Inject;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

@ApplicationScoped
@Transacional
public class UsuarioService {

    @Inject
    private UsuarioDAO uDAO;

    @Inject
    private LogAuditoriaService logAuditoriaService;

    public boolean cadastrar(String nome, String email, String senha) {

        if (uDAO.buscarPorEmail(email).isPresent()) {
            return false;
        }

        Usuario usuario = new Usuario(
            nome,
            email,
            BCrypt.hashpw(senha, BCrypt.gensalt())
        );

        uDAO.salvar(usuario);

        return true;
    }

    public void alterarPerfil(Long idUsuario, String novaRole) {

        Usuario usuario = uDAO.buscarPorId(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + idUsuario));

        Role role = Role.valueOf(novaRole);

        usuario.setRole(role);

        uDAO.salvar(usuario);
    }

    /**
     * Usuários que o executor pode gerenciar: o admin vê todos,
     * o gerente não vê os admins.
     */
    public List<Usuario> listarGerenciaveisPor(Usuario executor) {

        List<Usuario> usuarios = uDAO.listarTodos();

        if (executor.getRole() == Role.ADMIN) {
            return usuarios;
        }

        return usuarios.stream()
                .filter(u -> u.getRole() != Role.ADMIN)
                .toList();
    }

    /**
     * Aplica a edição feita na tela de gerenciamento de usuários.
     *
     * @return false se nada mudou
     * @throws RegraNegocioException se o executor não puder fazer essa edição
     */
    public boolean editar(Usuario editado, Usuario executor) {

        Usuario original = buscar(editado.getId());

        if (executor.getRole() == Role.ADMIN) {

            if (executor.getId().equals(editado.getId()) && editado.getRole() != Role.ADMIN) {
                throw new RegraNegocioException("Você não pode remover seu próprio ADMIN");
            }

        } else {

            if (original.getRole() == Role.ADMIN) {
                throw new RegraNegocioException("Gerente não pode editar ADMIN");
            }

            if (editado.getRole() == Role.ADMIN) {
                throw new RegraNegocioException("Gerente não pode atribuir ADMIN");
            }
        }

        StringBuilder alteracoes = new StringBuilder();

        aplicarNomeEmail(original, editado, alteracoes);

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
            return false;
        }

        uDAO.salvar(original);

        logAuditoriaService.registrar(
                "EDITAR_USUARIO",
                alteracoes.toString(),
                executor.getNome()
        );

        return true;
    }

    /**
     * @throws RegraNegocioException se o executor não puder excluir esse usuário
     */
    public void excluir(Long idUsuario, Usuario executor) {

        if (executor.getId().equals(idUsuario)) {
            throw new RegraNegocioException("Você não pode excluir seu próprio usuário");
        }

        if (executor.getRole() != Role.ADMIN && buscar(idUsuario).getRole() == Role.ADMIN) {
            throw new RegraNegocioException("Gerente não pode excluir ADMIN");
        }

        uDAO.remover(idUsuario);

        logAuditoriaService.registrar(
                "EXCLUIR_USUARIO",
                "Excluiu usuário ID: " + idUsuario,
                executor.getNome()
        );
    }

    /**
     * Atualiza nome, email e, se informada, a senha do próprio usuário.
     *
     * @return o usuário salvo, ou null se nada mudou
     * @throws RegraNegocioException se a nova senha e a confirmação não coincidirem
     */
    public Usuario atualizarProprioPerfil(Usuario editado, String novaSenha, String confirmarSenha) {

        Usuario original = buscar(editado.getId());

        StringBuilder alteracoes = new StringBuilder();

        aplicarNomeEmail(original, editado, alteracoes);

        if (novaSenha != null && !novaSenha.isBlank()) {

            if (!novaSenha.equals(confirmarSenha)) {
                throw new RegraNegocioException("As senhas não coincidem");
            }

            original.setSenha(BCrypt.hashpw(novaSenha, BCrypt.gensalt()));

            alteracoes.append("Senha alterada | ");
        }

        if (alteracoes.isEmpty()) {
            return null;
        }

        uDAO.salvar(original);

        logAuditoriaService.registrar(
                "EDITAR_USUARIO",
                alteracoes.toString(),
                original.getNome()
        );

        return original;
    }

    public void excluirPropriaConta(Usuario usuario) {

        logAuditoriaService.registrar(
                "EXCLUIR_USUARIO",
                "Usuário excluiu a própria conta",
                usuario.getNome()
        );

        uDAO.remover(usuario.getId());
    }

    private Usuario buscar(Long id) {
        return uDAO.buscarPorId(id)
                .orElseThrow(() -> new RegraNegocioException("Usuário não encontrado"));
    }

    private void aplicarNomeEmail(Usuario original, Usuario editado, StringBuilder alteracoes) {

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
    }
}
