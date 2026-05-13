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
  employee: EmployeeIfs | null;
}

export interface EmployeeIfs {
  firstName: string;
  lastName: string;
  email: string;
  jobTitle: string;
  primaryPhone: string;
  secondaryPhone: string;
  notes: string;
  titleOfCourtesy: string;
  birthDate: string;
  hireDate: string;
  address: string;
  city: string;
  region: string;
  zipCode: string;
  country: string;
  photo: Uint8Array;
  title: string;
  supervisor: EmployeeIfs | null;
  subordinates: Set<EmployeeIfs> | null;
}
