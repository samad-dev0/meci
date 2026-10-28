package meci_e_services.service;

import meci_e_services.dto.AccessRightNodeDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class AccessRightCatalog {

    private static final List<AccessRightNodeDTO> TREE = buildTree();

    private AccessRightCatalog() {
    }

    public static List<AccessRightNodeDTO> getTree() {
        return TREE;
    }

    public static List<String> getAllCodes() {
        List<String> codes = new ArrayList<>();
        flatten(TREE).forEach(node -> codes.add(node.code()));
        return List.copyOf(codes);
    }

    public static boolean contains(String code) {
        return flatten(TREE).anyMatch(node -> node.code().equals(code));
    }

    public static List<String> descendantsIncluding(String code) {
        return find(TREE, code)
                .map(node -> flatten(List.of(node))
                        .map(AccessRightNodeDTO::code)
                        .toList())
                .orElse(List.of());
    }

    private static Stream<AccessRightNodeDTO> flatten(List<AccessRightNodeDTO> nodes) {
        return nodes.stream().flatMap(node ->
                Stream.concat(Stream.of(node), flatten(node.children())));
    }

    private static java.util.Optional<AccessRightNodeDTO> find(
            List<AccessRightNodeDTO> nodes,
            String code
    ) {
        return nodes.stream()
                .map(node -> node.code().equals(code)
                        ? java.util.Optional.of(node)
                        : find(node.children(), code))
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .findFirst();
    }

    private static List<AccessRightNodeDTO> buildTree() {
        return List.of(
                menu("pages.dashboard", "Dashboard", List.of(
                        page("pages.dashboard.main", "Tableau de bord", "/pages/dashboard"),
                        page("pages.dashboard.iot", "Dashboard IoT", "/pages/iot-dashboard")
                )),
                group("pages.eservice", "E-SERVICE", List.of(
                        menu("pages.administration", "Administration", List.of(
                                page("pages.administration.users", "Utilisateurs", "/pages/administration/users",
                                        action("pages.administration.users.create", "Créer"),
                                        action("pages.administration.users.edit", "Modifier"),
                                        action("pages.administration.users.delete", "Supprimer")),
                                page("pages.administration.report-access", "Autorisation", "/pages/administration/autorise-rapport",
                                        action("pages.administration.report-access.manage", "Gérer les autorisations"))
                        )),
                        menu("pages.archiving", "Archivage", List.of(
                                page("pages.archiving.initialization", "Initialisation", "/pages/layout/initialisation",
                                        action("pages.archiving.initialization.create", "Ajouter"),
                                        action("pages.archiving.initialization.submit", "Enregistrer")),
                                page("pages.archiving.dashboard", "Dashboard archivage", "/pages/layout/archivage"),
                                page("pages.archiving.processing", "Traitement", "/pages/layout/list",
                                        action("pages.archiving.processing.edit", "Modifier"),
                                        action("pages.archiving.processing.validate", "Valider")),
                                page("pages.archiving.tracking", "Suivi", "/pages/layout/infinite-list",
                                        action("pages.archiving.tracking.export", "Exporter")),
                                page("pages.archiving.accordion", "Accordion", "/pages/layout/accordion"),
                                page("pages.archiving.tabs", "Tabs", "/pages/layout/tabs")
                        )),
                        menu("pages.reports", "Gestion rapport", List.of(
                                page("pages.reports.list", "Liste des rapports", "/pages/gestion-rapport/liste",
                                        action("pages.reports.list.edit", "Modifier"),
                                        action("pages.reports.list.delete", "Supprimer"),
                                        action("pages.reports.list.download", "Télécharger"),
                                        action("pages.reports.list.validate", "Valider")),
                                page("pages.reports.create", "Ajout rapport", "/pages/gestion-rapport/ajout",
                                        action("pages.reports.create.submit", "Ajouter / soumettre")),
                                page("pages.reports.shared", "Rapports partagés", "/pages/gestion-rapport/partages",
                                        action("pages.reports.shared.request", "Demander le partage"),
                                        action("pages.reports.shared.manage", "Gérer les partages"),
                                        action("pages.reports.shared.download", "Télécharger"))
                        )),
                        menu("pages.forms", "Forms", List.of(
                                page("pages.forms.inputs", "Form Inputs", "/pages/forms/inputs"),
                                page("pages.forms.layouts", "Form Layouts", "/pages/forms/layouts"),
                                page("pages.forms.buttons", "Buttons", "/pages/forms/buttons"),
                                page("pages.forms.datepicker", "Datepicker", "/pages/forms/datepicker")
                        )),
                        menu("pages.ui-features", "UI Features", List.of(
                                page("pages.ui-features.home", "UI Features", "/pages/ui-features"),
                                page("pages.ui-features.grid", "Grid", "/pages/ui-features/grid"),
                                page("pages.ui-features.icons", "Icons", "/pages/ui-features/icons"),
                                page("pages.ui-features.typography", "Typography", "/pages/ui-features/typography"),
                                page("pages.ui-features.search-fields", "Animated Searches", "/pages/ui-features/search-fields")
                        )),
                        menu("pages.modal-overlays", "Modal & Overlays", List.of(
                                page("pages.modal-overlays.dialog", "Dialog", "/pages/modal-overlays/dialog"),
                                page("pages.modal-overlays.window", "Window", "/pages/modal-overlays/window"),
                                page("pages.modal-overlays.popover", "Popover", "/pages/modal-overlays/popover"),
                                page("pages.modal-overlays.toastr", "Toastr", "/pages/modal-overlays/toastr"),
                                page("pages.modal-overlays.tooltip", "Tooltip", "/pages/modal-overlays/tooltip")
                        )),
                        menu("pages.extra-components", "Extra Components", List.of(
                                page("pages.extra-components.calendar", "Calendar", "/pages/extra-components/calendar"),
                                page("pages.extra-components.progress-bar", "Progress Bar", "/pages/extra-components/progress-bar"),
                                page("pages.extra-components.spinner", "Spinner", "/pages/extra-components/spinner"),
                                page("pages.extra-components.alert", "Alert", "/pages/extra-components/alert"),
                                page("pages.extra-components.calendar-kit", "Calendar Kit", "/pages/extra-components/calendar-kit"),
                                page("pages.extra-components.chat", "Chat", "/pages/extra-components/chat")
                        )),
                        menu("pages.maps", "Maps", List.of(
                                page("pages.maps.gmaps", "Google Maps", "/pages/maps/gmaps"),
                                page("pages.maps.leaflet", "Leaflet Maps", "/pages/maps/leaflet"),
                                page("pages.maps.bubble", "Bubble Maps", "/pages/maps/bubble"),
                                page("pages.maps.search", "Search Maps", "/pages/maps/searchmap")
                        )),
                        menu("pages.charts", "Charts", List.of(
                                page("pages.charts.echarts", "Echarts", "/pages/charts/echarts"),
                                page("pages.charts.chartjs", "Charts.js", "/pages/charts/chartjs"),
                                page("pages.charts.d3", "D3", "/pages/charts/d3")
                        )),
                        menu("pages.editors", "Editors", List.of(
                                page("pages.editors.tinymce", "TinyMCE", "/pages/editors/tinymce"),
                                page("pages.editors.ckeditor", "CKEditor", "/pages/editors/ckeditor")
                        )),
                        menu("pages.tables", "Tables & Data", List.of(
                                page("pages.tables.smart-table", "Smart Table", "/pages/tables/smart-table"),
                                page("pages.tables.tree-grid", "Tree Grid", "/pages/tables/tree-grid")
                        )),
                        menu("pages.miscellaneous", "Miscellaneous", List.of(
                                page("pages.miscellaneous.not-found", "404", "/pages/miscellaneous/404")
                        )),
                        menu("pages.authentication", "Authentification", List.of(
                                page("pages.authentication.login", "Connexion", "/auth/login"),
                                page("pages.authentication.register", "Inscription", "/auth/register"),
                                page("pages.authentication.reset-password", "Réinitialiser le mot de passe", "/auth/reset-password")
                        ))
                ))
        );
    }

    private static AccessRightNodeDTO group(
            String code,
            String label,
            List<AccessRightNodeDTO> children
    ) {
        return node(code, label, "GROUP", null, children);
    }

    private static AccessRightNodeDTO menu(
            String code,
            String label,
            List<AccessRightNodeDTO> children
    ) {
        return node(code, label, "MENU", null, children);
    }

    private static AccessRightNodeDTO page(
            String code,
            String label,
            String route,
            AccessRightNodeDTO... actions
    ) {
        List<AccessRightNodeDTO> pageActions = new ArrayList<>();
        pageActions.add(action(code + ".view", "Consulter"));
        pageActions.addAll(List.of(actions));
        return node(code, label, "PAGE", route, List.copyOf(pageActions));
    }

    private static AccessRightNodeDTO action(String code, String label) {
        return node(code, label, "ACTION", null, List.of());
    }

    private static AccessRightNodeDTO node(
            String code,
            String label,
            String type,
            String route,
            List<AccessRightNodeDTO> children
    ) {
        return new AccessRightNodeDTO(code, label, type, route, children);
    }
}
