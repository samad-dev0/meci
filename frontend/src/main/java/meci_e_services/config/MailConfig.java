package meci_e_services.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        // Development default: no-op sender if no SMTP config is provided.
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(System.getProperty("spring.mail.host", "localhost"));
        mailSender.setPort(Integer.parseInt(System.getProperty("spring.mail.port", "2525")));
        mailSender.setUsername(System.getProperty("spring.mail.username", "dev@meci.local"));
        mailSender.setPassword(System.getProperty("spring.mail.password", ""));
        return mailSender;
    }
}
