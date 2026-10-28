package meci_e_services.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    public AuthController(
            AuthService authService,
            PasswordResetService passwordResetService
    ) {
        this.authService = authService;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(
                authService.login(request)
        );
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @RequestBody ForgotPasswordRequest request
    ) {

        passwordResetService.demanderReinitialisation(
                request.getEmail()
        );

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Si cette adresse e-mail existe, un lien de réinitialisation a été envoyé."
                )
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @RequestBody ResetPasswordRequest request
    ) {

        passwordResetService.reinitialiserMotDePasse(
                request.getToken(),
                request.getPassword()
        );

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Votre mot de passe a été réinitialisé avec succès."
                )
        );
    }
}