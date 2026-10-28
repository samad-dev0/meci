package meci_e_services.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import meci_e_services.entity.enums.StatutAffectation;
import meci_e_services.entity.enums.StatutDelai;
import meci_e_services.entity.enums.TypeAffectation;

@Entity
@Table(
        name = "affectations",
        indexes = {
                @Index(name = "idx_affectation_courrier", columnList = "courrier_id"),
                @Index(name = "idx_affectation_destinataire", columnList = "affecte_a_id"),
                @Index(name = "idx_affectation_statut", columnList = "statut"),
                @Index(name = "idx_affectation_date_limite", columnList = "date_limite")
        }
)
public class Affectation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "courrier_id", nullable = false)
    private Courrier courrier;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "affecte_par_id", nullable = false)
    private AppUsers affectePar;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "affecte_a_id", nullable = false)
    private AppUsers affecteA;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_affectation", nullable = false, length = 30)
    private TypeAffectation typeAffectation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutAffectation statut = StatutAffectation.EN_ATTENTE;

    @Column(name = "date_affectation", nullable = false)
    private LocalDateTime dateAffectation;

    @Column(name = "delai_jours")
    private Integer delaiJours;

    @Column(name = "date_limite")
    private LocalDate dateLimite;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_delai", nullable = false, length = 20)
    private StatutDelai statutDelai = StatutDelai.EN_ATTENTE;

    @Column(name = "date_reponse_delai")
    private LocalDateTime dateReponseDelai;

    @Column(name = "motif_refus_delai", columnDefinition = "TEXT")
    private String motifRefusDelai;

    @Column(name = "delai_propose")
    private Integer delaiPropose;

    @Column(name = "commentaire", columnDefinition = "TEXT")
    private String commentaire;

    @Column(name = "date_traitement")
    private LocalDateTime dateTraitement;

    @Column(name = "date_retour")
    private LocalDateTime dateRetour;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @PrePersist
    protected void onCreate() {

        LocalDateTime maintenant = LocalDateTime.now();

        dateCreation = maintenant;

        if (dateAffectation == null) {
            dateAffectation = maintenant;
        }
    }

    public Affectation() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Courrier getCourrier() {
        return courrier;
    }

    public void setCourrier(Courrier courrier) {
        this.courrier = courrier;
    }

    public AppUsers getAffectePar() {
        return affectePar;
    }

    public void setAffectePar(AppUsers affectePar) {
        this.affectePar = affectePar;
    }

    public AppUsers getAffecteA() {
        return affecteA;
    }

    public void setAffecteA(AppUsers affecteA) {
        this.affecteA = affecteA;
    }

    public TypeAffectation getTypeAffectation() {
        return typeAffectation;
    }

    public void setTypeAffectation(TypeAffectation typeAffectation) {
        this.typeAffectation = typeAffectation;
    }

    public StatutAffectation getStatut() {
        return statut;
    }

    public void setStatut(StatutAffectation statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateAffectation() {
        return dateAffectation;
    }

    public void setDateAffectation(LocalDateTime dateAffectation) {
        this.dateAffectation = dateAffectation;
    }

    public Integer getDelaiJours() {
        return delaiJours;
    }

    public void setDelaiJours(Integer delaiJours) {
        this.delaiJours = delaiJours;
    }

    public LocalDate getDateLimite() {
        return dateLimite;
    }

    public void setDateLimite(LocalDate dateLimite) {
        this.dateLimite = dateLimite;
    }

    public StatutDelai getStatutDelai() {
        return statutDelai;
    }

    public void setStatutDelai(StatutDelai statutDelai) {
        this.statutDelai = statutDelai;
    }

    public LocalDateTime getDateReponseDelai() {
        return dateReponseDelai;
    }

    public void setDateReponseDelai(LocalDateTime dateReponseDelai) {
        this.dateReponseDelai = dateReponseDelai;
    }

    public String getMotifRefusDelai() {
        return motifRefusDelai;
    }

    public void setMotifRefusDelai(String motifRefusDelai) {
        this.motifRefusDelai = motifRefusDelai;
    }

    public Integer getDelaiPropose() {
        return delaiPropose;
    }

    public void setDelaiPropose(Integer delaiPropose) {
        this.delaiPropose = delaiPropose;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public LocalDateTime getDateTraitement() {
        return dateTraitement;
    }

    public void setDateTraitement(LocalDateTime dateTraitement) {
        this.dateTraitement = dateTraitement;
    }

    public LocalDateTime getDateRetour() {
        return dateRetour;
    }

    public void setDateRetour(LocalDateTime dateRetour) {
        this.dateRetour = dateRetour;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
}