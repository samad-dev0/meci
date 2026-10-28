package meci_e_services.entity;

import jakarta.persistence.*;
import meci_e_services.entity.enums.ActionHistorique;
import meci_e_services.entity.enums.EtapeCourrier;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "historique_courriers",
        indexes = {
                @Index(name = "idx_historique_courrier", columnList = "courrier_id"),
                @Index(name = "idx_historique_utilisateur", columnList = "utilisateur_id"),
                @Index(name = "idx_historique_date", columnList = "date_action")
        }
)
public class HistoriqueCourrier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "courrier_id", nullable = false)
    private Courrier courrier;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private AppUsers utilisateur;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private ActionHistorique action;

    @Enumerated(EnumType.STRING)
    @Column(name = "ancienne_etape", length = 30)
    private EtapeCourrier ancienneEtape;

    @Enumerated(EnumType.STRING)
    @Column(name = "nouvelle_etape", length = 30)
    private EtapeCourrier nouvelleEtape;

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    @Column(columnDefinition = "TEXT")
    private String motif;

    @Column(name = "date_action", nullable = false)
    private LocalDateTime dateAction;

    @Column(name = "ancienne_valeur_delai")
    private Integer ancienneValeurDelai;

    @Column(name = "nouvelle_valeur_delai")
    private Integer nouvelleValeurDelai;

    @Column(name = "ancienne_date_limite")
    private java.time.LocalDate ancienneDateLimite;

    @Column(name = "nouvelle_date_limite")
    private java.time.LocalDate nouvelleDateLimite;

    @PrePersist
    protected void onCreate() {

        if (dateAction == null) {
            dateAction = LocalDateTime.now();
        }
    }

    public HistoriqueCourrier() {
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

    public AppUsers getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(AppUsers utilisateur) {
        this.utilisateur = utilisateur;
    }

    public ActionHistorique getAction() {
        return action;
    }

    public void setAction(ActionHistorique action) {
        this.action = action;
    }

    public EtapeCourrier getAncienneEtape() {
        return ancienneEtape;
    }

    public void setAncienneEtape(EtapeCourrier ancienneEtape) {
        this.ancienneEtape = ancienneEtape;
    }

    public EtapeCourrier getNouvelleEtape() {
        return nouvelleEtape;
    }

    public void setNouvelleEtape(EtapeCourrier nouvelleEtape) {
        this.nouvelleEtape = nouvelleEtape;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public LocalDateTime getDateAction() {
        return dateAction;
    }

    public void setDateAction(LocalDateTime dateAction) {
        this.dateAction = dateAction;
    }

    public Integer getAncienneValeurDelai() {
        return ancienneValeurDelai;
    }

    public void setAncienneValeurDelai(Integer ancienneValeurDelai) {
        this.ancienneValeurDelai = ancienneValeurDelai;
    }

    public Integer getNouvelleValeurDelai() {
        return nouvelleValeurDelai;
    }

    public void setNouvelleValeurDelai(Integer nouvelleValeurDelai) {
        this.nouvelleValeurDelai = nouvelleValeurDelai;
    }

    public java.time.LocalDate getAncienneDateLimite() {
        return ancienneDateLimite;
    }

    public void setAncienneDateLimite(java.time.LocalDate ancienneDateLimite) {
        this.ancienneDateLimite = ancienneDateLimite;
    }

    public java.time.LocalDate getNouvelleDateLimite() {
        return nouvelleDateLimite;
    }

    public void setNouvelleDateLimite(java.time.LocalDate nouvelleDateLimite) {
        this.nouvelleDateLimite = nouvelleDateLimite;
    }
}