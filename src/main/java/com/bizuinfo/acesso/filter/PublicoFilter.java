package com.bizuinfo.acesso.filter;

import com.bizuinfo.acesso.bean.SessaoBean;
import com.bizuinfo.usuario.model.Usuario;
import com.bizuinfo.usuario.model.Role;

import com.bizuinfo.web.Paginas;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter("/publico/*")
public class PublicoFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        Usuario usuario = null;

        if (session != null) {
            usuario = (Usuario) session.getAttribute(SessaoBean.ATRIBUTO_USUARIO);
        }

        if (usuario != null) {

            String destino;

            if (usuario.getRole() == Role.ADMIN) {
                destino = Paginas.DASHBOARD_ADMIN;

            } else if (usuario.getRole() == Role.GERENTE) {
                destino = Paginas.DASHBOARD_GERENTE;

            } else {
                destino = Paginas.DASHBOARD_FUNCIONARIO;
            }

            res.sendRedirect(req.getContextPath() + destino);
            return;
        }

        chain.doFilter(request, response);
    }
}