package meci_e_services.repository;

import meci_e_services.entity.RapportAutorisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RapportAutorisationRepository extends JpaRepository<RapportAutorisation, Long> {

    List<RapportAutorisation> findByBeneficiaireId(Long beneficiaireId);

    List<RapportAutorisation> findByProprietaireId(Long proprietaireId);

    List<RapportAutorisation> findByBeneficiaireIdAndProprietaireId(Long beneficiaireId, Long proprietaireId);

    boolean existsByBeneficiaireIdAndRapportIdAndTousRapportsFalse(Long beneficiaireId, Long rapportId);

    @Modifying
    @Query("DELETE FROM RapportAutorisation r WHERE r.rapportId = :rapportId")
    void deleteByRapportId(@Param("rapportId") Long rapportId);

    @Modifying
    @Query("DELETE FROM RapportAutorisation r WHERE r.beneficiaireId = :beneficiaireId AND r.proprietaireId = :proprietaireId")
    void deleteByBeneficiaireIdAndProprietaireId(@Param("beneficiaireId") Long beneficiaireId, @Param("proprietaireId") Long proprietaireId);

    void deleteByBeneficiaireIdAndProprietaireIdAndRapportId(
            Long beneficiaireId,
            Long proprietaireId,
            Long rapportId
    );
}
