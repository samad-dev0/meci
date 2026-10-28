package meci_e_services.service;

import meci_e_services.dto.RapportDTO;
import meci_e_services.entity.AppUsers;
import meci_e_services.entity.Rapport;
import meci_e_services.entity.RapportAutorisation;
import meci_e_services.entity.enums.StatutRapport;
import meci_e_services.entity.enums.TypeRapport;
import meci_e_services.repository.AppUsersRepository;
import meci_e_services.repository.RapportAutorisationRepository;
import meci_e_services.repository.RapportRepository;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class RapportService {

    private final RapportRepository repository;
    private final AppUsersRepository appUsersRepository;
    private final RapportAutorisationRepository autorisationRepository;
    private final SharePermissionService sharePermissionService;

    public RapportService(
            RapportRepository repository,
            AppUsersRepository appUsersRepository,
            RapportAutorisationRepository autorisationRepository,
            SharePermissionService sharePermissionService) {
        this.repository = repository;
        this.appUsersRepository = appUsersRepository;
        this.autorisationRepository = autorisationRepository;
        this.sharePermissionService = sharePermissionService;
    }

    public List<RapportDTO> getMesRapports(String email) {
        return repository.findByAuteurEmailIgnoreCase(email).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<RapportDTO> getRapportsPartages(String email) {
        AppUsers beneficiaire = appUsersRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable."));
        List<Long> rapportIds = autorisationRepository.findByBeneficiaireId(beneficiaire.getId()).stream()
                .filter(autorisation -> !Boolean.TRUE.equals(autorisation.getTousRapports()))
                .map(RapportAutorisation::getRapportId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        return repository.findAllById(rapportIds).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<RapportDTO> getAllRapports(
            String recherche,
            StatutRapport statut,
            TypeRapport typeRapport,
            String email) {

        List<Rapport> rapports;

        if (recherche != null || statut != null || typeRapport != null) {

            rapports = repository.meciSearch(
                    recherche != null && !recherche.trim().isEmpty()
                            ? recherche.trim()
                            : null,
                    statut,
                    typeRapport
            );

        } else {
            rapports = repository.findAll();
        }

        return rapports.stream()
                .filter(rapport -> estProprietaire(rapport, email))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<RapportDTO> getAllRapportsForSuperAdmin(
            String recherche,
            StatutRapport statut,
            TypeRapport typeRapport,
            String email) {
        sharePermissionService.requireSuperAdmin(email);
        return rechercherRapports(recherche, statut, typeRapport).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public RapportDTO getRapportById(Long id, String email) {
        Rapport rapport = trouverRapport(id);
        if (!estProprietaire(rapport, email) && !estRapportPartage(rapport, email)) {
            throw new AccessDeniedException("Vous ne pouvez pas consulter ce rapport.");
        }

        return mapToDTO(rapport);
    }

    public RapportDTO createRapportAvecFichier(
            String titre,
            String typeRapport,
            String contenu,
            MultipartFile fichier,
            String auteurEmail) {

        if (titre == null || titre.isBlank()) {
            throw new IllegalArgumentException("Le titre est obligatoire.");
        }
        if (fichier == null || fichier.isEmpty()) {
            throw new IllegalArgumentException("Le fichier du rapport est obligatoire.");
        }

        RapportDTO dto = new RapportDTO();
        dto.setTitre(titre.trim());
        dto.setTypeRapport(resoudreTypeRapport(typeRapport));
        dto.setContenu(contenu);
        dto.setNomFichier(fichier.getOriginalFilename());
        dto.setFileSize(fichier.getSize());
        RapportDTO created = createRapport(dto, auteurEmail);

        try {
            Path dossier = Path.of("uploads", "rapports");
            Files.createDirectories(dossier);
            String nomOriginal = fichier.getOriginalFilename() == null || fichier.getOriginalFilename().isBlank()
                    ? "rapport"
                    : Path.of(fichier.getOriginalFilename()).getFileName().toString();
            String nomStocke = created.getId() + "_" + nomOriginal.replaceAll("[^a-zA-Z0-9._-]", "_");
            Path destination = dossier.resolve(nomStocke);
            fichier.transferTo(destination.toAbsolutePath());

            Rapport rapport = trouverRapport(created.getId());
            rapport.setNomFichier(nomOriginal);
            rapport.setCheminFichier(destination.toAbsolutePath().toString());
            rapport.setFileSize(fichier.getSize());
            return mapToDTO(repository.save(rapport));
        } catch (IOException exception) {
            throw new IllegalStateException("Impossible d’enregistrer le fichier du rapport.");
        }
    }

    public ResponseEntity<byte[]> telechargerRapport(Long id, String email) {
        Rapport rapport = trouverRapport(id);
        if (!estProprietaire(rapport, email) && !estRapportPartage(rapport, email)) {
            throw new AccessDeniedException("Vous ne pouvez pas télécharger ce rapport.");
        }

        byte[] contenu;
        String nomFichier;
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;

        Path fichierStocke = rapport.getCheminFichier() != null && !rapport.getCheminFichier().isBlank()
                ? Path.of(rapport.getCheminFichier())
                : null;
        if (fichierStocke != null && Files.isRegularFile(fichierStocke)) {
            try {
                contenu = Files.readAllBytes(fichierStocke);
                String probed = Files.probeContentType(fichierStocke);
                if (probed != null) {
                    mediaType = MediaType.parseMediaType(probed);
                }
            } catch (IOException exception) {
                throw new IllegalStateException("Impossible de lire le fichier du rapport.");
            }
            nomFichier = rapport.getNomFichier() != null && !rapport.getNomFichier().isBlank()
                    ? rapport.getNomFichier()
                    : fichierStocke.getFileName().toString();
        } else {
            String texte = rapport.getContenu() != null && !rapport.getContenu().isBlank()
                    ? rapport.getContenu()
                    : "Rapport : " + rapport.getTitre() + System.lineSeparator()
                    + "Auteur : " + rapport.getAuteurNom() + System.lineSeparator();
            contenu = texte.getBytes(StandardCharsets.UTF_8);
            nomFichier = rapport.getNomFichier() != null && !rapport.getNomFichier().isBlank()
                    && !rapport.getNomFichier().contains(",")
                    ? rapport.getNomFichier()
                    : (rapport.getTitre() == null ? "rapport" : rapport.getTitre().replaceAll("[\\\\/:*?\"<>|]", "_")) + ".txt";
            mediaType = MediaType.TEXT_PLAIN;
        }

        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(nomFichier, StandardCharsets.UTF_8)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .contentType(mediaType)
                .body(contenu);
    }

    public RapportDTO createRapport(RapportDTO dto, String auteurEmail) {

        /*
         * Vérification de la taille du fichier.
         */
        if (dto.getFileSize() == null) {
            throw new IllegalArgumentException(
                    "La taille du fichier est obligatoire."
            );
        }

        if (dto.getFileSize() < 0) {
            throw new IllegalArgumentException(
                    "La taille du fichier ne peut pas être négative."
            );
        }

        AppUsers auteur = appUsersRepository.findByEmail(auteurEmail)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable."));
        Rapport rapport = mapToEntity(dto);
        rapport.setAuteurEmail(auteur.getEmail());
        rapport.setAuteurNom(auteur.getPrenom() + " " + auteur.getNom());

        /*
         * Valeur par défaut du statut.
         */
        if (rapport.getStatut() == null) {
            rapport.setStatut(StatutRapport.EN_ATTENTE);
        }

        Rapport saved = repository.save(rapport);

        return mapToDTO(saved);
    }

    public RapportDTO updateRapport(Long id, RapportDTO dto, String email) {
        Rapport existing = trouverRapport(id);
        verifierProprietaire(existing, email);

        if (dto.getTitre() != null) {
            existing.setTitre(dto.getTitre());
        }

        if (dto.getTypeRapport() != null) {
            existing.setTypeRapport(dto.getTypeRapport());
        }

        if (dto.getContenu() != null) {
            existing.setContenu(dto.getContenu());
        }

        if (dto.getStatut() != null) {
            existing.setStatut(dto.getStatut());
        }

        if (dto.getVerificateursAssignes() != null) {
            existing.setVerificateursAssignes(
                    dto.getVerificateursAssignes()
            );
        }

        if (dto.getNomFichier() != null) {
            existing.setNomFichier(dto.getNomFichier());
        }

        if (dto.getCheminFichier() != null) {
            existing.setCheminFichier(dto.getCheminFichier());
        }

        /*
         * On ne remplace fileSize que si une valeur est fournie.
         */
        if (dto.getFileSize() != null) {

            if (dto.getFileSize() < 0) {
                throw new IllegalArgumentException(
                        "La taille du fichier ne peut pas être négative."
                );
            }

            existing.setFileSize(dto.getFileSize());
        }

        if (dto.getCommentaires() != null) {
            existing.setCommentaires(dto.getCommentaires());
        }

        Rapport updated = repository.save(existing);

        return mapToDTO(updated);
    }

    public RapportDTO updateStatut(
            Long id,
            StatutRapport statut,
            String commentaires,
            String email) {
        Rapport existing = trouverRapport(id);
        verifierProprietaire(existing, email);

        existing.setStatut(statut);

        if (commentaires != null) {
            existing.setCommentaires(commentaires);
        }

        Rapport updated = repository.save(existing);

        return mapToDTO(updated);
    }

    public RapportDTO updateStatutAsSuperAdmin(
            Long id,
            StatutRapport statut,
            String commentaires,
            String email) {
        sharePermissionService.requireSuperAdmin(email);
        Rapport existing = trouverRapport(id);
        existing.setStatut(statut);
        if (commentaires != null) {
            existing.setCommentaires(commentaires);
        }
        return mapToDTO(repository.save(existing));
    }

    public void deleteRapport(Long id, String email) {
        Rapport rapport = trouverRapport(id);
        verifierProprietaire(rapport, email);
        supprimerRapport(rapport);
    }

    public void deleteRapportAsSuperAdmin(Long id, String email) {
        sharePermissionService.requireSuperAdmin(email);
        supprimerRapport(trouverRapport(id));
    }

    private void supprimerRapport(Rapport rapport) {
        Long id = rapport.getId();
        autorisationRepository.deleteByRapportId(id);
        if (rapport.getCheminFichier() != null && !rapport.getCheminFichier().isBlank()) {
            try {
                Files.deleteIfExists(Path.of(rapport.getCheminFichier()));
            } catch (IOException ignored) {
                // Le rapport métier est prioritaire sur le nettoyage du fichier.
            }
        }
        repository.deleteById(id);
    }

    private List<Rapport> rechercherRapports(
            String recherche,
            StatutRapport statut,
            TypeRapport typeRapport) {
        if ((recherche != null && !recherche.trim().isEmpty())
                || statut != null || typeRapport != null) {
            return repository.meciSearch(
                    recherche != null && !recherche.trim().isEmpty()
                            ? recherche.trim()
                            : null,
                    statut,
                    typeRapport
            );
        }
        return repository.findAll();
    }

    private TypeRapport resoudreTypeRapport(String typeRapport) {
        if (typeRapport == null || typeRapport.isBlank()) {
            return TypeRapport.RAPPORT_ACTIVITE;
        }
        try {
            return TypeRapport.valueOf(typeRapport);
        } catch (IllegalArgumentException exception) {
            return TypeRapport.RAPPORT_ACTIVITE;
        }
    }

    private Rapport trouverRapport(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rapport non trouvé avec l'id : " + id));
    }

    private boolean estProprietaire(Rapport rapport, String email) {
        return email != null && rapport.getAuteurEmail() != null &&
                rapport.getAuteurEmail().equalsIgnoreCase(email);
    }

    private boolean estRapportPartage(Rapport rapport, String email) {
        if (email == null) {
            return false;
        }
        AppUsers beneficiaire = appUsersRepository.findByEmail(email).orElse(null);
        return beneficiaire != null && autorisationRepository
                .existsByBeneficiaireIdAndRapportIdAndTousRapportsFalse(
                        beneficiaire.getId(),
                        rapport.getId()
                );
    }

    private void verifierProprietaire(Rapport rapport, String email) {
        if (!estProprietaire(rapport, email)) {
            throw new AccessDeniedException("Vous ne pouvez modifier que vos propres rapports.");
        }
    }

    /*
     * Entity -> DTO
     */
    private RapportDTO mapToDTO(Rapport rapport) {

        RapportDTO dto = new RapportDTO();

        dto.setId(rapport.getId());
        dto.setTitre(rapport.getTitre());
        dto.setTypeRapport(rapport.getTypeRapport());
        dto.setAuteurNom(rapport.getAuteurNom());
        dto.setAuteurEmail(rapport.getAuteurEmail());
        dto.setContenu(rapport.getContenu());
        dto.setStatut(rapport.getStatut());
        dto.setVerificateursAssignes(
                rapport.getVerificateursAssignes()
        );
        dto.setNomFichier(rapport.getNomFichier());
        dto.setCheminFichier(rapport.getCheminFichier());

        /*
         * IMPORTANT :
         * récupération de fileSize.
         */
        dto.setFileSize(rapport.getFileSize());

        dto.setCommentaires(rapport.getCommentaires());
        dto.setDateCreation(rapport.getDateCreation());
        dto.setDateModification(rapport.getDateModification());

        return dto;
    }

    /*
     * DTO -> Entity
     */
    private Rapport mapToEntity(RapportDTO dto) {

        Rapport r = new Rapport();

        r.setId(dto.getId());
        r.setTitre(dto.getTitre());
        r.setTypeRapport(dto.getTypeRapport());
        r.setAuteurNom(dto.getAuteurNom());
        r.setAuteurEmail(dto.getAuteurEmail());
        r.setContenu(dto.getContenu());
        r.setStatut(dto.getStatut());
        r.setVerificateursAssignes(
                dto.getVerificateursAssignes()
        );
        r.setNomFichier(dto.getNomFichier());
        r.setCheminFichier(dto.getCheminFichier());

        /*
         * IMPORTANT :
         * transfert de fileSize vers l'entité.
         */
        r.setFileSize(dto.getFileSize());

        r.setCommentaires(dto.getCommentaires());

        return r;
    }
}