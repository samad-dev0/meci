package meci_e_services.dto;

import meci_e_services.entity.enums.StatutRapport;
import meci_e_services.entity.enums.TypeRapport;

import java.time.LocalDateTime;

public class RapportDTO {

    private Long id;
    private String titre;
    private TypeRapport typeRapport;
    private String auteurNom;
    private String auteurEmail;
    private String contenu;
    private StatutRapport statut;
    private String verificateursAssignes;
    private String nomFichier;
    private String cheminFichier;
    private Long fileSize;
    private String commentaires;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    public RapportDTO() {
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
