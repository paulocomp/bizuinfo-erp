package com.bizuinfo.infra.util;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;

public class EmailUtil {
    public static boolean validarEmail(String email) {
        try {
            InternetAddress endereco = new InternetAddress(email);
            endereco.validate();
            return true;
        } catch (AddressException e) {
            return false;
        }
    }
}
