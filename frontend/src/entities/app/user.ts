export interface SessionIfs {
  id: number;
  username: string;
  authorities: string[];
  loggedIn: boolean;
}
