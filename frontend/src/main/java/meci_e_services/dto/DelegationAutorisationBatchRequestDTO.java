package meci_e_services.dto;

import java.util.List;

public class DelegationAutorisationBatchRequestDTO {

    private Long proprietaireId;
    private List<AutorisationUtilisateurDTO> autorisations;

    public DelegationAutorisationBatchRequestDTO() {
    }

    public Long getProprietaireId() {
        return proprietaireId;
    }

    public void setProprietaireId(Long proprietaireId) {
        this.proprietaireId = proprietaireId;
    }

    public List<AutorisationUtilisateurDTO> getAutorisations() {
        return autorisations;
    }

    public void setAutorisations(List<AutorisationUtilisateurDTO> autorisations) {
        this.autorisations = autorisations;
    }
}
