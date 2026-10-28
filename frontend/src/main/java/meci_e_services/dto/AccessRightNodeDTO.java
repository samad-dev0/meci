package meci_e_services.dto;

import java.util.List;

public record AccessRightNodeDTO(
        String code,
        String label,
        String type,
        String route,
        List<AccessRightNodeDTO> children
) {
}
