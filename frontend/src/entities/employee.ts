export interface TitleIfs {
  id: number;
  title: string;
  isNew?: boolean;
}

export interface TeamIfs {
  id: number;
  name: string;
  isNew?: boolean;
}

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
}
