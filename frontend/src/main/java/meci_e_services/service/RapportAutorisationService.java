package meci_e_services.service;

import meci_e_services.dto.AutorisationDTO;
import meci_e_services.dto.AutorisationUtilisateurDTO;
import meci_e_services.dto.DelegationAutorisationBatchRequestDTO;
import meci_e_services.dto.DelegationAutorisationRequestDTO;
import meci_e_services.dto.RetraitPartageDTO;
import meci_e_services.entity.AppUsers;
import meci_e_services.entity.Rapport;
import meci_e_services.entity.RapportAutorisation;
import meci_e_services.repository.AppUsersRepository;
import meci_e_services.repository.RapportAutorisationRepository;
import meci_e_services.repository.RapportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class RapportAutorisationService {

    private final RapportAutorisationRepository repository;
    private final RapportRepository rapportRepository;
    private final AppUsersRepository appUsersRepository;
    private final SharePermissionService sharePermissionService;

    public RapportAutorisationService(
            RapportAutorisationRepository repository,
            RapportRepository rapportRepository,
            AppUsersRepository appUsersRepository,
            SharePermissionService sharePermissionService) {
        this.repository = repository;
        this.rapportRepository = rapportRepository;
        this.appUsersRepository = appUsersRepository;
        this.sharePermissionService = sharePermissionService;
    }

    public List<AutorisationDTO> getAllAutorisations() {
        return repository.findAll().stream()
                .map(a -> new AutorisationDTO(
                        a.getBeneficiaireId(),
                        a.getBeneficiaireNom(),
                        a.getProprietaireId(),
                        a.getProprietaireNom(),
                        a.getTousRapports()
                ))
                .collect(Collectors.toList());
    }

    public List<RapportAutorisation> getPartagesEnregistres() {
        return repository.findAll();
    }

    public List<RapportAutorisation> getPartagesDuProprietaire(String emailConnecte) {
        sharePermissionService.requireCanShareOwnReports(emailConnecte);
        AppUsers proprietaire = appUsersRepository.findByEmailIgnoreCase(emailConnecte)
                .orElseThrow(() -> new IllegalArgumentException("Propriétaire introuvable."));
        return repository.findByProprietaireId(proprietaire.getId());
    }

    public List<RapportAutorisation> getAutorisationsParCouple(Long beneficiaireId, Long proprietaireId) {
        return repository.findByBeneficiaireIdAndProprietaireId(beneficiaireId, proprietaireId);
    }

    public List<Long> getBeneficiaireIdsAutorises(Long proprietaireId) {
        return repository.findByProprietaireId(proprietaireId).stream()
                .map(RapportAutorisation::getBeneficiaireId)
                .distinct()
                .collect(Collectors.toList());
    }

    public void verifierProprietaire(Long proprietaireId, String emailConnecte) {
        if (proprietaireId == null) {
            throw new IllegalArgumentException("Propriétaire obligatoire.");
        }
        AppUsers proprietaire = appUsersRepository.findById(proprietaireId)
                .orElseThrow(() -> new IllegalArgumentException("Propriétaire introuvable."));
        if (emailConnecte != null && proprietaire.getEmail().equalsIgnoreCase(emailConnecte)) {
            sharePermissionService.requireCanShareOwnReports(emailConnecte);
            return;
        }
        if (!sharePermissionService.canManageAnyOwner(emailConnecte)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Vous ne pouvez gérer que vos propres autorisations.");
        }
    }

    public void enregistrerPartagesGlobalement(
            List<DelegationAutorisationBatchRequestDTO> partages,
            String emailConnecte) {
        sharePermissionService.requireShareManager(emailConnecte);
        if (partages == null) {
            throw new IllegalArgumentException("Les partages à enregistrer sont obligatoires.");
        }
        Set<Long> proprietairesTraites = new HashSet<>();
        for (DelegationAutorisationBatchRequestDTO partage : partages) {
            if (partage == null || partage.getProprietaireId() == null
                    || !proprietairesTraites.add(partage.getProprietaireId())) {
                throw new IllegalArgumentException("Chaque propriétaire doit être indiqué une seule fois.");
            }
            enregistrerDelegations(partage);
        }
    }

    public void accorderPartages(
            List<DelegationAutorisationBatchRequestDTO> partages,
            String emailConnecte) {
        sharePermissionService.requireShareManager(emailConnecte);
        enregistrerNouveauxPartages(partages);
    }

    public void accorderMesPartages(
            List<DelegationAutorisationBatchRequestDTO> partages,
            String emailConnecte) {
        sharePermissionService.requireCanShareOwnReports(emailConnecte);
        AppUsers proprietaireConnecte = appUsersRepository.findByEmailIgnoreCase(emailConnecte)
                .orElseThrow(() -> new IllegalArgumentException("Propriétaire introuvable."));
        if (partages == null || partages.stream().anyMatch(partage ->
                partage == null || !proprietaireConnecte.getId().equals(partage.getProprietaireId()))) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Vous ne pouvez partager que vos propres rapports.");
        }
        enregistrerNouveauxPartages(partages);
    }

    private void enregistrerNouveauxPartages(List<DelegationAutorisationBatchRequestDTO> partages) {
        if (partages == null || partages.isEmpty()) {
            throw new IllegalArgumentException("Sélectionnez au moins un propriétaire, des rapports et des bénéficiaires.");
        }

        for (DelegationAutorisationBatchRequestDTO partage : partages) {
            if (partage == null || partage.getProprietaireId() == null
                    || partage.getAutorisations() == null || partage.getAutorisations().isEmpty()) {
                throw new IllegalArgumentException("Chaque partage doit indiquer un propriétaire et au moins un bénéficiaire.");
            }

            AppUsers proprietaire = appUsersRepository.findById(partage.getProprietaireId())
                    .orElseThrow(() -> new IllegalArgumentException("Propriétaire introuvable."));
            Set<Long> beneficiairesTraites = new HashSet<>();

            for (AutorisationUtilisateurDTO autorisation : partage.getAutorisations()) {
                if (autorisation == null || autorisation.getBeneficiaireId() == null
                        || !beneficiairesTraites.add(autorisation.getBeneficiaireId())) {
                    throw new IllegalArgumentException("Chaque bénéficiaire doit être renseigné une seule fois par propriétaire.");
                }
                if (autorisation.getBeneficiaireId().equals(proprietaire.getId())) {
                    throw new IllegalArgumentException("Un utilisateur ne peut pas se partager ses propres rapports.");
                }

                AppUsers beneficiaire = appUsersRepository.findById(autorisation.getBeneficiaireId())
                        .orElseThrow(() -> new IllegalArgumentException("Bénéficiaire introuvable."));
                if (!Boolean.TRUE.equals(beneficiaire.getActif())) {
                    throw new IllegalArgumentException("Le bénéficiaire est désactivé.");
                }

                List<Long> rapportIds = autorisation.getRapportIds() == null
                        ? List.of()
                        : autorisation.getRapportIds().stream().filter(java.util.Objects::nonNull).distinct().collect(Collectors.toList());
                if (rapportIds.isEmpty()) {
                    throw new IllegalArgumentException(
                            "Sélectionnez explicitement les rapports à partager. L’accès à tous les rapports d’une personne n’est pas autorisé."
                    );
                }

                List<Rapport> rapports = rapportRepository.findAllById(rapportIds);
                if (rapports.size() != rapportIds.size() || rapports.stream().anyMatch(rapport ->
                        rapport.getAuteurEmail() == null
                                || !rapport.getAuteurEmail().equalsIgnoreCase(proprietaire.getEmail()))) {
                    throw new IllegalArgumentException("Un ou plusieurs rapports n’appartiennent pas au propriétaire sélectionné.");
                }

                for (Rapport rapport : rapports) {
                    if (repository.existsByBeneficiaireIdAndRapportIdAndTousRapportsFalse(
                            beneficiaire.getId(),
                            rapport.getId()
                    )) {
                        continue;
                    }
                    repository.save(new RapportAutorisation(
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
        }
    }

    public void retirerPartage(
            Long proprietaireId,
            Long beneficiaireId,
            Long rapportId,
            String emailConnecte) {
        verifierProprietaire(proprietaireId, emailConnecte);
        if (proprietaireId == null || beneficiaireId == null || rapportId == null) {
            throw new IllegalArgumentException("Le propriétaire, le bénéficiaire et le rapport sont obligatoires.");
        }
        repository.deleteByBeneficiaireIdAndProprietaireIdAndRapportId(
                beneficiaireId,
                proprietaireId,
                rapportId
        );
    }

    public void retirerPartages(List<RetraitPartageDTO> retraits, String emailConnecte) {
        sharePermissionService.requireShareManager(emailConnecte);
        if (retraits == null || retraits.isEmpty()) {
            throw new IllegalArgumentException("Sélectionnez au moins un droit à retirer.");
        }
        for (RetraitPartageDTO retrait : retraits) {
            if (retrait == null || retrait.getProprietaireId() == null
                    || retrait.getBeneficiaireId() == null || retrait.getRapportId() == null) {
                throw new IllegalArgumentException("Chaque droit à retirer doit identifier le propriétaire, le bénéficiaire et le rapport.");
            }
        }
        for (RetraitPartageDTO retrait : retraits) {
            repository.deleteByBeneficiaireIdAndProprietaireIdAndRapportId(
                    retrait.getBeneficiaireId(),
                    retrait.getProprietaireId(),
                    retrait.getRapportId()
            );
        }
    }

    public void enregistrerDelegations(DelegationAutorisationBatchRequestDTO dto) {
        if (dto == null || dto.getProprietaireId() == null || dto.getAutorisations() == null) {
            throw new IllegalArgumentException("Les autorisations à enregistrer sont obligatoires.");
        }

        AppUsers proprietaire = appUsersRepository.findById(dto.getProprietaireId())
                .orElseThrow(() -> new IllegalArgumentException("Propriétaire introuvable."));
        Set<Long> beneficiairesTraites = new HashSet<>();

        for (AutorisationUtilisateurDTO autorisation : dto.getAutorisations()) {
            if (autorisation == null || autorisation.getBeneficiaireId() == null ||
                    !beneficiairesTraites.add(autorisation.getBeneficiaireId())) {
                throw new IllegalArgumentException("Chaque bénéficiaire doit être renseigné une seule fois.");
            }
            if (autorisation.getBeneficiaireId().equals(proprietaire.getId())) {
                throw new IllegalArgumentException("Un utilisateur ne peut pas se partager ses propres rapports.");
            }
            AppUsers beneficiaire = appUsersRepository.findById(autorisation.getBeneficiaireId())
                    .orElseThrow(() -> new IllegalArgumentException("Bénéficiaire introuvable."));
            List<Long> rapportIds = autorisation.getRapportIds() == null
                    ? List.of()
                    : autorisation.getRapportIds().stream().distinct().collect(Collectors.toList());
            if (!Boolean.TRUE.equals(beneficiaire.getActif()) && !rapportIds.isEmpty()) {
                Set<Long> rapportsDejaAutorises = repository
                        .findByBeneficiaireIdAndProprietaireId(
                                beneficiaire.getId(),
                                proprietaire.getId()
                        ).stream()
                        .map(RapportAutorisation::getRapportId)
                        .filter(java.util.Objects::nonNull)
                        .collect(Collectors.toSet());
                if (!rapportsDejaAutorises.containsAll(rapportIds)) {
                    throw new IllegalArgumentException("Le bénéficiaire est désactivé.");
                }
            }
            List<Rapport> rapports = rapportIds.isEmpty()
                    ? List.of()
                    : rapportRepository.findAllById(rapportIds);
            if (rapports.size() != rapportIds.size() || rapports.stream().anyMatch(rapport ->
                    rapport.getAuteurEmail() == null ||
                            !rapport.getAuteurEmail().equalsIgnoreCase(proprietaire.getEmail()))) {
                throw new IllegalArgumentException("Un ou plusieurs rapports ne vous appartiennent pas.");
            }

            autorisation.setRapportIds(rapportIds);
            autorisation.setBeneficiaireId(beneficiaire.getId());
        }

        for (AutorisationUtilisateurDTO autorisation : dto.getAutorisations()) {
            AppUsers beneficiaire = appUsersRepository.findById(autorisation.getBeneficiaireId())
                    .orElseThrow(() -> new IllegalArgumentException("Bénéficiaire introuvable."));
            repository.deleteByBeneficiaireIdAndProprietaireId(
                    beneficiaire.getId(),
                    proprietaire.getId()
            );
            for (Rapport rapport : rapportRepository.findAllById(autorisation.getRapportIds())) {
                repository.save(new RapportAutorisation(
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
    }

    public void enregistrerDelegation(DelegationAutorisationRequestDTO dto) {
        if (dto == null || dto.getBeneficiaireIds() == null || dto.getBeneficiaireIds().isEmpty()) {
            throw new IllegalArgumentException("Au moins un bénéficiaire est obligatoire.");
        }
        if (Boolean.TRUE.equals(dto.getTousRapports())) {
            throw new IllegalArgumentException("Sélectionnez explicitement les rapports à partager.");
        }
        List<AutorisationUtilisateurDTO> autorisations = dto.getBeneficiaireIds().stream()
                .map(beneficiaireId -> {
                    AutorisationUtilisateurDTO autorisation = new AutorisationUtilisateurDTO();
                    autorisation.setBeneficiaireId(beneficiaireId);
                    autorisation.setRapportIds(dto.getRapportIds());
                    return autorisation;
                })
                .collect(Collectors.toList());
        DelegationAutorisationBatchRequestDTO batch = new DelegationAutorisationBatchRequestDTO();
        batch.setProprietaireId(dto.getProprietaireId());
        batch.setAutorisations(autorisations);
        enregistrerDelegations(batch);
    }
}
