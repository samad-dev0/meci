package meci_e_services.auth;

import meci_e_services.email.EmailService;
import meci_e_services.entity.AppUsers;
import meci_e_services.entity.PasswordResetToken;
import meci_e_services.repository.AppUsersRepository;
import meci_e_services.repository.PasswordResetTokenRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class PasswordResetService {

    private final AppUsersRepository utilisateursRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    private final EmailService emailService;

    private final SecureRandom secureRandom = new SecureRandom();

    public PasswordResetService(
            AppUsersRepository utilisateursRepository,
            PasswordResetTokenRepository tokenRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService
    ) {
        this.utilisateursRepository = utilisateursRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public void demanderReinitialisation(String email) {

        AppUsers utilisateur =
                utilisateursRepository
                        .findByEmail(email)
                        .orElse(null);

        /*
         * Pour éviter de révéler si une adresse e-mail
         * existe dans la base, on ne retourne pas d'erreur
         * si l'utilisateur est introuvable.
         */
        if (utilisateur == null) {
            return;
        }

        /*
         * Invalide les anciens tokens encore actifs.
         */
        tokenRepository
                .findAll()
                .stream()
                .filter(token ->
                        token.getUtilisateur()
                                .getId()
                                .equals(utilisateur.getId())
                )
                .filter(token ->
                        !Boolean.TRUE.equals(token.getUtilise())
                )
                .forEach(token -> {
                    token.setUtilise(true);
                    tokenRepository.save(token);
                });

        /*
         * Génération d'un token aléatoire sécurisé.
         */
        String token = genererToken();

        /*
         * On ne stocke pas le token original en base.
         */
        String tokenHash = hasherToken(token);

        PasswordResetToken passwordResetToken =
                new PasswordResetToken();

        passwordResetToken.setUtilisateur(utilisateur);
        passwordResetToken.setTokenHash(tokenHash);
        passwordResetToken.setExpiration(
                LocalDateTime.now().plusMinutes(30)
        );
        passwordResetToken.setUtilise(false);

        tokenRepository.save(passwordResetToken);

        /*
         * Pour le moment, nous affichons le lien dans la console.
         * L'envoi réel par e-mail sera ajouté à l'étape suivante.
         */
        String lien =
                "http://localhost:4200/auth/reset-password?token="
                        + token;

        emailService.envoyerLienReinitialisation(
                utilisateur.getEmail(),
                utilisateur.getPrenom() + " " + utilisateur.getNom(),
                lien
        );
    }

    public void reinitialiserMotDePasse(
            String token,
            String nouveauMotDePasse
    ) {

        String tokenHash = hasherToken(token);

        PasswordResetToken resetToken =
                tokenRepository
                        .findByTokenHashAndUtiliseFalse(tokenHash)
                        .orElseThrow(() ->
                                new AuthenticationFailedException(
                                        "Le lien de réinitialisation est invalide."
                                )
                        );

        if (resetToken.getExpiration()
                .isBefore(LocalDateTime.now())) {

            resetToken.setUtilise(true);
            tokenRepository.save(resetToken);

            throw new AuthenticationFailedException(
                    "Le lien de réinitialisation a expiré."
            );
        }

        if (nouveauMotDePasse == null
                || nouveauMotDePasse.length() < 8) {

            throw new AuthenticationFailedException(
                    "Le mot de passe doit contenir au moins 8 caractères."
            );
        }

        AppUsers utilisateur =
                resetToken.getUtilisateur();

        utilisateur.setPassword(
                passwordEncoder.encode(
                        nouveauMotDePasse
                )
        );

        utilisateursRepository.save(utilisateur);

        /*
         * Le token devient inutilisable immédiatement
         * après le changement du mot de passe.
         */
        resetToken.setUtilise(true);
        tokenRepository.save(resetToken);
    }

    private String genererToken() {

        byte[] bytes = new byte[32];

        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    private String hasherToken(String token) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    digest.digest(
                            token.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            return Base64.getEncoder()
                    .encodeToString(hash);

        } catch (NoSuchAlgorithmException e) {

            throw new IllegalStateException(
                    "Impossible de sécuriser le token.",
                    e
            );
        }
    }
}