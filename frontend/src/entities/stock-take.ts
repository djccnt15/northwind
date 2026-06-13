export interface StockTakeRowIfs {
  productId: number;
  productCode: string;
  productName: string;
  expectedQuantity: number;
  quantityOnHand: number | null;
}
