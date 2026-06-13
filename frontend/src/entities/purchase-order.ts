export interface PurchaseOrderStatusIfs {
  id: number;
  code: string;
  name: string;
  sortOrder: string | null;
}

export interface PurchaseOrderStatusRefIfs {
  id: number;
  code: string;
  name: string;
}

export interface VendorOptionIfs {
  id: number;
  name: string;
}

export interface ProductCostOptionIfs {
  id: number;
  name: string;
  standardUnitCost: number;
}

export interface PurchaseOrderDetailIfs {
  id: number;
  product: { id: number; name: string };
  unitPrice: number;
  quantity: number;
  subtotal: number;
}

export interface PurchaseOrderListItemIfs {
  id: number;
  submittedDate: string;
  vendorName: string | null;
  status: PurchaseOrderStatusRefIfs;
  totalAmount: number;
}

export interface PurchaseOrderIfs {
  id: number;
  submittedDate: string;
  approvedDate: string | null;
  receivedDate: string | null;
  paymentDate: string | null;
  shippingFee: number | null;
  taxAmount: number | null;
  paymentAmount: number | null;
  paymentMethod: string | null;
  note: string | null;
  vendor: { id: number; name: string } | null;
  submittedBy: { id: number; firstName: string; lastName: string } | null;
  approvedBy: { id: number; firstName: string; lastName: string } | null;
  status: PurchaseOrderStatusRefIfs;
  purchaseOrderDetails: PurchaseOrderDetailIfs[];
  totalAmount: number;
}
