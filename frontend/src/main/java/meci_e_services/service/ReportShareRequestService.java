package meci_e_services.service;

import meci_e_services.dto.ShareRequestDTO;
import meci_e_services.entity.AppUsers;
import meci_e_services.entity.Rapport;
import meci_e_services.entity.RapportAutorisation;
import meci_e_services.entity.ReportShareRequest;
import meci_e_services.entity.ShareRequestStatus;
import meci_e_services.repository.AppUsersRepository;
import meci_e_services.repository.RapportAutorisationRepository;
import meci_e_services.repository.RapportRepository;
import meci_e_services.repository.ReportShareRequestRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReportShareRequestService {

    private final ReportShareRequestRepository shareRequestRepository;
    private final RapportRepository rapportRepository;
    private final RapportAutorisationRepository autorisationRepository;
    private final AppUsersRepository appUsersRepository;
    private final SharePermissionService sharePermissionService;

    public ReportShareRequestService(
            ReportShareRequestRepository shareRequestRepository,
            RapportRepository rapportRepository,
            RapportAutorisationRepository autorisationRepository,
            AppUsersRepository appUsersRepository,
            SharePermissionService sharePermissionService) {
        this.shareRequestRepository = shareRequestRepository;
        this.rapportRepository = rapportRepository;
        this.autorisationRepository = autorisationRepository;
        this.appUsersRepository = appUsersRepository;
        this.sharePermissionService = sharePermissionService;
    }

    // ─────────────────────────────────────────────────────────────
    //  UTILISATEUR : créer une demande de partage
    // ─────────────────────────────────────────────────────────────

    /**
     * L'utilisateur connecté crée une ou plusieurs demandes de partage
     * pour ses propres rapports vers un ou plusieurs bénéficiaires.
     * Chaque combinaison (rapport, bénéficiaire) génère une demande distincte.
     *
     * L'option de partage n'est visible que si l'admin a autorisé l'utilisateur
     * (champ peutPartagerRapports = true, ou superAdmin, ou gestionnairePartage).
     */
    public List<ShareRequestDTO> creerDemandes(ShareRequestDTO dto, String emailConnecte) {

        // Vérifier que l'utilisateur a le droit de partager
        sharePermissionService.requireCanShareOwnReports(emailConnecte);

        AppUsers proprietaire = appUsersRepository.findByEmailIgnoreCase(emailConnecte)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable."));

        // Valider les rapports
        List<Long> rapportIds = dto.getRapportIds();
        if (rapportIds == null || rapportIds.isEmpty()) {
            throw new IllegalArgumentException("Sélectionnez au moins un rapport à partager.");
        }
        List<Rapport> rapports = rapportRepository.findAllById(rapportIds);
        if (rapports.size() != rapportIds.size()) {
            throw new IllegalArgumentException("Un ou plusieurs rapports sont introuvables.");
        }
        // Vérifier que tous les rapports appartiennent au propriétaire
        for (Rapport rapport : rapports) {
            if (rapport.getAuteurEmail() == null
                    || !rapport.getAuteurEmail().equalsIgnoreCase(proprietaire.getEmail())) {
                throw new AccessDeniedException(
                        "Vous ne pouvez partager que vos propres rapports.");
            }
        }

        // Valider les bénéficiaires
        List<Long> beneficiaireIds = dto.getBeneficiaireIds();
        if (beneficiaireIds == null || beneficiaireIds.isEmpty()) {
            throw new IllegalArgumentException("Sélectionnez au moins un bénéficiaire.");
        }
        List<AppUsers> beneficiaires = appUsersRepository.findAllById(beneficiaireIds);
        if (beneficiaires.size() != beneficiaireIds.size()) {
            throw new IllegalArgumentException("Un ou plusieurs bénéficiaires sont introuvables.");
        }

        List<ShareRequestDTO> resultats = new ArrayList<>();

        for (Rapport rapport : rapports) {
            for (AppUsers beneficiaire : beneficiaires) {
                if (Objects.equals(beneficiaire.getId(), proprietaire.getId())) {
                    throw new IllegalArgumentException(
                            "Vous ne pouvez pas partager un rapport avec vous-même.");
                }
                if (!Boolean.TRUE.equals(beneficiaire.getActif())) {
                    throw new IllegalArgumentException(
                            "Le bénéficiaire « " + beneficiaire.getPrenom() + " "
                                    + beneficiaire.getNom() + " » est désactivé.");
                }

                // Vérifier qu'il n'y a pas déjà une demande PENDING identique
                if (shareRequestRepository.existsByRapportIdAndBeneficiaireIdAndStatus(
                        rapport.getId(), beneficiaire.getId(), ShareRequestStatus.PENDING)) {
                    continue; // ignorer les doublons silencieusement
                }

                // Vérifier que le partage n'existe pas déjà
                if (autorisationRepository.existsByBeneficiaireIdAndRapportIdAndTousRapportsFalse(
                        beneficiaire.getId(), rapport.getId())) {
                    continue; // déjà partagé
                }

                ReportShareRequest request = new ReportShareRequest();
                request.setRapportId(rapport.getId());
                request.setRapportTitre(rapport.getTitre());
                request.setProprietaireId(proprietaire.getId());
                request.setProprietaireNom(proprietaire.getPrenom() + " " + proprietaire.getNom());
                request.setBeneficiaireId(beneficiaire.getId());
                request.setBeneficiaireNom(beneficiaire.getPrenom() + " " + beneficiaire.getNom());
                request.setMotif(dto.getMotif());

                ReportShareRequest saved = shareRequestRepository.save(request);
                resultats.add(mapToDTO(saved));
            }
        }

        if (resultats.isEmpty()) {
            throw new IllegalArgumentException(
                    "Aucune nouvelle demande créée : les partages existent déjà ou des demandes sont en attente.");
        }

        return resultats;
    }

    // ─────────────────────────────────────────────────────────────
    //  UTILISATEUR : consulter ses propres demandes
    // ─────────────────────────────────────────────────────────────

    /**
     * Retourne les demandes de partage créées par l'utilisateur connecté.
     */
    public List<ShareRequestDTO> getMesDemandes(String emailConnecte) {
        AppUsers proprietaire = appUsersRepository.findByEmailIgnoreCase(emailConnecte)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable."));
        return shareRequestRepository.findByProprietaireId(proprietaire.getId())
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────────────
    //  UTILISATEUR : vérifier s'il a le droit de partager
    // ─────────────────────────────────────────────────────────────

    /**
     * Retourne true si l'utilisateur connecté a le droit de créer
     * des demandes de partage (option visible dans le frontend).
     */
    public boolean peutPartager(String emailConnecte) {
        return sharePermissionService.canShareOwnReports(emailConnecte);
    }

    // ─────────────────────────────────────────────────────────────
    //  ADMIN : lister les demandes en attente
    // ─────────────────────────────────────────────────────────────

    /**
     * L'admin consulte toutes les demandes en attente.
     */
    public List<ShareRequestDTO> getDemandesEnAttente(String emailAdmin) {
        sharePermissionService.requireShareManager(emailAdmin);
        return shareRequestRepository.findByStatus(ShareRequestStatus.PENDING)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * L'admin consulte toutes les demandes (tous statuts).
     */
    public List<ShareRequestDTO> getToutesDemandes(String emailAdmin) {
        sharePermissionService.requireShareManager(emailAdmin);
        return shareRequestRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────────────
    //  ADMIN : approuver une demande
    // ─────────────────────────────────────────────────────────────

    /**
     * L'admin approuve la demande → crée le partage effectif
     * dans la table rapport_autorisations.
     */
    public ShareRequestDTO approuverDemande(Long demandeId, String commentaire, String emailAdmin) {
        sharePermissionService.requireShareManager(emailAdmin);

        ReportShareRequest request = shareRequestRepository.findById(demandeId)
                .orElseThrow(() -> new IllegalArgumentException("Demande introuvable."));

        if (request.getStatus() != ShareRequestStatus.PENDING) {
            throw new IllegalArgumentException("Cette demande a déjà été traitée.");
        }

        request.setStatus(ShareRequestStatus.APPROVED);
        request.setAdminCommentaire(commentaire);
        request.setDateTraitement(LocalDateTime.now());

        // Créer le partage effectif dans RapportAutorisation
        if (!autorisationRepository.existsByBeneficiaireIdAndRapportIdAndTousRapportsFalse(
                request.getBeneficiaireId(), request.getRapportId())) {

            AppUsers proprietaire = appUsersRepository.findById(request.getProprietaireId())
                    .orElse(null);
            AppUsers beneficiaire = appUsersRepository.findById(request.getBeneficiaireId())
                    .orElse(null);
            Rapport rapport = rapportRepository.findById(request.getRapportId())
                    .orElse(null);

            if (proprietaire != null && beneficiaire != null && rapport != null) {
                autorisationRepository.save(new RapportAutorisation(
                        beneficiaire.getId(),
                        beneficiaire.getPrenom() + " " + beneficiaire.getNom(),
                        proprietaire.getId(),
                        proprietaire.getPrenom() + " " + proprietaire.getNom(),
                        false,
                        rapport.getId(),
                        rapport.getTitre()
                ));
            }
        }

        return mapToDTO(shareRequestRepository.save(request));
    }

    // ─────────────────────────────────────────────────────────────
    //  ADMIN : rejeter une demande
    // ─────────────────────────────────────────────────────────────

    /**
     * L'admin rejette la demande. Pas de partage créé.
     */
    public ShareRequestDTO rejeterDemande(Long demandeId, String commentaire, String emailAdmin) {
        sharePermissionService.requireShareManager(emailAdmin);

        ReportShareRequest request = shareRequestRepository.findById(demandeId)
                .orElseThrow(() -> new IllegalArgumentException("Demande introuvable."));

        if (request.getStatus() != ShareRequestStatus.PENDING) {
            throw new IllegalArgumentException("Cette demande a déjà été traitée.");
        }

        request.setStatus(ShareRequestStatus.REJECTED);
        request.setAdminCommentaire(commentaire);
        request.setDateTraitement(LocalDateTime.now());

        return mapToDTO(shareRequestRepository.save(request));
    }

    // ─────────────────────────────────────────────────────────────
    //  Mapping
    // ─────────────────────────────────────────────────────────────

    private ShareRequestDTO mapToDTO(ReportShareRequest entity) {
        ShareRequestDTO dto = new ShareRequestDTO();
        dto.setId(entity.getId());
        dto.setRapportId(entity.getRapportId());
        dto.setRapportTitre(entity.getRapportTitre());
        dto.setProprietaireId(entity.getProprietaireId());
        dto.setProprietaireNom(entity.getProprietaireNom());
        dto.setBeneficiaireId(entity.getBeneficiaireId());
        dto.setBeneficiaireNom(entity.getBeneficiaireNom());
        dto.setStatus(entity.getStatus());
        dto.setMotif(entity.getMotif());
        dto.setAdminCommentaire(entity.getAdminCommentaire());
        dto.setDateDemande(entity.getDateDemande());
        dto.setDateTraitement(entity.getDateTraitement());
        return dto;
    }
}
