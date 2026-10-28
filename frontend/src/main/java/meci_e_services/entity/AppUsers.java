package meci_e_services.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "app_users")
public class AppUsers {

    //Relation
    @JsonIgnore
    @OneToMany(
            mappedBy = "utilisateur",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<UtilisateurPermission> permissions =
            new ArrayList<>();

    @JsonIgnore
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_access_rights",
            joinColumns = @JoinColumn(name = "user_id"),
            uniqueConstraints = @UniqueConstraint(
                    name = "uk_user_access_right",
                    columnNames = {"user_id", "right_code"}
            )
    )
    @Column(name = "right_code", nullable = false, length = 180)
    private Set<String> accessRightCodes = new HashSet<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false, unique = true)
    private String email;

    private String telephone;

    private String fonction;

    private String service;

    @Column(nullable = false)
    private Boolean actif = true;

    @Column
    private Boolean superAdmin = false;

    @Column
    private Boolean gestionnairePartage = false;

    @Column(name = "peut_partager_rapports")
    private Boolean peutPartagerRapports = false;

    @Column(name = "access_rights_configured")
    private Boolean accessRightsConfigured = false;

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String motDePasse;


    // CONSTRUCTEUR

    public List<UtilisateurPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(
            List<UtilisateurPermission> permissions
    ) {
        this.permissions = permissions;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Transient
    private Set<String> requestedAccessRights;

    public Set<String> getRequestedAccessRights() {
        return requestedAccessRights;
    }

    public void setRequestedAccessRights(Set<String> requestedAccessRights) {
        this.requestedAccessRights = requestedAccessRights;
    }

    public Set<String> getAccessRightCodes() {
        return accessRightCodes;
    }

    public void setAccessRightCodes(Set<String> accessRightCodes) {
        this.accessRightCodes = accessRightCodes == null
                ? new HashSet<>()
                : new HashSet<>(accessRightCodes);
    }

    public Boolean getAccessRightsConfigured() {
        return accessRightsConfigured;
    }

    public void setAccessRightsConfigured(Boolean accessRightsConfigured) {
        this.accessRightsConfigured = accessRightsConfigured;
    }

    public AppUsers() {
    }


    // GETTERS / SETTERS

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getFonction() {
        return fonction;
    }

    public void setFonction(String fonction) {
        this.fonction = fonction;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public boolean getSuperAdmin() {
        return Boolean.TRUE.equals(superAdmin);
    }

    public void setSuperAdmin(Boolean superAdmin) {
        this.superAdmin = superAdmin;
    }

    public boolean getGestionnairePartage() {
        return Boolean.TRUE.equals(gestionnairePartage);
    }

    public void setGestionnairePartage(Boolean gestionnairePartage) {
        this.gestionnairePartage = gestionnairePartage;
    }

    public boolean getPeutPartagerRapports() {
        return Boolean.TRUE.equals(peutPartagerRapports);
    }

    public void setPeutPartagerRapports(Boolean peutPartagerRapports) {
        this.peutPartagerRapports = peutPartagerRapports;
    }
}