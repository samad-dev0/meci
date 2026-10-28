package meci_e_services.dto;

import java.util.List;

public class DelegationAutorisationRequestDTO {

    private List<Long> beneficiaireIds;
    private List<String> beneficiaireNoms;
    private Long proprietaireId;
    private String proprietaireNom;
    private Boolean tousRapports;
    private List<Long> rapportIds;

    public DelegationAutorisationRequestDTO() {
    }

    public List<Long> getBeneficiaireIds() {
        return beneficiaireIds;
    }

    public void setBeneficiaireIds(List<Long> beneficiaireIds) {
        this.beneficiaireIds = beneficiaireIds;
    }

    public List<String> getBeneficiaireNoms() {
        return beneficiaireNoms;
    }

    public void setBeneficiaireNoms(List<String> beneficiaireNoms) {
        this.beneficiaireNoms = beneficiaireNoms;
    }

    public Long getProprietaireId() {
        return proprietaireId;
    }

    public void setProprietaireId(Long proprietaireId) {
        this.proprietaireId = proprietaireId;
    }

    public String getProprietaireNom() {
        return proprietaireNom;
    }

    public void setProprietaireNom(String proprietaireNom) {
        this.proprietaireNom = proprietaireNom;
    }

    public Boolean getTousRapports() {
        return tousRapports;
    }

    public void setTousRapports(Boolean tousRapports) {
        this.tousRapports = tousRapports;
    }

    public List<Long> getRapportIds() {
        return rapportIds;
    }

    public void setRapportIds(List<Long> rapportIds) {
        this.rapportIds = rapportIds;
    }
}
