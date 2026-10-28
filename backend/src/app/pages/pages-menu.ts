import { NbMenuItem } from '@nebular/theme';

export const MENU_ITEMS: NbMenuItem[] = [
  {
    title: 'Dashboard',
    icon: 'home-outline',
    link: '/pages/dashboard',
    home: true,
    data: { permissionCode: 'pages.dashboard.main' },
    children: [
      {
        title: 'Dashboard IoT',
        link: '/pages/iot-dashboard',
        data: { permissionCode: 'pages.dashboard.iot' },
      },
    ],
  },
  {
    title: 'E-SERVICE',
    group: true,
    data: { permissionCode: 'pages.eservice' },
  },
  {
    title: 'Administration',
    icon: 'people-outline',
    data: { permissionCode: 'pages.administration' },
    children: [
      {
        title: 'Utilisateurs',
        link: '/pages/administration/users',
        data: { permissionCode: 'pages.administration.users' },
      },
      {
        title: 'Autorisation',
        link: '/pages/administration/autorise-rapport',
        data: { permissionCode: 'pages.administration.report-access' },
      },
    ],
  },
  {
    title: 'Archivage',
    icon: 'layout-outline',
    expanded: true,
    data: { permissionCode: 'pages.archiving' },
    children: [
      {
        title: 'Initialisation',
        link: '/pages/layout/initialisation',
        data: { permissionCode: 'pages.archiving.initialization' },
      },
      {
        title: 'Dashboard archivage',
        link: '/pages/layout/archivage',
        pathMatch: 'full',
        data: { permissionCode: 'pages.archiving.dashboard' },
      },
      {
        title: 'Traitement',
        link: '/pages/layout/list',
        data: { permissionCode: 'pages.archiving.processing' },
      },
      {
        title: 'Suivi',
        link: '/pages/layout/infinite-list',
        data: { permissionCode: 'pages.archiving.tracking' },
      },
      {
        title: 'Accordion',
        link: '/pages/layout/accordion',
        data: { permissionCode: 'pages.archiving.accordion' },
      },
      {
        title: 'Tabs',
        pathMatch: 'prefix',
        link: '/pages/layout/tabs',
        data: { permissionCode: 'pages.archiving.tabs' },
      },
    ],
  },
  {
    title: 'Gestion rapport',
    icon: 'file-text-outline',
    data: { permissionCode: 'pages.reports' },
    children: [
      {
        title: 'Liste des rapports',
        link: '/pages/gestion-rapport/liste',
        data: { permissionCode: 'pages.reports.list' },
      },
      {
        title: 'Ajout rapport',
        link: '/pages/gestion-rapport/ajout',
        data: { permissionCode: 'pages.reports.create' },
      },
      {
        title: 'Rapports partagés',
        link: '/pages/gestion-rapport/partages',
        data: { permissionCode: 'pages.reports.shared' },
      },
    ],
  },
  {
    title: 'Forms',
    icon: 'edit-2-outline',
    data: { permissionCode: 'pages.forms' },
    children: [
      {
        title: 'Form Inputs',
        link: '/pages/forms/inputs',
        data: { permissionCode: 'pages.forms.inputs' },
      },
      {
        title: 'Form Layouts',
        link: '/pages/forms/layouts',
        data: { permissionCode: 'pages.forms.layouts' },
      },
      {
        title: 'Buttons',
        link: '/pages/forms/buttons',
        data: { permissionCode: 'pages.forms.buttons' },
      },
      {
        title: 'Datepicker',
        link: '/pages/forms/datepicker',
        data: { permissionCode: 'pages.forms.datepicker' },
      },
    ],
  },
  {
    title: 'UI Features',
    icon: 'keypad-outline',
    link: '/pages/ui-features',
    data: { permissionCode: 'pages.ui-features.home' },
    children: [
      {
        title: 'Grid',
        link: '/pages/ui-features/grid',
        data: { permissionCode: 'pages.ui-features.grid' },
      },
      {
        title: 'Icons',
        link: '/pages/ui-features/icons',
        data: { permissionCode: 'pages.ui-features.icons' },
      },
      {
        title: 'Typography',
        link: '/pages/ui-features/typography',
        data: { permissionCode: 'pages.ui-features.typography' },
      },
      {
        title: 'Animated Searches',
        link: '/pages/ui-features/search-fields',
        data: { permissionCode: 'pages.ui-features.search-fields' },
      },
    ],
  },
  {
    title: 'Modal & Overlays',
    icon: 'browser-outline',
    data: { permissionCode: 'pages.modal-overlays' },
    children: [
      {
        title: 'Dialog',
        link: '/pages/modal-overlays/dialog',
        data: { permissionCode: 'pages.modal-overlays.dialog' },
      },
      {
        title: 'Window',
        link: '/pages/modal-overlays/window',
        data: { permissionCode: 'pages.modal-overlays.window' },
      },
      {
        title: 'Popover',
        link: '/pages/modal-overlays/popover',
        data: { permissionCode: 'pages.modal-overlays.popover' },
      },
      {
        title: 'Toastr',
        link: '/pages/modal-overlays/toastr',
        data: { permissionCode: 'pages.modal-overlays.toastr' },
      },
      {
        title: 'Tooltip',
        link: '/pages/modal-overlays/tooltip',
        data: { permissionCode: 'pages.modal-overlays.tooltip' },
      },
    ],
  },
  {
    title: 'Extra Components',
    icon: 'message-circle-outline',
    data: { permissionCode: 'pages.extra-components' },
    children: [
      {
        title: 'Calendar',
        link: '/pages/extra-components/calendar',
        data: { permissionCode: 'pages.extra-components.calendar' },
      },
      {
        title: 'Progress Bar',
        link: '/pages/extra-components/progress-bar',
        data: { permissionCode: 'pages.extra-components.progress-bar' },
      },
      {
        title: 'Spinner',
        link: '/pages/extra-components/spinner',
        data: { permissionCode: 'pages.extra-components.spinner' },
      },
      {
        title: 'Alert',
        link: '/pages/extra-components/alert',
        data: { permissionCode: 'pages.extra-components.alert' },
      },
      {
        title: 'Calendar Kit',
        link: '/pages/extra-components/calendar-kit',
        data: { permissionCode: 'pages.extra-components.calendar-kit' },
      },
      {
        title: 'Chat',
        link: '/pages/extra-components/chat',
        data: { permissionCode: 'pages.extra-components.chat' },
      },
    ],
  },
  {
    title: 'Maps',
    icon: 'map-outline',
    data: { permissionCode: 'pages.maps' },
    children: [
      {
        title: 'Google Maps',
        link: '/pages/maps/gmaps',
        data: { permissionCode: 'pages.maps.gmaps' },
      },
      {
        title: 'Leaflet Maps',
        link: '/pages/maps/leaflet',
        data: { permissionCode: 'pages.maps.leaflet' },
      },
      {
        title: 'Bubble Maps',
        link: '/pages/maps/bubble',
        data: { permissionCode: 'pages.maps.bubble' },
      },
      {
        title: 'Search Maps',
        link: '/pages/maps/searchmap',
        data: { permissionCode: 'pages.maps.search' },
      },
    ],
  },
  {
    title: 'Charts',
    icon: 'pie-chart-outline',
    data: { permissionCode: 'pages.charts' },
    children: [
      {
        title: 'Echarts',
        link: '/pages/charts/echarts',
        data: { permissionCode: 'pages.charts.echarts' },
      },
      {
        title: 'Charts.js',
        link: '/pages/charts/chartjs',
        data: { permissionCode: 'pages.charts.chartjs' },
      },
      {
        title: 'D3',
        link: '/pages/charts/d3',
        data: { permissionCode: 'pages.charts.d3' },
      },
    ],
  },
  {
    title: 'Editors',
    icon: 'text-outline',
    data: { permissionCode: 'pages.editors' },
    children: [
      {
        title: 'TinyMCE',
        link: '/pages/editors/tinymce',
        data: { permissionCode: 'pages.editors.tinymce' },
      },
      {
        title: 'CKEditor',
        link: '/pages/editors/ckeditor',
        data: { permissionCode: 'pages.editors.ckeditor' },
      },
    ],
  },
  {
    title: 'Tables & Data',
    icon: 'grid-outline',
    data: { permissionCode: 'pages.tables' },
    children: [
      {
        title: 'Smart Table',
        link: '/pages/tables/smart-table',
        data: { permissionCode: 'pages.tables.smart-table' },
      },
      {
        title: 'Tree Grid',
        link: '/pages/tables/tree-grid',
        data: { permissionCode: 'pages.tables.tree-grid' },
      },
    ],
  },
  {
    title: 'Miscellaneous',
    icon: 'shuffle-2-outline',
    data: { permissionCode: 'pages.miscellaneous' },
    children: [
      {
        title: '404',
        link: '/pages/miscellaneous/404',
        data: { permissionCode: 'pages.miscellaneous.not-found' },
      },
    ],
  },
  {
    title: 'Authentification',
    icon: 'lock-outline',
    data: { permissionCode: 'pages.authentication' },
    children: [
      {
        title: 'Connexion',
        link: '/auth/login',
        data: { permissionCode: 'pages.authentication.login' },
      },
      {
        title: 'Inscription',
        link: '/auth/register',
        data: { permissionCode: 'pages.authentication.register' },
      },
      {
        title: 'Rénisialier le mot de pass',
        link: '/auth/reset-password',
        data: { permissionCode: 'pages.authentication.reset-password' },
      },
    ],
  },
];
