package meci_e_services.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "utilisateur_permissions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_utilisateur_permission",
                        columnNames = {
                                "utilisateur_id",
                                "permission_id"
                        }
                )
        }
)
public class UtilisateurPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "utilisateur_id",
            nullable = false
    )
    private AppUsers utilisateur;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "permission_id",
            nullable = false
    )
    private Permission permission;

    @Column(nullable = false)
    private Boolean autorise = true;

    @Column(nullable = false)
    private LocalDateTime dateAttribution;

    public UtilisateurPermission() {
    }

    @PrePersist
    protected void onCreate() {
        if (dateAttribution == null) {
            dateAttribution = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AppUsers getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(AppUsers utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    public Boolean getAutorise() {
        return autorise;
    }

    public void setAutorise(Boolean autorise) {
        this.autorise = autorise;
    }

    public LocalDateTime getDateAttribution() {
        return dateAttribution;
    }

    public void setDateAttribution(
            LocalDateTime dateAttribution
    ) {
        this.dateAttribution = dateAttribution;
    }
}