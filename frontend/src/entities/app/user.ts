export interface UserIfs {
  id: number;
  username: string;
  email: string;
  authorities: string[];
  enabled: boolean;
  liveUntil: string;
  passwordChangedAt: string;
  loggedIn: boolean;
}
