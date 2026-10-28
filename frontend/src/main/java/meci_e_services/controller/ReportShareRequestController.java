package meci_e_services.controller;

import meci_e_services.dto.ShareRequestDTO;
import meci_e_services.service.ReportShareRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * Endpoints pour la gestion des demandes de partage de rapports.
 *
 * ── Endpoints Utilisateur (/api/share-requests) ──
 *   POST   /                   → créer des demandes de partage
 *   GET    /mes-demandes       → consulter ses propres demandes
 *   GET    /peut-partager      → vérifier si l'option est disponible
 *
 * ── Endpoints Admin (/api/admin/share-requests) ──
 *   GET    /en-attente         → lister les demandes PENDING
 *   GET    /                   → lister toutes les demandes
 *   PUT    /{id}/approuver     → approuver une demande
 *   PUT    /{id}/rejeter       → rejeter une demande
 */
@RestController
public class ReportShareRequestController {

    private final ReportShareRequestService service;

    public ReportShareRequestController(ReportShareRequestService service) {
        this.service = service;
    }

    // ═══════════════════════════════════════════════════════════════
    //  UTILISATEUR
    // ═══════════════════════════════════════════════════════════════

    /**
     * Créer une ou plusieurs demandes de partage.
     * Body attendu : { rapportIds: [...], beneficiaireIds: [...], motif: "..." }
     */
    @PostMapping("/api/share-requests")
    public ResponseEntity<List<ShareRequestDTO>> creerDemandes(
            @RequestBody ShareRequestDTO dto,
            Principal principal) {
        List<ShareRequestDTO> created = service.creerDemandes(dto, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Consulter ses propres demandes de partage.
     */
    @GetMapping("/api/share-requests/mes-demandes")
    public ResponseEntity<List<ShareRequestDTO>> mesDemandes(Principal principal) {
        return ResponseEntity.ok(service.getMesDemandes(principal.getName()));
    }

    /**
     * Vérifier si l'utilisateur connecté peut créer des demandes de partage.
     * Retourne { "peutPartager": true/false }
     */
    @GetMapping("/api/share-requests/peut-partager")
    public ResponseEntity<Map<String, Boolean>> peutPartager(Principal principal) {
        boolean result = service.peutPartager(principal.getName());
        return ResponseEntity.ok(Map.of("peutPartager", result));
    }

    // ═══════════════════════════════════════════════════════════════
    //  ADMIN
    // ═══════════════════════════════════════════════════════════════

    /**
     * Lister toutes les demandes en attente (PENDING).
     */
    @GetMapping("/api/admin/share-requests/en-attente")
    public ResponseEntity<List<ShareRequestDTO>> demandesEnAttente(Principal principal) {
        return ResponseEntity.ok(service.getDemandesEnAttente(principal.getName()));
    }

    /**
     * Lister toutes les demandes (tous statuts).
     */
    @GetMapping("/api/admin/share-requests")
    public ResponseEntity<List<ShareRequestDTO>> toutesDemandes(Principal principal) {
        return ResponseEntity.ok(service.getToutesDemandes(principal.getName()));
    }

    /**
     * Approuver une demande.
     * Body optionnel : { "commentaire": "..." }
     */
    @PutMapping("/api/admin/share-requests/{id}/approuver")
    public ResponseEntity<ShareRequestDTO> approuver(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body,
            Principal principal) {
        String commentaire = body != null ? body.get("commentaire") : null;
        return ResponseEntity.ok(service.approuverDemande(id, commentaire, principal.getName()));
    }

    /**
     * Rejeter une demande.
     * Body optionnel : { "commentaire": "..." }
     */
    @PutMapping("/api/admin/share-requests/{id}/rejeter")
    public ResponseEntity<ShareRequestDTO> rejeter(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body,
            Principal principal) {
        String commentaire = body != null ? body.get("commentaire") : null;
        return ResponseEntity.ok(service.rejeterDemande(id, commentaire, principal.getName()));
    }
}
