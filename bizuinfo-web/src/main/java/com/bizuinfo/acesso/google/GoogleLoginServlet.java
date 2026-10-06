package com.bizuinfo.acesso.google;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;

import static java.nio.charset.StandardCharsets.UTF_8;

@WebServlet("/google/login")
public class GoogleLoginServlet extends HttpServlet {

    private static final String CLIENT_ID =
            System.getenv("GOOGLE_CLIENT_ID");

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        String redirectUri = "http://localhost:8080/bizuinfo_erp_war_exploded/google/callback";

        String url = "https://accounts.google.com/o/oauth2/v2/auth"
                + "?client_id=" + CLIENT_ID
                + "&redirect_uri=" + URLEncoder.encode(redirectUri, UTF_8)
                + "&response_type=code"
                + "&scope=openid%20email%20profile";

        response.sendRedirect(url);
    }
}
