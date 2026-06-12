export interface OrderStatusIfs {
  id: number;
  code: string;
  name: string;
  sortOrder: string | null;
}

export interface OrderDetailStatusIfs {
  id: number;
  name: string;
  sortOrder: string | null;
}

export interface CompanyOptionIfs {
  id: number;
  name: string;
}

export interface ProductOptionIfs {
  id: number;
  name: string;
  unitPrice: number;
}

export interface OrderDetailIfs {
  id: number;
  product: { id: number; name: string };
  unitPrice: number;
  quantity: number;
  discount: number;
  subtotal: number;
  status: { id: number; name: string };
}

export interface OrderListItemIfs {
  id: number;
  orderDate: string;
  customerName: string;
  shipperName: string | null;
  status: { id: number; code: string; name: string };
  totalAmount: number;
}

export interface OrderIfs {
  id: number;
  orderDate: string;
  requiredDate: string | null;
  shippedDate: string | null;
  paidDate: string | null;
  shippingFee: number | null;
  taxRate: number | null;
  paymentType: string | null;
  notes: string | null;
  customer: { id: number; name: string };
  shipper: { id: number; name: string } | null;
  taxStatus: { id: number; status: string } | null;
  status: { id: number; code: string; name: string };
  orderDetails: OrderDetailIfs[];
  totalAmount: number;
}
