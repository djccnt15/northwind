interface ResultIfs {
  code: number;
  message: string;
  description: string;
}

interface Page {
  size: number;
  page: number;
  totalPages: number;
  totalElements: number;
}

export interface PageIfs<T = Record<string, unknown>> {
  page: Page;
  content: T[];
}

export interface ApiIfs<TBody = Record<string, unknown>> {
  serverTime: number;
  result: ResultIfs;
  body: TBody | null;
}
