package meci_e_services.controller;

import meci_e_services.dto.AccessRightNodeDTO;
import meci_e_services.dto.AccessRightsResponseDTO;
import meci_e_services.service.AccessRightCatalog;
import meci_e_services.service.AccessControlService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/access-control")
public class AccessControlController {

    private final AccessControlService accessControlService;

    public AccessControlController(AccessControlService accessControlService) {
        this.accessControlService = accessControlService;
    }

    @GetMapping("/catalog")
    public ResponseEntity<List<AccessRightNodeDTO>> getCatalog() {
        return ResponseEntity.ok(accessControlService.getCatalog());
    }

    @GetMapping("/me")
    public ResponseEntity<AccessRightsResponseDTO> getMyAccessRights(Principal principal) {
        return ResponseEntity.ok(new AccessRightsResponseDTO(
                accessControlService.getAccessRights(principal.getName()),
                accessControlService.isSuperAdmin(principal.getName())
        ));
    }
}
