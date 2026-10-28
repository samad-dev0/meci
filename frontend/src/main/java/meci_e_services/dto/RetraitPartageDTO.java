package meci_e_services.dto;

public class RetraitPartageDTO {

    private Long proprietaireId;
    private Long beneficiaireId;
    private Long rapportId;

    public RetraitPartageDTO() {
    }

    public Long getProprietaireId() {
        return proprietaireId;
    }

    public void setProprietaireId(Long proprietaireId) {
        this.proprietaireId = proprietaireId;
    }

    public Long getBeneficiaireId() {
        return beneficiaireId;
    }

    public void setBeneficiaireId(Long beneficiaireId) {
        this.beneficiaireId = beneficiaireId;
    }

    public Long getRapportId() {
        return rapportId;
    }

    public void setRapportId(Long rapportId) {
        this.rapportId = rapportId;
    }
}
