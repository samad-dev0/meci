package meci_e_services.entity;

import jakarta.persistence.*;
import meci_e_services.entity.enums.StatutRapport;
import meci_e_services.entity.enums.TypeRapport;

import java.time.LocalDateTime;

@Entity
@Table(name = "rapports")
public class Rapport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String titre;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_rapport", nullable = false, length = 50)
    private TypeRapport typeRapport = TypeRapport.RAPPORT_ACTIVITE;

    @Column(name = "auteur_nom", nullable = false, length = 150)
    private String auteurNom;

    @Column(name = "auteur_email", length = 255)
    private String auteurEmail;

    @Column(columnDefinition = "TEXT")
    private String contenu;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatutRapport statut = StatutRapport.EN_ATTENTE;

    @Column(name = "verificateurs_assignes", length = 500)
    private String verificateursAssignes;

    @Column(name = "nom_fichier", length = 255)
    private String nomFichier;

    @Column(name = "chemin_fichier", length = 500)
    private String cheminFichier;

    /*
     * Identifiant de la catégorie (optionnel - fonctionnalité à venir).
     * La colonne existe en BDD mais la feature n'est pas encore implémentée.
     */
    @Column(name = "categorie_id", nullable = true)
    private Long categorieId;

    /*
     * Taille du fichier en octets.
     * Cette colonne existe dans PostgreSQL et est NOT NULL.
     */
    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(columnDefinition = "TEXT")
    private String commentaires;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        if (this.dateCreation == null) {
            this.dateCreation = now;
        }

        if (this.dateModification == null) {
            this.dateModification = now;
        }

        if (this.statut == null) {
            this.statut = StatutRapport.EN_ATTENTE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.dateModification = LocalDateTime.now();
    }

    public Rapport() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public TypeRapport getTypeRapport() {
        return typeRapport;
    }

    public void setTypeRapport(TypeRapport typeRapport) {
        this.typeRapport = typeRapport;
    }

    public String getAuteurNom() {
        return auteurNom;
    }

    public void setAuteurNom(String auteurNom) {
        this.auteurNom = auteurNom;
    }

    public String getAuteurEmail() {
        return auteurEmail;
    }

    public void setAuteurEmail(String auteurEmail) {
        this.auteurEmail = auteurEmail;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public StatutRapport getStatut() {
        return statut;
    }

    public void setStatut(StatutRapport statut) {
        this.statut = statut;
    }

    public String getVerificateursAssignes() {
        return verificateursAssignes;
    }

    public void setVerificateursAssignes(String verificateursAssignes) {
        this.verificateursAssignes = verificateursAssignes;
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

    public Long getCategorieId() {
        return categorieId;
    }

    public void setCategorieId(Long categorieId) {
        this.categorieId = categorieId;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getCommentaires() {
        return commentaires;
    }

    public void setCommentaires(String commentaires) {
        this.commentaires = commentaires;
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
}