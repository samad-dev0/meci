package meci_e_services.dto;

public class AutorisationDTO {

    private Long verificateurId;
    private String verificateurNom;
    private Long cibleId;
    private String cibleNom;
    private Boolean autorise;

    public AutorisationDTO() {
    }

    public AutorisationDTO(Long verificateurId, String verificateurNom, Long cibleId, String cibleNom, Boolean autorise) {
        this.verificateurId = verificateurId;
        this.verificateurNom = verificateurNom;
        this.cibleId = cibleId;
        this.cibleNom = cibleNom;
        this.autorise = autorise;
    }

    public Long getVerificateurId() {
        return verificateurId;
    }

    public void setVerificateurId(Long verificateurId) {
        this.verificateurId = verificateurId;
    }

    public String getVerificateurNom() {
        return verificateurNom;
    }

    public void setVerificateurNom(String verificateurNom) {
        this.verificateurNom = verificateurNom;
    }

    public Long getCibleId() {
        return cibleId;
    }

    public void setCibleId(Long cibleId) {
        this.cibleId = cibleId;
    }

    public String getCibleNom() {
        return cibleNom;
    }

    public void setCibleNom(String cibleNom) {
        this.cibleNom = cibleNom;
    }

    public Boolean getAutorise() {
        return autorise;
    }

    public void setAutorise(Boolean autorise) {
        this.autorise = autorise;
    }
}
