package meci_e_services.entity;

import jakarta.persistence.*;
import meci_e_services.entity.enums.EtapeCourrier;
import meci_e_services.entity.enums.OrigineCourrier;
import meci_e_services.entity.enums.StatutCourrier;
import meci_e_services.entity.enums.TypeDocument;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "courriers",
        indexes = {
                @Index(name = "idx_courrier_reference", columnList = "reference"),
                @Index(name = "idx_courrier_statut", columnList = "statut"),
                @Index(name = "idx_courrier_etape", columnList = "etape"),
                @Index(name = "idx_courrier_date_reception", columnList = "date_reception")
        }
)
public class Courrier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String reference;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_document", nullable = false, length = 30)
    private TypeDocument typeDocument;

    @Column(name = "date_reception", nullable = false)
    private LocalDate dateReception;

    @Column(nullable = false, length = 255)
    private String expediteur;

    @Column(name = "email_expediteur", length = 255)
    private String emailExpediteur;

    @Column(name = "date_email")
    private LocalDateTime dateEmail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinataire_initial_id")
    private AppUsers destinataireInitial;

    @Column(nullable = false, length = 500)
    private String objet;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrigineCourrier origine;

    @Column(name = "nom_fichier", length = 255)
    private String nomFichier;

    @Column(name = "chemin_fichier", length = 500)
    private String cheminFichier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatutCourrier statut = StatutCourrier.EN_COURS;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EtapeCourrier etape = EtapeCourrier.SECRETARIAT;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @OneToMany(
            mappedBy = "courrier",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("dateAffectation ASC")
    private List<Affectation> affectations = new ArrayList<>();

    @OneToMany(
            mappedBy = "courrier",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("dateAction ASC")
    private List<HistoriqueCourrier> historiques = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        LocalDateTime maintenant = LocalDateTime.now();
        dateCreation = maintenant;
        dateModification = maintenant;
    }

    @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
    }

    public Courrier() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public TypeDocument getTypeDocument() {
        return typeDocument;
    }

    public void setTypeDocument(TypeDocument typeDocument) {
        this.typeDocument = typeDocument;
    }

    public LocalDate getDateReception() {
        return dateReception;
    }

    public void setDateReception(LocalDate dateReception) {
        this.dateReception = dateReception;
    }

    public String getExpediteur() {
        return expediteur;
    }

    public void setExpediteur(String expediteur) {
        this.expediteur = expediteur;
    }

    public String getEmailExpediteur() {
        return emailExpediteur;
    }

    public void setEmailExpediteur(String emailExpediteur) {
        this.emailExpediteur = emailExpediteur;
    }

    public LocalDateTime getDateEmail() {
        return dateEmail;
    }

    public void setDateEmail(LocalDateTime dateEmail) {
        this.dateEmail = dateEmail;
    }

    public AppUsers getDestinataireInitial() {
        return destinataireInitial;
    }

    public void setDestinataireInitial(AppUsers destinataireInitial) {
        this.destinataireInitial = destinataireInitial;
    }

    public String getObjet() {
        return objet;
    }

    public void setObjet(String objet) {
        this.objet = objet;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public OrigineCourrier getOrigine() {
        return origine;
    }

    public void setOrigine(OrigineCourrier origine) {
        this.origine = origine;
    }

    public String getNomFichier() {
        return nomFichier;
    }

    public void setNomFichier(String nomFichier) {
        this.nomFichier = nomFichier;
    }

    public String getCheminFichier() {
        return cheminFichier;
    }

    public void setCheminFichier(String cheminFichier) {
        this.cheminFichier = cheminFichier;
    }

    public StatutCourrier getStatut() {
        return statut;
    }

    public void setStatut(StatutCourrier statut) {
        this.statut = statut;
    }

    public EtapeCourrier getEtape() {
        return etape;
    }

    public void setEtape(EtapeCourrier etape) {
        this.etape = etape;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateModification() {
        return dateModification;
    }

    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }

    public List<Affectation> getAffectations() {
        return affectations;
    }

    public void setAffectations(List<Affectation> affectations) {
        this.affectations = affectations;
    }

    public List<HistoriqueCourrier> getHistoriques() {
        return historiques;
    }

    public void setHistoriques(List<HistoriqueCourrier> historiques) {
        this.historiques = historiques;
    }

    public void ajouterAffectation(Affectation affectation) {
        affectations.add(affectation);
        affectation.setCourrier(this);
    }

    public void ajouterHistorique(HistoriqueCourrier historique) {
        historiques.add(historique);
        historique.setCourrier(this);
    }
}