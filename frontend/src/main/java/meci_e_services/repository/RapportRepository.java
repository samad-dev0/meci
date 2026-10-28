package meci_e_services.repository;

import meci_e_services.entity.Rapport;
import meci_e_services.entity.enums.StatutRapport;
import meci_e_services.entity.enums.TypeRapport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RapportRepository extends JpaRepository<Rapport, Long> {

    List<Rapport> findByStatut(StatutRapport statut);

    List<Rapport> findByTypeRapport(TypeRapport typeRapport);

    List<Rapport> findByAuteurEmailIgnoreCase(String auteurEmail);

    @Query("SELECT r FROM Rapport r WHERE " +
            "(:recherche IS NULL OR LOWER(r.titre) LIKE LOWER(CONCAT('%', :recherche, '%')) OR LOWER(r.auteurNom) LIKE LOWER(CONCAT('%', :recherche, '%'))) AND " +
            "(:statut IS NULL OR r.statut = :statut) AND " +
            "(:typeRapport IS NULL OR r.typeRapport = :typeRapport)")
    List<Rapport> meciSearch(
            @Param("recherche") String recherche,
            @Param("statut") StatutRapport statut,
            @Param("typeRapport") TypeRapport typeRapport
    );
}
