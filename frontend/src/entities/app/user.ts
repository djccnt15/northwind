export interface SessionIfs {
  id: number;
  username: string;
  authorities: string[];
  preferredLang: string;
  loggedIn: boolean;
}
