package meci_e_services.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:dev@meci.local}")
    private String adresseExpediteur;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void envoyerLienReinitialisation(
            String destinataire,
            String nomUtilisateur,
            String lien
    ) {

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setFrom(adresseExpediteur);
        message.setTo(destinataire);

        message.setSubject(
                "MECI - Réinitialisation de votre mot de passe"
        );

        message.setText(
                "Bonjour " + nomUtilisateur + ",\n\n"
                        + "Une demande de réinitialisation de mot de passe "
                        + "a été effectuée pour votre compte MECI.\n\n"
                        + "Pour définir un nouveau mot de passe, cliquez sur "
                        + "le lien suivant :\n\n"
                        + lien + "\n\n"
                        + "Ce lien est valable pendant 30 minutes.\n\n"
                        + "Si vous n'êtes pas à l'origine de cette demande, "
                        + "vous pouvez ignorer cet e-mail.\n\n"
                        + "Cordialement,\n"
                        + "MECI"
        );

        mailSender.send(message);
    }
}