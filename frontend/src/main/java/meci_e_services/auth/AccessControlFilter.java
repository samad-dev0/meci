package meci_e_services.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import meci_e_services.service.AccessControlService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class AccessControlFilter extends OncePerRequestFilter {

    private final AccessControlService accessControlService;

    public AccessControlFilter(AccessControlService accessControlService) {
        this.accessControlService = accessControlService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !path.startsWith("/api/")
                || path.startsWith("/api/auth/")
                || path.startsWith("/api/admin/")
                || path.endsWith("/access-rights")
                || path.startsWith("/api/access-control/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!hasRequiredAccess(authentication.getName(), request.getMethod(), request.getRequestURI())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Droit d’accès insuffisant.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean hasRequiredAccess(String email, String method, String path) {
        if ("GET".equals(method) && path.equals("/api/utilisateurs")) {
            return accessControlService.hasAccess(email, "pages.administration.users")
                    || accessControlService.hasAccess(email, "pages.archiving.initialization");
        }

        String requiredCode = requiredRight(method, path);
        if (requiredCode == null) {
            return false;
        }
        if (accessControlService.hasAccess(email, requiredCode)) {
            return true;
        }
        return "GET".equals(method) && path.endsWith("/telecharger")
                && accessControlService.hasAccess(email, "pages.reports.shared.download");
    }

    private String requiredRight(String method, String path) {
        if (path.equals("/api/rapports")) {
            return switch (method) {
                case "GET" -> "pages.reports.list";
                case "POST" -> "pages.reports.create.submit";
                default -> null;
            };
        }
        if (path.startsWith("/api/rapports/")) {
            String suffix = path.substring("/api/rapports/".length());
            if (suffix.equals("mes-rapports")) {
                return "pages.reports.list";
            }
            if (suffix.equals("partages")) {
                return "pages.reports.shared";
            }
            if (suffix.equals("fichier")) {
                return "POST".equals(method) ? "pages.reports.create.submit" : null;
            }
            if (suffix.endsWith("/telecharger")) {
                return "GET".equals(method) ? "pages.reports.list.download" : null;
            }
            if (suffix.endsWith("/statut")) {
                return "PATCH".equals(method) ? "pages.reports.list.validate" : null;
            }
            if ("GET".equals(method)) {
                return "pages.reports.list";
            }
            if ("PUT".equals(method)) {
                return "pages.reports.list.edit";
            }
            if ("DELETE".equals(method)) {
                return "pages.reports.list.delete";
            }
            return null;
        }
        if (path.startsWith("/api/autorisations")) {
            return authorizationRight(method, path);
        }
        if (path.equals("/api/utilisateurs") || path.matches("/api/utilisateurs/\\d+")) {
            return switch (method) {
                case "GET" -> "pages.administration.users";
                case "POST" -> "pages.administration.users.create";
                case "PUT" -> "pages.administration.users.edit";
                case "DELETE" -> "pages.administration.users.delete";
                default -> null;
            };
        }
        if (path.startsWith("/api/share-requests")) {
            if (path.startsWith("/api/admin/")) {
                return null;
            }
            if ("POST".equals(method) && path.equals("/api/share-requests")) {
                return "pages.reports.shared.request";
            }
            return "pages.reports.shared";
        }

        return null;
    }

    private String authorizationRight(String method, String path) {
        if (path.endsWith("/gestionnaires") || path.endsWith("/partageurs")
                || path.endsWith("/super-admin")) {
            return "pages.administration.report-access.manage";
        }
        if (path.endsWith("/mes-partages")) {
            return "POST".equals(method)
                    ? "pages.reports.shared.manage"
                    : "pages.reports.shared";
        }
        if (path.contains("/partages")) {
            return "GET".equals(method)
                    ? "pages.administration.report-access.manage"
                    : "pages.administration.report-access.manage";
        }
        if (path.endsWith("/peut-partager")
                ) {
            return "pages.reports.shared";
        }
        if (path.endsWith("/beneficiaires")
                || path.endsWith("/delegation")
                || path.endsWith("/delegations")
                || path.contains("/proprietaires/")) {
            return "pages.administration.report-access.manage";
        }
        return null;
    }
}
