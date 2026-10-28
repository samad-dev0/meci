package meci_e_services.dto;

import java.util.Set;

public record AccessRightsResponseDTO(
        Set<String> codes,
        boolean superAdmin
) {
}
