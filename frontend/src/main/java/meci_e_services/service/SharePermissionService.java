package meci_e_services.service;

import meci_e_services.entity.AppUsers;
import meci_e_services.repository.AppUsersRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class SharePermissionService {

    private final AppUsersRepository users;
    private final AccessControlService accessControlService;
    private final String initialSuperAdminEmail;
    private final PasswordEncoder passwordEncoder;

    public SharePermissionService(
            AppUsersRepository users,
            AccessControlService accessControlService,
            PasswordEncoder passwordEncoder,
            @Value("${app.security.initial-super-admin-email:admin@meci.com}")
            String initialSuperAdminEmail) {
        this.users = users;
        this.accessControlService = accessControlService;
        this.passwordEncoder = passwordEncoder;
        this.initialSuperAdminEmail = initialSuperAdminEmail;
    }

    public boolean canManageAnyOwner(String email) {
        AppUsers user = findActiveUser(email);
        return user != null && (Boolean.TRUE.equals(user.getSuperAdmin())
                || Boolean.TRUE.equals(user.getGestionnairePartage())
                || accessControlService.hasAccess(email, "pages.administration.report-access.manage"));
    }

    public boolean isSuperAdmin(String email) {
        AppUsers user = findActiveUser(email);
        return user != null && Boolean.TRUE.equals(user.getSuperAdmin());
    }

    public boolean canShareOwnReports(String email) {
        AppUsers user = findActiveUser(email);
        return user != null && (user.getSuperAdmin()
                || user.getGestionnairePartage()
                || user.getPeutPartagerRapports()
                || accessControlService.hasAccess(email, "pages.reports.shared.request")
                || accessControlService.hasAccess(email, "pages.reports.shared.manage"));
    }

    public void requireCanShareOwnReports(String email) {
        if (!canShareOwnReports(email)) {
            throw new AccessDeniedException("Le partage de vos rapports n’est pas autorisé.");
        }
    }

    public void requireShareManager(String email) {
        if (!canManageAnyOwner(email)) {
            throw new AccessDeniedException("Vous n’avez pas le droit de gérer les partages globaux.");
        }
    }

    public void requireSuperAdmin(String email) {
        if (!isSuperAdmin(email)) {
            throw new AccessDeniedException("Seul le Super Admin peut effectuer cette opération.");
        }
    }

    public List<Long> getShareManagerIds(String email) {
        requireSuperAdmin(email);
        return users.findAll().stream()
                .filter(user -> Boolean.TRUE.equals(user.getGestionnairePartage())
                        || accessControlService.hasDirectAccess(
                                user,
                                "pages.administration.report-access.manage"
                        ))
                .map(AppUsers::getId)
                .collect(Collectors.toList());
    }

    public List<Long> getReportSharerIds(String email) {
        requireSuperAdmin(email);
        return users.findAll().stream()
                .filter(user -> Boolean.TRUE.equals(user.getPeutPartagerRapports())
                        || accessControlService.hasDirectAccess(
                                user,
                                "pages.reports.shared.manage"
                        ))
                .map(AppUsers::getId)
                .collect(Collectors.toList());
    }

    public void replaceReportSharers(String email, List<Long> userIds) {
        requireSuperAdmin(email);
        Set<Long> selectedIds = userIds == null
                ? Set.of()
                : userIds.stream().filter(Objects::nonNull).collect(Collectors.toSet());
        List<AppUsers> allUsers = users.findAll();
        Set<Long> knownIds = allUsers.stream().map(AppUsers::getId).collect(Collectors.toSet());
        if (!knownIds.containsAll(selectedIds)) {
            throw new IllegalArgumentException("Un ou plusieurs utilisateurs sélectionnés sont introuvables.");
        }
        if (allUsers.stream().anyMatch(user ->
                selectedIds.contains(user.getId()) && !Boolean.TRUE.equals(user.getActif()))) {
            throw new IllegalArgumentException("Seuls les utilisateurs actifs peuvent partager leurs rapports.");
        }
        for (AppUsers user : allUsers) {
            boolean granted = selectedIds.contains(user.getId());
            user.setPeutPartagerRapports(granted);
            accessControlService.setAccessRight(user, "pages.reports.shared", granted);
            accessControlService.setAccessRight(user, "pages.reports.shared.manage", granted);
        }
        users.saveAll(allUsers);
    }

    public void replaceShareManagers(String email, List<Long> userIds) {
        requireSuperAdmin(email);
        Set<Long> selectedIds = userIds == null
                ? Set.of()
                : userIds.stream().filter(Objects::nonNull).collect(Collectors.toSet());
        List<AppUsers> allUsers = users.findAll();
        Set<Long> knownIds = allUsers.stream().map(AppUsers::getId).collect(Collectors.toSet());
        if (!knownIds.containsAll(selectedIds)) {
            throw new IllegalArgumentException("Un ou plusieurs utilisateurs sélectionnés sont introuvables.");
        }
        if (allUsers.stream().anyMatch(user ->
                selectedIds.contains(user.getId())
                        && (!Boolean.TRUE.equals(user.getActif())
                        || Boolean.TRUE.equals(user.getSuperAdmin())))) {
            throw new IllegalArgumentException(
                    "Seuls les utilisateurs actifs non-Super Admin peuvent devenir gestionnaires."
            );
        }
        for (AppUsers user : allUsers) {
            boolean granted = !Boolean.TRUE.equals(user.getSuperAdmin())
                    && selectedIds.contains(user.getId());
            user.setGestionnairePartage(granted);
            accessControlService.setAccessRight(
                    user,
                    "pages.administration.report-access",
                    granted
            );
            accessControlService.setAccessRight(
                    user,
                    "pages.administration.report-access.manage",
                    granted
            );
        }
        users.saveAll(allUsers);
    }

    public void transferSuperAdmin(String currentEmail, Long newSuperAdminId) {
        requireSuperAdmin(currentEmail);
        if (newSuperAdminId == null) {
            throw new IllegalArgumentException("Le nouvel administrateur est obligatoire.");
        }
        AppUsers currentAdmin = users.findByEmailIgnoreCase(currentEmail)
                .orElseThrow(() -> new AccessDeniedException("Compte Super Admin introuvable."));
        AppUsers newAdmin = users.findById(newSuperAdminId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable."));
        if (!Boolean.TRUE.equals(newAdmin.getActif())) {
            throw new IllegalArgumentException("Le nouveau Super Admin doit avoir un compte actif.");
        }
        if (Objects.equals(currentAdmin.getId(), newAdmin.getId())) {
            return;
        }
        currentAdmin.setSuperAdmin(false);
        newAdmin.setSuperAdmin(true);
        newAdmin.setGestionnairePartage(false);
        users.saveAll(List.of(currentAdmin, newAdmin));
    }

    public void initializeSuperAdmin() {
        if (users.findFirstBySuperAdminTrue().isPresent()) {
            return;
        }

        users.findByEmailIgnoreCase(initialSuperAdminEmail).ifPresentOrElse(user -> {
            user.setSuperAdmin(true);
            user.setGestionnairePartage(false);
            users.save(user);
        }, () -> {
            AppUsers defaultAdmin = new AppUsers();
            defaultAdmin.setNom("Super");
            defaultAdmin.setPrenom("Admin");
            defaultAdmin.setEmail(initialSuperAdminEmail);
            defaultAdmin.setPassword(passwordEncoder.encode("MECI@2026"));
            defaultAdmin.setActif(true);
            defaultAdmin.setSuperAdmin(true);
            defaultAdmin.setGestionnairePartage(false);
            defaultAdmin.setPeutPartagerRapports(false);
            users.save(defaultAdmin);
        });
    }

    private AppUsers findActiveUser(String email) {
        if (email == null) {
            return null;
        }
        return users.findByEmailIgnoreCase(email)
                .filter(user -> Boolean.TRUE.equals(user.getActif()))
                .orElse(null);
    }
}
