package meci_e_services.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Représente une demande de partage d'un rapport par son propriétaire
 * à un autre utilisateur. L'admin doit approuver ou rejeter la demande.
 *
 * Flux :
 *  1. L'utilisateur (propriétaire) crée une demande → statut PENDING
 *  2. L'admin approuve (APPROVED) → le partage effectif est créé dans RapportAutorisation
 *     L'admin rejette (REJECTED) → pas de partage
 */
@Entity
@Table(name = "report_share_requests")
public class ReportShareRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** ID du rapport à partager. */
    @Column(name = "rapport_id", nullable = false)
    private Long rapportId;

    /** Titre du rapport (dénormalisé pour affichage). */
    @Column(name = "rapport_titre", length = 255)
    private String rapportTitre;

    /** ID de l'utilisateur propriétaire du rapport (celui qui demande le partage). */
    @Column(name = "proprietaire_id", nullable = false)
    private Long proprietaireId;

    /** Nom complet du propriétaire (dénormalisé). */
    @Column(name = "proprietaire_nom", length = 300)
    private String proprietaireNom;

    /** ID de l'utilisateur bénéficiaire (celui qui recevra l'accès). */
    @Column(name = "beneficiaire_id", nullable = false)
    private Long beneficiaireId;

    /** Nom complet du bénéficiaire (dénormalisé). */
    @Column(name = "beneficiaire_nom", length = 300)
    private String beneficiaireNom;

    /** Statut de la demande. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ShareRequestStatus status = ShareRequestStatus.PENDING;

    /** Message optionnel du propriétaire pour justifier la demande. */
    @Column(columnDefinition = "TEXT")
    private String motif;

    /** Commentaire de l'admin lors de l'approbation ou du rejet. */
    @Column(name = "admin_commentaire", columnDefinition = "TEXT")
    private String adminCommentaire;

    /** Date de création de la demande. */
    @Column(name = "date_demande", nullable = false)
    private LocalDateTime dateDemande;

    /** Date de traitement par l'admin. */
    @Column(name = "date_traitement")
    private LocalDateTime dateTraitement;

    @PrePersist
    protected void onCreate() {
        if (this.dateDemande == null) {
            this.dateDemande = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = ShareRequestStatus.PENDING;
        }
    }

    public ReportShareRequest() {
    }

    // ── Getters / Setters ──

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
