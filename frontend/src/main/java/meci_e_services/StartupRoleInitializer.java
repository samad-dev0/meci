package meci_e_services;

import meci_e_services.service.SharePermissionService;
import meci_e_services.service.AccessControlService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupRoleInitializer implements CommandLineRunner {

    private final SharePermissionService sharePermissionService;
    private final AccessControlService accessControlService;

    public StartupRoleInitializer(
            SharePermissionService sharePermissionService,
            AccessControlService accessControlService
    ) {
        this.sharePermissionService = sharePermissionService;
        this.accessControlService = accessControlService;
    }

    @Override
    public void run(String... args) {
        sharePermissionService.initializeSuperAdmin();
        accessControlService.initializeLegacyUsers();
    }
}
