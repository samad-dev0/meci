export type StatutRapport = 'EN_ATTENTE' | 'EN_COURS_VALIDATION' | 'VALIDE' | 'REJETE';

export type TypeRapport = 'RAPPORT_ACTIVITE' | 'RAPPORT_FINANCIER' | 'RAPPORT_TECHNIQUE' | 'RAPPORT_MISSION' | 'AUTRE';

export interface Rapport {
  id?: number;
  titre: string;
  typeRapport: TypeRapport;
  auteurNom: string;
  auteurEmail?: string;
  contenu?: string;
  statut?: StatutRapport;
  verificateursAssignes?: string;
  nomFichier?: string;
  cheminFichier?: string;
  fileSize?: number;
  commentaires?: string;
  dateCreation?: string;
  dateModification?: string;
}

export interface AutorisationDTO {
  verificateurId: number;
  verificateurNom: string;
  cibleId: number;
  cibleNom: string;
  autorise: boolean;
}

export interface AppUser {
  id?: number;
  nom: string;
  prenom: string;
  email: string;
  telephone?: string;
  fonction?: string;
  service?: string;
  actif?: boolean;
}
