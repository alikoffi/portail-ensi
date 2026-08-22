export interface DocumentJoint {
  id: number;
  pvId?: number | null;
  evenementId?: number | null;
  libelle?: string | null;
  nomOriginal: string;
  contentType: string;
  taille: number;
  principal: boolean;
  deposePar?: string | null;
  deposeLe?: string | null;
}
