package meci_e_services.service;

import meci_e_services.entity.AppUsers;
import meci_e_services.repository.AppUsersRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AppUsersService {

    private final AppUsersRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final AccessControlService accessControlService;

    public AppUsersService(
            AppUsersRepository repository,
            PasswordEncoder passwordEncoder,
            AccessControlService accessControlService) {

        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.accessControlService = accessControlService;
    }

    public List<AppUsers> getAllUsers() {
        return repository.findAll();
    }

    public AppUsers getUserById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Utilisateur introuvable : " + id
                        )
                );
    }

    @Transactional
    public AppUsers createUser(AppUsers user) {
        if (user == null) {
            throw new IllegalArgumentException("Les informations utilisateur sont obligatoires.");
        }

        String email = user.getEmail() == null ? null : user.getEmail().trim();
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("L'email utilisateur est obligatoire.");
        }
        if (repository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà.");
        }

        // Récupérer le mot de passe fourni dans le formulaire (motDePasse ou password)
        String motDePasseAUtiliser = user.getMotDePasse();
        if (motDePasseAUtiliser == null || motDePasseAUtiliser.isBlank()) {
            motDePasseAUtiliser = user.getPassword();
        }
        if (motDePasseAUtiliser == null || motDePasseAUtiliser.isBlank()) {
            throw new IllegalArgumentException("Le mot de passe utilisateur est obligatoire.");
        }

        user.setEmail(email);
        user.setPassword(
                passwordEncoder.encode(motDePasseAUtiliser.trim())
        );
        user.setSuperAdmin(false);
        user.setGestionnairePartage(false);
        user.setPeutPartagerRapports(false);
        user.setAccessRightsConfigured(true);

        if (user.getActif() == null) {
            user.setActif(true);
        }

        AppUsers savedUser = repository.save(user);
        accessControlService.replaceAccessRights(
                savedUser,
                user.getRequestedAccessRights()
        );
        return savedUser;
    }

    @Transactional
    public AppUsers updateUser(Long id, AppUsers user) {

        AppUsers existingUser = getUserById(id);

        existingUser.setNom(user.getNom());
        existingUser.setPrenom(user.getPrenom());
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            existingUser.setEmail(user.getEmail().trim());
        }
        existingUser.setTelephone(user.getTelephone());
        existingUser.setFonction(user.getFonction());
        existingUser.setService(user.getService());
        if (user.getActif() != null) {
            existingUser.setActif(user.getActif());
        }

        // Si un nouveau mot de passe est renseigné lors de la modification
        String nouveauMotDePasse = user.getMotDePasse();
        if (nouveauMotDePasse == null || nouveauMotDePasse.isBlank()) {
            nouveauMotDePasse = user.getPassword();
        }
        if (nouveauMotDePasse != null && !nouveauMotDePasse.isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(nouveauMotDePasse.trim()));
        }

        if (user.getRequestedAccessRights() != null) {
            accessControlService.replaceAccessRights(
                    existingUser,
                    user.getRequestedAccessRights()
            );
        }
        return repository.save(existingUser);
    }

    public void deleteUser(Long id) {
        repository.deleteById(id);
    }
}