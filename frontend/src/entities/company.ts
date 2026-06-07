export interface CompanyTypeIfs {
  id: number;
  companyType: string;
}

export interface TaxStatusIfs {
  id: number;
  status: string;
}

export interface CompanyIfs {
  id: number;
  name: string;
  businessPhone: string | null;
  website: string | null;
  notes: string | null;
  address: string | null;
  city: string | null;
  region: string | null;
  zipCode: string | null;
  country: string | null;
  companyType: CompanyTypeIfs;
  taxStatus: TaxStatusIfs;
}

export interface ContactIfs {
  id: number;
  firstName: string;
  lastName: string;
  email: string | null;
  jobTitle: string | null;
  primaryPhone: string | null;
  secondaryPhone: string | null;
  notes: string | null;
  companyId: number;
}

export interface OrderSummaryIfs {
  id: number;
  orderDate: string;
  shippedDate: string | null;
  paidDate: string | null;
  shippingFee: number | null;
  taxRate: number | null;
  status: string | null;
}

export interface PurchaseOrderSummaryIfs {
  id: number;
  submittedDate: string;
  approvedDate: string | null;
  receivedDate: string | null;
  paymentAmount: number | null;
  status: string | null;
}
