package meci_e_services.auth;

import meci_e_services.entity.AppUsers;
import meci_e_services.repository.AppUsersRepository;
import meci_e_services.dto.AdminLoginResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AppUsersRepository utilisateursRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            AppUsersRepository utilisateursRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.utilisateursRepository = utilisateursRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        if (request == null) {
            throw new AuthenticationFailedException("Email ou mot de passe incorrect.");
        }

        String email = request.getEmail() == null ? "" : request.getEmail().trim();
        String password = request.getPassword() == null ? "" : request.getPassword().trim();

        AppUsers utilisateur =
                utilisateursRepository
                        .findByEmailIgnoreCase(email)
                        .orElseThrow(() ->
                                new AuthenticationFailedException(
                                        "Email ou mot de passe incorrect."
                                )
                        );

        if (!Boolean.TRUE.equals(
                utilisateur.getActif()
        )) {
            throw new AuthenticationFailedException(
                    "Ce compte utilisateur est désactivé."
            );
        }

        if (!passwordEncoder.matches(
                password,
                utilisateur.getPassword()
        )) {
            throw new AuthenticationFailedException(
                    "Email ou mot de passe incorrect."
            );
        }

        String token =
                jwtService.generateToken(
                        utilisateur
                );

        return new LoginResponse(
                token,
                utilisateur.getId(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getEmail(),
                utilisateur.getSuperAdmin(),
                utilisateur.getGestionnairePartage(),
                utilisateur.getPeutPartagerRapports()
        );
    }

    public AdminLoginResponse loginSuperAdmin(String email, String password) {
        AppUsers user = utilisateursRepository.findByEmailIgnoreCase(email == null ? "" : email.trim())
                .orElseThrow(() -> new AuthenticationFailedException(
                        "Adresse e-mail ou mot de passe incorrect."
                ));
        if (!Boolean.TRUE.equals(user.getActif())
                || !Boolean.TRUE.equals(user.getSuperAdmin())
                || password == null
                || !passwordEncoder.matches(password.trim(), user.getPassword())) {
            throw new AuthenticationFailedException(
                    "Adresse e-mail ou mot de passe incorrect."
            );
        }
        return new AdminLoginResponse(
                true,
                "Connexion réussie",
                jwtService.generateToken(user),
                user.getEmail()
        );
    }
}