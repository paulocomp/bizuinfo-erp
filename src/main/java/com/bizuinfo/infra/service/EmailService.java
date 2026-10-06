package com.bizuinfo.infra.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Date;
import java.util.Properties;

@ApplicationScoped
public class EmailService {

    private static final String USER = System.getenv("MAIL_USER");
    private static final String PASS = System.getenv("MAIL_PASS");

    public void enviarEmail(String destinatario, String assunto, String mensagem) {

        Properties props = new Properties();

        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props);

        try {

            MimeMessage msg = new MimeMessage(session);

            msg.setFrom(new InternetAddress(USER));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(destinatario));
            msg.setSubject(assunto);
            msg.setSentDate(new Date());
            msg.setContent(mensagem, "text/html; charset=utf-8");

            Transport.send(msg, USER, PASS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}