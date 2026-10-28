package meci_e_services.controller;

import meci_e_services.dto.AdminLoginRequest;
import meci_e_services.dto.AdminLoginResponse;
import meci_e_services.dto.AutorisationDTO;
import meci_e_services.dto.DelegationAutorisationBatchRequestDTO;
import meci_e_services.dto.DelegationAutorisationRequestDTO;
import meci_e_services.entity.AppUsers;
import meci_e_services.entity.RapportAutorisation;
import meci_e_services.auth.AuthService;
import meci_e_services.service.AppUsersService;
import meci_e_services.service.RapportAutorisationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminAutorisationController {

    private final RapportAutorisationService autorisationService;
    private final AuthService authService;
    private final AppUsersService appUsersService;

    public AdminAutorisationController(
            RapportAutorisationService autorisationService,
            AuthService authService,
            AppUsersService appUsersService) {
        this.autorisationService = autorisationService;
        this.authService = authService;
        this.appUsersService = appUsersService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<AdminLoginResponse> login(@RequestBody AdminLoginRequest request) {
        if (request == null) {
            throw new meci_e_services.auth.AuthenticationFailedException(
                    "Adresse e-mail ou mot de passe incorrect."
            );
        }
        return ResponseEntity.ok(authService.loginSuperAdmin(
                request.getNomUtilisateur(),
                request.getMotDePasse()
        ));
    }

    @GetMapping("/utilisateurs")
    public ResponseEntity<List<AppUsers>> getUsers() {
        return ResponseEntity.ok(appUsersService.getAllUsers());
    }

    @DeleteMapping("/utilisateurs/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        appUsersService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/autorisations")
    public ResponseEntity<List<AutorisationDTO>> getAutorisations() {
        return ResponseEntity.ok(autorisationService.getAllAutorisations());
    }

    @GetMapping("/autorisations/delegation")
    public ResponseEntity<List<RapportAutorisation>> getAutorisationsParCouple(
            @RequestParam Long beneficiaireId,
            @RequestParam Long proprietaireId,
            Principal principal) {
        autorisationService.verifierProprietaire(proprietaireId, principal.getName());
        return ResponseEntity.ok(autorisationService.getAutorisationsParCouple(beneficiaireId, proprietaireId));
    }

    @PostMapping("/autorisations/delegation")
    public ResponseEntity<Void> enregistrerDelegation(
            @RequestBody DelegationAutorisationRequestDTO dto,
            Principal principal) {
        autorisationService.verifierProprietaire(dto.getProprietaireId(), principal.getName());
        autorisationService.enregistrerDelegation(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/autorisations/delegations")
    public ResponseEntity<Void> enregistrerDelegations(
            @RequestBody DelegationAutorisationBatchRequestDTO dto,
            Principal principal) {
        autorisationService.verifierProprietaire(dto.getProprietaireId(), principal.getName());
        autorisationService.enregistrerDelegations(dto);
        return ResponseEntity.ok().build();
    }
}
