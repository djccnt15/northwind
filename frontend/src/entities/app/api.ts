interface ResultIfs {
  code: number;
  message: string;
  description: string;
}

export interface ApiIfs {
  serverTime: number;
  result: ResultIfs;
  body: Record<string, unknown> | null;
}
