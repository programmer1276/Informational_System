package com.example.is_curse_work.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.core.env.Environment;

import java.util.Properties;

@Configuration
public class MailConfig {

    // Create a real JavaMailSender when spring.mail.host is configured
    @Bean
    @ConditionalOnProperty(prefix = "spring.mail", name = "host")
    @ConditionalOnMissingBean(JavaMailSender.class)
    public JavaMailSender javaMailSender(Environment env) {
        JavaMailSenderImpl impl = new JavaMailSenderImpl();
        String host = env.getProperty("spring.mail.host");
        String port = env.getProperty("spring.mail.port");
        String username = env.getProperty("spring.mail.username");
        String password = env.getProperty("spring.mail.password");
        impl.setHost(host);
        if (port != null) {
            try {
                impl.setPort(Integer.parseInt(port));
            } catch (NumberFormatException ignored) {
            }
        }
        impl.setUsername(username);
        impl.setPassword(password);
        Properties props = new Properties();
        // copy known properties
        String auth = env.getProperty("spring.mail.properties.mail.smtp.auth");
        String starttls = env.getProperty("spring.mail.properties.mail.smtp.starttls.enable");
        String debug = env.getProperty("spring.mail.properties.mail.debug");
        if (auth != null) props.put("mail.smtp.auth", auth);
        if (starttls != null) props.put("mail.smtp.starttls.enable", starttls);
        if (debug != null) props.put("mail.debug", debug);
        impl.setJavaMailProperties(props);
        return impl;
    }

    @Bean
    @ConditionalOnMissingBean(JavaMailSender.class)
    public JavaMailSender noOpMailSender() {
        return new com.example.is_curse_work.config.NoOpMailSender();
    }
}
