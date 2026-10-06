package com.bizuinfo.infra.util;

import jakarta.faces.context.FacesContext;
import jakarta.servlet.http.HttpServletRequest;

public class RequestUtil {

    public static String getIpUsuario() {

        FacesContext context = FacesContext.getCurrentInstance();

        if (context == null) {
            return "IP_DESCONHECIDO";
        }

        HttpServletRequest request =
                (HttpServletRequest)
                        context.getExternalContext().getRequest();

        String[] headers = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED",
                "HTTP_VIA",
                "REMOTE_ADDR"
        };

        for (String header : headers) {

            String ip = request.getHeader(header);

            if (ip != null &&
                    !ip.isBlank() &&
                    !"unknown".equalsIgnoreCase(ip)) {

                if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }

                return ip;
            }
        }

        String remoteAddr = request.getRemoteAddr();

        if ("0:0:0:0:0:0:0:1".equals(remoteAddr)) {
            return "127.0.0.1";
        }

        return remoteAddr;
    }
}