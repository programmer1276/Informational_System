package com.example.is_curse_work.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.util.Arrays;

import java.io.InputStream;
import java.util.Properties;

/**
 * A safe no-op JavaMailSender implementation that logs emails instead of sending them.
 * Registered only when a real JavaMailSender is not present.
 */
public class NoOpMailSender implements JavaMailSender {
    private static final Logger log = LoggerFactory.getLogger(NoOpMailSender.class);

    private final Session session = Session.getDefaultInstance(new Properties());

    @Override
    public MimeMessage createMimeMessage() {
        return new MimeMessage(session);
    }

    @Override
    public MimeMessage createMimeMessage(InputStream contentStream) {
        try {
            return new MimeMessage(session, contentStream);
        } catch (Exception e) {
            log.warn("Failed to create MimeMessage from stream: {}", e.toString());
            return new MimeMessage(session);
        }
    }

    @Override
    public void send(MimeMessage mimeMessage) {
        if (mimeMessage == null) {
            log.info("[NoOpMailSender] send MimeMessage <null>");
            return;
        }
        String recs;
        try {
            recs = Arrays.toString(mimeMessage.getAllRecipients());
        } catch (MessagingException e) {
            recs = "<error getting recipients: " + e.getMessage() + ">";
        }
        log.info("[NoOpMailSender] send MimeMessage to: {}", recs);
    }

    @Override
    public void send(MimeMessage... mimeMessages) {
        log.info("[NoOpMailSender] send {} MimeMessages", mimeMessages == null ? 0 : mimeMessages.length);
    }

    @Override
    public void send(MimeMessagePreparator mimeMessagePreparator) {
        log.info("[NoOpMailSender] send MimeMessagePreparator");
    }

    @Override
    public void send(MimeMessagePreparator... mimeMessagePreparators) {
        log.info("[NoOpMailSender] send {} MimeMessagePreparators", mimeMessagePreparators == null ? 0 : mimeMessagePreparators.length);
    }

    @Override
    public void send(SimpleMailMessage simpleMessage) {
        if (simpleMessage == null) {
            log.info("[NoOpMailSender] send SimpleMailMessage <null>");
            return;
        }
        log.info("[NoOpMailSender] send SimpleMailMessage to={}, subject={}, text=[{}]", (Object) simpleMessage.getTo(), simpleMessage.getSubject(), simpleMessage.getText());
    }

    @Override
    public void send(SimpleMailMessage... simpleMessages) {
        log.info("[NoOpMailSender] send {} SimpleMailMessage(s)", simpleMessages == null ? 0 : simpleMessages.length);
    }
}
