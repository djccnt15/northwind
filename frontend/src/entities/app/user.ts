export interface UserIfs {
  id: number;
  username: string;
  email: string;
  authorities: string[];
  enabled: boolean;
  liveUntil: string;
  passwordChangedAt: string;
  loginFailedCount: number;
  lastLoginAt: string;
  team: string;
  loggedIn: boolean;
}
