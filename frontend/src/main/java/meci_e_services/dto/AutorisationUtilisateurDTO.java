package meci_e_services.dto;

import java.util.List;

public class AutorisationUtilisateurDTO {

    private Long beneficiaireId;
    private List<Long> rapportIds;

    public AutorisationUtilisateurDTO() {
    }

    public Long getBeneficiaireId() {
        return beneficiaireId;
    }

    public void setBeneficiaireId(Long beneficiaireId) {
        this.beneficiaireId = beneficiaireId;
    }

    public List<Long> getRapportIds() {
        return rapportIds;
    }

    public void setRapportIds(List<Long> rapportIds) {
        this.rapportIds = rapportIds;
    }
}
