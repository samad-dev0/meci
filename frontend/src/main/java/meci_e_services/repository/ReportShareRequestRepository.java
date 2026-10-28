package meci_e_services.repository;

import meci_e_services.entity.ReportShareRequest;
import meci_e_services.entity.ShareRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportShareRequestRepository extends JpaRepository<ReportShareRequest, Long> {

    /** Toutes les demandes faites par un propriétaire donné. */
    List<ReportShareRequest> findByProprietaireId(Long proprietaireId);

    /** Toutes les demandes d'un statut donné (pour l'admin : lister les PENDING). */
    List<ReportShareRequest> findByStatus(ShareRequestStatus status);

    /** Vérifier si une demande identique est déjà en attente. */
    boolean existsByRapportIdAndBeneficiaireIdAndStatus(
            Long rapportId,
            Long beneficiaireId,
            ShareRequestStatus status
    );
}
