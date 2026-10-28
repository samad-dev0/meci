package meci_e_services.controller;

import meci_e_services.dto.RapportDTO;
import meci_e_services.entity.enums.StatutRapport;
import meci_e_services.entity.enums.TypeRapport;
import meci_e_services.service.RapportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/admin/rapports")
public class AdminRapportController {

    private final RapportService rapportService;

    public AdminRapportController(RapportService rapportService) {
        this.rapportService = rapportService;
    }

    @GetMapping
    public ResponseEntity<List<RapportDTO>> getAllRapports(
            @RequestParam(required = false) String recherche,
            @RequestParam(required = false) StatutRapport statut,
            @RequestParam(required = false) TypeRapport typeRapport,
            Principal principal) {
        return ResponseEntity.ok(rapportService.getAllRapportsForSuperAdmin(
                recherche,
                statut,
                typeRapport,
                principal.getName()
        ));
    }

    @PatchMapping("/{id}/statut")
    public ResponseEntity<RapportDTO> updateStatut(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload,
            Principal principal) {
        String statutValue = payload.get("statut");
        if (statutValue == null || statutValue.isBlank()) {
            throw new IllegalArgumentException("Le statut est obligatoire.");
        }
        return ResponseEntity.ok(rapportService.updateStatutAsSuperAdmin(
                id,
                StatutRapport.valueOf(statutValue),
                payload.get("commentaires"),
                principal.getName()
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRapport(
            @PathVariable Long id,
            Principal principal) {
        rapportService.deleteRapportAsSuperAdmin(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
