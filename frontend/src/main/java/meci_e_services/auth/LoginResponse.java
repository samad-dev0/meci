package meci_e_services.auth;

public class LoginResponse {

    private String token;
    private Long userId;
    private String nom;
    private String prenom;
    private String email;
    private Boolean superAdmin;
    private Boolean gestionnairePartage;
    private Boolean peutPartagerRapports;

    public LoginResponse() {
    }

    public LoginResponse(
            String token,
            Long userId,
            String nom,
            String prenom,
            String email,
            Boolean superAdmin,
            Boolean gestionnairePartage,
            Boolean peutPartagerRapports
    ) {
        this.token = token;
        this.userId = userId;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.superAdmin = superAdmin;
        this.gestionnairePartage = gestionnairePartage;
        this.peutPartagerRapports = peutPartagerRapports;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public Boolean getSuperAdmin() {
        return superAdmin;
    }

    public void setSuperAdmin(Boolean superAdmin) {
        this.superAdmin = superAdmin;
    }

    public Boolean getGestionnairePartage() {
        return gestionnairePartage;
    }

    public void setGestionnairePartage(Boolean gestionnairePartage) {
        this.gestionnairePartage = gestionnairePartage;
    }

    public Boolean getPeutPartagerRapports() {
        return peutPartagerRapports;
    }

    public void setPeutPartagerRapports(Boolean peutPartagerRapports) {
        this.peutPartagerRapports = peutPartagerRapports;
    }
}