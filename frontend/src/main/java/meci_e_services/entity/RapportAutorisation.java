package meci_e_services.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "rapport_autorisations")
public class RapportAutorisation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "beneficiaire_id", nullable = false)
    private Long beneficiaireId;

    @Column(name = "beneficiaire_nom")
    private String beneficiaireNom;

    @Column(name = "proprietaire_id", nullable = false)
    private Long proprietaireId;

    @Column(name = "proprietaire_nom")
    private String proprietaireNom;

    @Column(name = "tous_rapports", nullable = false)
    private Boolean tousRapports = false;

    @Column(name = "autorise", nullable = false)
    private Boolean autorise = true;

    @Column(name = "rapport_id")
    private Long rapportId;

    @Column(name = "rapport_titre")
    private String rapportTitre;

    public RapportAutorisation() {
    }

    public RapportAutorisation(Long beneficiaireId, String beneficiaireNom, Long proprietaireId, String proprietaireNom, Boolean tousRapports, Long rapportId, String rapportTitre) {
        this.beneficiaireId = beneficiaireId;
        this.beneficiaireNom = beneficiaireNom;
        this.proprietaireId = proprietaireId;
        this.proprietaireNom = proprietaireNom;
        this.tousRapports = tousRapports != null ? tousRapports : false;
        this.autorise = true;
        this.rapportId = rapportId;
        this.rapportTitre = rapportTitre;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBeneficiaireId() {
        return beneficiaireId;
    }

    public void setBeneficiaireId(Long beneficiaireId) {
        this.beneficiaireId = beneficiaireId;
    }

    public String getBeneficiaireNom() {
        return beneficiaireNom;
    }

    public void setBeneficiaireNom(String beneficiaireNom) {
        this.beneficiaireNom = beneficiaireNom;
    }

    public Long getProprietaireId() {
        return proprietaireId;
    }

    public void setProprietaireId(Long proprietaireId) {
        this.proprietaireId = proprietaireId;
    }

    public String getProprietaireNom() {
        return proprietaireNom;
    }

    public void setProprietaireNom(String proprietaireNom) {
        this.proprietaireNom = proprietaireNom;
    }

    public Boolean getTousRapports() {
        return tousRapports;
    }

    public void setTousRapports(Boolean tousRapports) {
        this.tousRapports = tousRapports;
    }

    public Boolean getAutorise() {
        return autorise;
    }

    public void setAutorise(Boolean autorise) {
        this.autorise = autorise;
    }

    public Long getRapportId() {
        return rapportId;
    }

    public void setRapportId(Long rapportId) {
        this.rapportId = rapportId;
    }

    public String getRapportTitre() {
        return rapportTitre;
    }

    public void setRapportTitre(String rapportTitre) {
        this.rapportTitre = rapportTitre;
    }
}
