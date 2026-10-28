package meci_e_services.controller;

import meci_e_services.dto.DelegationAutorisationBatchRequestDTO;
import meci_e_services.dto.RapportDTO;
import meci_e_services.dto.RetraitPartageDTO;
import meci_e_services.entity.RapportAutorisation;
import meci_e_services.repository.AppUsersRepository;
import meci_e_services.service.RapportService;
import meci_e_services.service.RapportAutorisationService;
import meci_e_services.service.SharePermissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/autorisations")
public class RapportAutorisationController {

    private final RapportAutorisationService autorisationService;
    private final RapportService rapportService;
    private final AppUsersRepository users;
    private final SharePermissionService sharePermissionService;

    public RapportAutorisationController(
            RapportAutorisationService autorisationService,
            RapportService rapportService,
            AppUsersRepository users,
            SharePermissionService sharePermissionService) {
        this.autorisationService = autorisationService;
        this.rapportService = rapportService;
        this.users = users;
        this.sharePermissionService = sharePermissionService;
    }

    @GetMapping("/gestionnaires")
    public ResponseEntity<List<Long>> getGestionnaires(Principal principal) {
        return ResponseEntity.ok(sharePermissionService.getShareManagerIds(principal.getName()));
    }

    @GetMapping("/partageurs")
    public ResponseEntity<List<Long>> getUtilisateursAutorisesAuPartage(Principal principal) {
        return ResponseEntity.ok(sharePermissionService.getReportSharerIds(principal.getName()));
    }

    @PutMapping("/partageurs")
    public ResponseEntity<Void> remplacerUtilisateursAutorisesAuPartage(
            @RequestBody List<Long> utilisateurIds,
            Principal principal) {
        sharePermissionService.replaceReportSharers(principal.getName(), utilisateurIds);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/peut-partager")
    public ResponseEntity<Boolean> peutPartager(Principal principal) {
        return ResponseEntity.ok(sharePermissionService.canShareOwnReports(principal.getName()));
    }

    @GetMapping("/mes-partages")
    public ResponseEntity<List<RapportAutorisation>> getMesPartages(Principal principal) {
        return ResponseEntity.ok(autorisationService.getPartagesDuProprietaire(principal.getName()));
    }

    @PostMapping("/mes-partages")
    public ResponseEntity<Void> accorderMesPartages(
            @RequestBody List<DelegationAutorisationBatchRequestDTO> partages,
            Principal principal) {
        autorisationService.accorderMesPartages(partages, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/gestionnaires")
    public ResponseEntity<Void> remplacerGestionnaires(
            @RequestBody List<Long> utilisateurIds,
            Principal principal) {
        sharePermissionService.replaceShareManagers(principal.getName(), utilisateurIds);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/super-admin")
    public ResponseEntity<Void> transfererSuperAdmin(
            @RequestBody Long nouvelAdministrateurId,
            Principal principal) {
        sharePermissionService.transferSuperAdmin(principal.getName(), nouvelAdministrateurId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/partages")
    public ResponseEntity<List<RapportAutorisation>> getPartages(Principal principal) {
        sharePermissionService.requireShareManager(principal.getName());
        return ResponseEntity.ok(autorisationService.getPartagesEnregistres());
    }

    @PutMapping("/partages")
    public ResponseEntity<Void> enregistrerPartages(
            @RequestBody List<DelegationAutorisationBatchRequestDTO> partages,
            Principal principal) {
        autorisationService.enregistrerPartagesGlobalement(partages, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/partages/accorder")
    public ResponseEntity<Void> accorderPartages(
            @RequestBody List<DelegationAutorisationBatchRequestDTO> partages,
            Principal principal) {
        autorisationService.accorderPartages(partages, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/partages")
    public ResponseEntity<Void> retirerPartage(
            @RequestParam Long proprietaireId,
            @RequestParam Long beneficiaireId,
            @RequestParam Long rapportId,
            Principal principal) {
        autorisationService.retirerPartage(
                proprietaireId,
                beneficiaireId,
                rapportId,
                principal.getName()
        );
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/partages/selection")
    public ResponseEntity<Void> retirerPartages(
            @RequestBody List<RetraitPartageDTO> retraits,
            Principal principal) {
        autorisationService.retirerPartages(retraits, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/proprietaires/{proprietaireId}/rapports")
    public ResponseEntity<List<RapportDTO>> getRapportsDuProprietaire(
            @PathVariable Long proprietaireId,
            Principal principal) {
        sharePermissionService.requireShareManager(principal.getName());
        var proprietaire = users.findById(proprietaireId)
                .orElseThrow(() -> new IllegalArgumentException("Propriétaire introuvable."));
        return ResponseEntity.ok(rapportService.getMesRapports(proprietaire.getEmail()));
    }

    @GetMapping("/beneficiaires")
    public ResponseEntity<List<Long>> getBeneficiairesAutorises(
            @RequestParam Long proprietaireId,
            Principal principal) {
        autorisationService.verifierProprietaire(proprietaireId, principal.getName());
        return ResponseEntity.ok(autorisationService.getBeneficiaireIdsAutorises(proprietaireId));
    }

    @GetMapping("/delegation")
    public ResponseEntity<List<RapportAutorisation>> getAutorisationsParCouple(
            @RequestParam Long beneficiaireId,
            @RequestParam Long proprietaireId,
            Principal principal) {
        autorisationService.verifierProprietaire(proprietaireId, principal.getName());
        return ResponseEntity.ok(
                autorisationService.getAutorisationsParCouple(beneficiaireId, proprietaireId)
        );
    }

    @PostMapping("/delegations")
    public ResponseEntity<Void> enregistrerDelegations(
            @RequestBody DelegationAutorisationBatchRequestDTO dto,
            Principal principal) {
        if (dto == null) {
            throw new IllegalArgumentException("Les autorisations à enregistrer sont obligatoires.");
        }
        autorisationService.verifierProprietaire(dto.getProprietaireId(), principal.getName());
        autorisationService.enregistrerDelegations(dto);
        return ResponseEntity.ok().build();
    }
}
