interface ResultIfs {
  code: number;
  message: string;
  description: string;
}

export interface ListCountIfs<T = Record<string, unknown>> {
  totalCounts: number;
  list: T[];
}

export interface ApiIfs<TBody = Record<string, unknown>> {
  serverTime: number;
  result: ResultIfs;
  body: TBody | null;
}
