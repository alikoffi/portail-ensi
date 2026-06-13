export interface RubriqueBilan {
  id?: number;
  rubrique: string;
  montant: number;
}

export interface Bilan {
  actifs: RubriqueBilan[];
  passifs: RubriqueBilan[];
  totalActif: number;
  totalPassif: number;
  equilibre: boolean;
}

export type SectionBilan = 'actif' | 'passif';
