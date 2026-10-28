package meci_e_services.dto;

import meci_e_services.entity.ShareRequestStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO pour les demandes de partage de rapports.
 * Utilisé à la fois pour la création (request) et la réponse (response).
 */
public class ShareRequestDTO {

    private Long id;

    /** Liste de rapport IDs à partager (utilisé lors de la création). */
    private List<Long> rapportIds;

    /** ID d'un rapport unique (rempli dans la réponse). */
    private Long rapportId;
    private String rapportTitre;

    private Long proprietaireId;
    private String proprietaireNom;

    /** Liste de bénéficiaire IDs (utilisé lors de la création). */
    private List<Long> beneficiaireIds;

    /** ID d'un bénéficiaire unique (rempli dans la réponse). */
    private Long beneficiaireId;
    private String beneficiaireNom;

    private ShareRequestStatus status;
    private String motif;
    private String adminCommentaire;
    private LocalDateTime dateDemande;
    private LocalDateTime dateTraitement;

    public ShareRequestDTO() {
    }

    // ── Getters / Setters ──

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Long> getRapportIds() {
        return rapportIds;
    }

    public void setRapportIds(List<Long> rapportIds) {
        this.rapportIds = rapportIds;
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

    public List<Long> getBeneficiaireIds() {
        return beneficiaireIds;
    }

    public void setBeneficiaireIds(List<Long> beneficiaireIds) {
        this.beneficiaireIds = beneficiaireIds;
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

    public ShareRequestStatus getStatus() {
        return status;
    }

    public void setStatus(ShareRequestStatus status) {
        this.status = status;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public String getAdminCommentaire() {
        return adminCommentaire;
    }

    public void setAdminCommentaire(String adminCommentaire) {
        this.adminCommentaire = adminCommentaire;
    }

    public LocalDateTime getDateDemande() {
        return dateDemande;
    }

    public void setDateDemande(LocalDateTime dateDemande) {
        this.dateDemande = dateDemande;
    }

    public LocalDateTime getDateTraitement() {
        return dateTraitement;
    }

    public void setDateTraitement(LocalDateTime dateTraitement) {
        this.dateTraitement = dateTraitement;
    }
}
