import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import styled from "styled-components";
import type { ApiIfs } from "../entities/app";
import type {
  CompanyTypeIfs,
  ProductCostOptionIfs,
  PurchaseOrderIfs,
  VendorOptionIfs,
} from "../entities";
import { privateApi } from "../shared/api";
import {
  commBorderRadius,
  commBtnHoverSkyBlue,
  commBtnHoverTomatoRed,
  commBtnSkyBlue,
  commBtnSkyBlueBoxShadow,
  commBtnTomatoRed,
  globalTransition,
  PageWrapper,
  Title,
} from "../shared/ui";
import { convertEmptyStringToNull } from "../shared/utils";

interface PurchaseItemRow {
  productId: number | "";
  productName: string;
  unitPrice: string;
  quantity: string;
}

const emptyItem: PurchaseItemRow = {
  productId: "",
  productName: "",
  unitPrice: "0",
  quantity: "1",
};

const isSupplierType = (label?: string) =>
  !!label &&
  (label.toLowerCase().includes("supplier") ||
    label.toLowerCase().includes("vendor"));

// Round HALF_UP to 2 decimals to match backend BigDecimal subtotal.
const roundHalfUp2 = (value: number) =>
  Math.round((value + Number.EPSILON) * 100) / 100;

const lineSubtotal = (unitPrice: number, quantity: number) =>
  roundHalfUp2(unitPrice * quantity);

export default function PurchaseOrderNew() {
  const navigate = useNavigate();
  const { t } = useTranslation();

  const [vendors, setVendors] = useState<VendorOptionIfs[]>([]);
  const [products, setProducts] = useState<ProductCostOptionIfs[]>([]);

  const [vendorId, setVendorId] = useState<number | "">("");
  const [shippingFee, setShippingFee] = useState("");
  const [taxAmount, setTaxAmount] = useState("");
  const [note, setNote] = useState("");
  const [items, setItems] = useState<PurchaseItemRow[]>([{ ...emptyItem }]);
  const [productKeyword, setProductKeyword] = useState("");
  const [loading, setLoading] = useState(false);

  // Load company types, find the supplier type, then fetch vendor options.
  useEffect(() => {
    privateApi
      .get("/v1/purchase-orders/company-types")
      .then((res) => {
        const data: ApiIfs<CompanyTypeIfs[]> = res.data;
        const types = data?.body ?? [];
        const supplierType = types.find((type) =>
          isSupplierType(type.companyType),
        );

        privateApi
          .get("/v1/purchase-orders/companies", {
            params: supplierType ? { type: supplierType.id } : {},
          })
          .then((vRes) => {
            const vData: ApiIfs<VendorOptionIfs[]> = vRes.data;
            setVendors(vData?.body ?? []);
          })
          .catch(console.error);
      })
      .catch(console.error);
  }, []);

  // Debounced product search
  useEffect(() => {
    const timer = setTimeout(() => {
      const params: Record<string, unknown> = {};
      if (productKeyword.trim()) params.keyword = productKeyword.trim();
      privateApi
        .get("/v1/purchase-orders/products", { params })
        .then((res) => {
          const data: ApiIfs<ProductCostOptionIfs[]> = res.data;
          setProducts(data?.body ?? []);
        })
        .catch(console.error);
    }, 400);
    return () => clearTimeout(timer);
  }, [productKeyword]);

  const addItem = () => setItems((prev) => [...prev, { ...emptyItem }]);

  const removeItem = (index: number) =>
    setItems((prev) => prev.filter((_, i) => i !== index));

  const updateItem = (index: number, patch: Partial<PurchaseItemRow>) =>
    setItems((prev) =>
      prev.map((item, i) => (i === index ? { ...item, ...patch } : item)),
    );

  // Selecting a product auto-fills the unit price with its standard unit cost.
  const onSelectProduct = (index: number, productId: number | "") => {
    if (productId === "") {
      updateItem(index, { productId: "", productName: "", unitPrice: "0" });
      return;
    }
    const product = products.find((p) => p.id === productId);
    updateItem(index, {
      productId,
      productName: product?.name ?? "",
      unitPrice:
        product?.standardUnitCost != null
          ? String(product.standardUnitCost)
          : "0",
    });
  };

  const shippingFeeNum = shippingFee === "" ? 0 : Number(shippingFee);
  const totalAmount = roundHalfUp2(
    items.reduce(
      (sum, item) =>
        sum +
        lineSubtotal(Number(item.unitPrice) || 0, Number(item.quantity) || 0),
      0,
    ) + (Number.isNaN(shippingFeeNum) ? 0 : shippingFeeNum),
  );

  const handleSubmit = () => {
    if (vendorId === "") {
      alert(t("page.purchaseOrderNew.alerts.pleaseSelectVendor"));
      return;
    }
    const validItems = items.filter((item) => item.productId !== "");
    if (validItems.length === 0) {
      alert(t("page.purchaseOrderNew.alerts.pleaseAddItem"));
      return;
    }
    for (const item of validItems) {
      if (!item.quantity || Number(item.quantity) < 1) {
        alert(t("page.purchaseOrderNew.alerts.minQty"));
        return;
      }
    }

    const body = {
      vendorId,
      shippingFee: shippingFee === "" ? null : Number(shippingFee),
      taxAmount: taxAmount === "" ? null : Number(taxAmount),
      note: convertEmptyStringToNull(note),
      purchaseOrderDetails: validItems.map((item) => ({
        productId: item.productId,
        quantity: Number(item.quantity),
        unitPrice: item.unitPrice === "" ? null : Number(item.unitPrice),
      })),
    };

    setLoading(true);
    privateApi
      .post("/v1/purchase-orders", body)
      .then((res) => {
        const data: ApiIfs<PurchaseOrderIfs> = res.data;
        alert(t("page.purchaseOrderNew.alerts.createSuccess"));
        if (data?.body) {
          navigate(`/purchase-orders/${data.body.id}`);
        } else {
          navigate("/purchase-orders");
        }
      })
      .catch((err) => {
        const data: ApiIfs<null> = err.response?.data;
        if (data?.result?.code === 1400) {
          const lines = Object.values(data?.body || {});
          alert(t("page.purchaseOrderNew.alerts.invalidInput", { message: lines.join("\n") }));
          return;
        }
        const message = data?.result?.description ?? "";
        alert(t("page.purchaseOrderNew.alerts.createFailed", { message }));
      })
      .finally(() => setLoading(false));
  };

  return (
    <Wrapper>
      <Title>{t("page.purchaseOrderNew.title")}</Title>
      <Content>
        <Header>
          <BackBtn onClick={() => navigate("/purchase-orders")}>
            {t("page.purchaseOrderNew.back")}
          </BackBtn>
        </Header>

        <Card>
          <CardTitle>{t("page.purchaseOrderNew.poInfo")}</CardTitle>
          <Grid>
            <FieldRow>
              <Label>{t("page.purchaseOrderNew.vendor")}</Label>
              <Select
                value={vendorId}
                onChange={(e) =>
                  setVendorId(
                    e.target.value === "" ? "" : Number(e.target.value),
                  )
                }
              >
                <option value="">{t("page.purchaseOrderNew.selectVendor")}</option>
                {vendors.map((v) => (
                  <option key={v.id} value={v.id}>
                    {v.name}
                  </option>
                ))}
              </Select>
            </FieldRow>
            <FieldRow>
              <Label>{t("page.purchaseOrderNew.shippingFee")}</Label>
              <Input
                type="number"
                value={shippingFee}
                onChange={(e) => setShippingFee(e.target.value)}
              />
            </FieldRow>
            <FieldRow>
              <Label>{t("page.purchaseOrderNew.taxAmount")}</Label>
              <Input
                type="number"
                value={taxAmount}
                onChange={(e) => setTaxAmount(e.target.value)}
              />
            </FieldRow>
          </Grid>
          <FieldRow>
            <Label>{t("page.purchaseOrderNew.notes")}</Label>
            <TextArea value={note} onChange={(e) => setNote(e.target.value)} />
          </FieldRow>
        </Card>

        <Card>
          <CardTitle>{t("page.purchaseOrderNew.poItems")}</CardTitle>
          <FieldRow>
            <Label>{t("page.purchaseOrderNew.productSearch")}</Label>
            <Input
              placeholder={t("page.purchaseOrderNew.productSearchPlaceholder")}
              value={productKeyword}
              onChange={(e) => setProductKeyword(e.target.value)}
            />
          </FieldRow>
          <ItemTable>
            <thead>
              <tr>
                <th>{t("page.purchaseOrderNew.col.product")}</th>
                <th>{t("page.purchaseOrderNew.col.unitCost")}</th>
                <th>{t("page.purchaseOrderNew.col.qty")}</th>
                <th>{t("page.purchaseOrderNew.col.subtotal")}</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {items.map((item, index) => (
                <tr key={index}>
                  <td>
                    <Select
                      value={item.productId}
                      onChange={(e) =>
                        onSelectProduct(
                          index,
                          e.target.value === ""
                            ? ""
                            : Number(e.target.value),
                        )
                      }
                    >
                      <option value="">{t("page.purchaseOrderNew.selectProduct")}</option>
                      {products.map((p) => (
                        <option key={p.id} value={p.id}>
                          {p.name}
                        </option>
                      ))}
                      {item.productId !== "" &&
                        !products.some((p) => p.id === item.productId) && (
                          <option value={item.productId}>
                            {item.productName}
                          </option>
                        )}
                    </Select>
                  </td>
                  <td>
                    <SmallInput
                      type="number"
                      min={0}
                      step="0.01"
                      value={item.unitPrice}
                      onChange={(e) =>
                        updateItem(index, { unitPrice: e.target.value })
                      }
                    />
                  </td>
                  <td>
                    <SmallInput
                      type="number"
                      min={1}
                      value={item.quantity}
                      onChange={(e) =>
                        updateItem(index, { quantity: e.target.value })
                      }
                    />
                  </td>
                  <td>
                    $
                    {lineSubtotal(
                      Number(item.unitPrice) || 0,
                      Number(item.quantity) || 0,
                    ).toFixed(2)}
                  </td>
                  <td>
                    <RemoveBtn
                      type="button"
                      onClick={() => removeItem(index)}
                      disabled={items.length === 1}
                    >
                      ✕
                    </RemoveBtn>
                  </td>
                </tr>
              ))}
            </tbody>
            <tfoot>
              <tr>
                <td colSpan={3} style={{ textAlign: "right" }}>
                  <strong>{t("page.purchaseOrderNew.total")}</strong>
                </td>
                <td>
                  <strong>${totalAmount.toFixed(2)}</strong>
                </td>
                <td />
              </tr>
            </tfoot>
          </ItemTable>
          <AddBtn type="button" onClick={addItem}>
            {t("page.purchaseOrderNew.addItem")}
          </AddBtn>
        </Card>

        <ActionBar>
          <PrimaryBtn type="button" onClick={handleSubmit} disabled={loading}>
            {t("page.purchaseOrderNew.createPO")}
          </PrimaryBtn>
          <SecondaryBtn
            type="button"
            onClick={() => navigate("/purchase-orders")}
            disabled={loading}
          >
            {t("page.purchaseOrderNew.cancel")}
          </SecondaryBtn>
        </ActionBar>
      </Content>
    </Wrapper>
  );
}

const Wrapper = styled(PageWrapper)``;

const Content = styled.div`
  padding: 0 20px 20px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 20px;
`;

const Header = styled.div`
  display: flex;
  align-items: center;
  gap: 14px;
  flex-wrap: wrap;
`;

const Card = styled.div`
  ${commBorderRadius}
  border: 1px solid #e0e0e0;
  padding: 16px 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
`;

const CardTitle = styled.h3`
  font-size: 16px;
  font-weight: 700;
  text-transform: uppercase;
  border-bottom: 1px solid #eee;
  padding-bottom: 8px;
`;

const Grid = styled.div`
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px 20px;

  @media (max-width: 900px) {
    grid-template-columns: 1fr;
  }
`;

const FieldRow = styled.div`
  display: flex;
  flex-direction: column;
  gap: 6px;
`;

const Label = styled.label`
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
  color: #666;
`;

const Input = styled.input`
  ${commBorderRadius}
  border: 1px solid #ccc;
  font-size: 14px;
  height: 38px;
  padding: 0 10px;
  width: 100%;

  &:focus {
    outline: none;
    ${commBtnSkyBlueBoxShadow}
  }
`;

const SmallInput = styled.input`
  ${commBorderRadius}
  border: 1px solid #ccc;
  font-size: 14px;
  height: 34px;
  padding: 0 8px;
  width: 90px;

  &:focus {
    outline: none;
    ${commBtnSkyBlueBoxShadow}
  }
`;

const TextArea = styled.textarea`
  ${commBorderRadius}
  border: 1px solid #ccc;
  font-size: 14px;
  padding: 8px 10px;
  width: 100%;
  min-height: 60px;
  resize: vertical;
  font-family: inherit;

  &:focus {
    outline: none;
    ${commBtnSkyBlueBoxShadow}
  }
`;

const Select = styled.select`
  ${commBorderRadius}
  border: 1px solid #ccc;
  font-size: 14px;
  height: 38px;
  padding: 0 8px;
  width: 100%;
  background-color: white;

  &:focus {
    outline: none;
    ${commBtnSkyBlueBoxShadow}
  }
`;

const ItemTable = styled.table`
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;

  th,
  td {
    text-align: left;
    padding: 8px 10px;
    border-bottom: 1px solid #eee;
    vertical-align: middle;
  }

  th {
    font-size: 12px;
    text-transform: uppercase;
    color: #666;
  }

  tfoot td {
    border-bottom: none;
  }
`;

const ActionBar = styled.div`
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
`;

const BtnBase = styled.button`
  ${commBorderRadius}
  ${globalTransition}
  height: 40px;
  min-width: 110px;
  border: none;
  color: white;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
`;

const PrimaryBtn = styled(BtnBase)`
  ${commBtnSkyBlue}
  &:hover {
    ${commBtnHoverSkyBlue}
  }
`;

const SecondaryBtn = styled(BtnBase)`
  background-color: #888;
  &:hover {
    background-color: #6f6f6f;
  }
`;

const AddBtn = styled.button`
  ${commBorderRadius}
  ${commBtnSkyBlue}
  ${globalTransition}
  align-self: flex-start;
  height: 36px;
  padding: 0 16px;
  border: none;
  color: white;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;

  &:hover {
    ${commBtnHoverSkyBlue}
  }
`;

const BackBtn = styled.button`
  ${commBorderRadius}
  ${globalTransition}
  background-color: #efefef;
  border: 1px solid #ccc;
  height: 36px;
  padding: 0 14px;
  font-size: 14px;
  cursor: pointer;

  &:hover {
    background-color: #d4d4d4;
  }
`;

const RemoveBtn = styled.button`
  ${commBorderRadius}
  ${commBtnTomatoRed}
  ${globalTransition}
  width: 32px;
  height: 32px;
  border: none;
  color: white;
  font-size: 14px;
  cursor: pointer;

  &:hover {
    ${commBtnHoverTomatoRed}
  }

  &:disabled {
    opacity: 0.4;
    cursor: not-allowed;
  }
`;
