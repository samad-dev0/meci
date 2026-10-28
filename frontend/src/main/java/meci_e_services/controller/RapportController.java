package meci_e_services.controller;

import meci_e_services.dto.RapportDTO;
import meci_e_services.entity.enums.StatutRapport;
import meci_e_services.entity.enums.TypeRapport;
import meci_e_services.service.RapportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rapports")
public class RapportController {

    private final RapportService service;

    public RapportController(RapportService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<RapportDTO>> getAllRapports(
            @RequestParam(required = false) String recherche,
            @RequestParam(required = false) StatutRapport statut,
            @RequestParam(required = false) TypeRapport typeRapport,
            Principal principal) {

        return ResponseEntity.ok(
                service.getAllRapports(
                        recherche,
                        statut,
                        typeRapport,
                        principal.getName()
                )
        );
    }

    @GetMapping("/mes-rapports")
    public ResponseEntity<List<RapportDTO>> getMesRapports(Principal principal) {
        return ResponseEntity.ok(service.getMesRapports(principal.getName()));
    }

    @GetMapping("/partages")
    public ResponseEntity<List<RapportDTO>> getRapportsPartages(Principal principal) {
        return ResponseEntity.ok(service.getRapportsPartages(principal.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RapportDTO> getRapportById(
            @PathVariable Long id,
            Principal principal) {

        return ResponseEntity.ok(
                service.getRapportById(id, principal.getName())
        );
    }

    @PostMapping
    public ResponseEntity<RapportDTO> createRapport(
            @RequestBody RapportDTO dto,
            Principal principal) {

        RapportDTO created = service.createRapport(dto, principal.getName());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @PostMapping(value = "/fichier", consumes = "multipart/form-data")
    public ResponseEntity<RapportDTO> createRapportAvecFichier(
            @RequestParam String titre,
            @RequestParam(required = false) String typeRapport,
            @RequestParam(required = false) String contenu,
            @RequestParam("fichier") MultipartFile fichier,
            Principal principal) {

        RapportDTO created = service.createRapportAvecFichier(
                titre,
                typeRapport,
                contenu,
                fichier,
                principal.getName()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @GetMapping("/{id}/telecharger")
    public ResponseEntity<byte[]> telechargerRapport(
            @PathVariable Long id,
            Principal principal) {
        return service.telechargerRapport(id, principal.getName());
    }

    @PutMapping("/{id}")
    public ResponseEntity<RapportDTO> updateRapport(
            @PathVariable Long id,
            @RequestBody RapportDTO dto,
            Principal principal) {

        return ResponseEntity.ok(
                service.updateRapport(id, dto, principal.getName())
        );
    }

    @PatchMapping("/{id}/statut")
    public ResponseEntity<RapportDTO> updateStatut(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload,
            Principal principal) {

        String statutStr = payload.get("statut");
        String commentaires = payload.get("commentaires");

        if (statutStr == null || statutStr.isBlank()) {
            throw new IllegalArgumentException(
                    "Le statut est obligatoire."
            );
        }

        StatutRapport statut =
                StatutRapport.valueOf(statutStr);

        return ResponseEntity.ok(
                service.updateStatut(
                        id,
                        statut,
                        commentaires,
                        principal.getName()
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRapport(
            @PathVariable Long id,
            Principal principal) {

        service.deleteRapport(id, principal.getName());

        return ResponseEntity.noContent().build();
    }
}