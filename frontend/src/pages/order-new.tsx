import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import styled from "styled-components";
import type { ApiIfs } from "../entities/app";
import type {
  CompanyOptionIfs,
  CompanyTypeIfs,
  OrderIfs,
  ProductOptionIfs,
  TaxStatusIfs,
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

interface OrderItemRow {
  productId: number | "";
  productName: string;
  unitPrice: number;
  quantity: string;
  discount: string;
}

const emptyItem: OrderItemRow = {
  productId: "",
  productName: "",
  unitPrice: 0,
  quantity: "1",
  discount: "0",
};

const isCustomerType = (label?: string) =>
  !!label && label.toLowerCase().includes("customer");

const isSupplierType = (label?: string) =>
  !!label &&
  (label.toLowerCase().includes("supplier") ||
    label.toLowerCase().includes("vendor"));

// Round HALF_UP to 2 decimals to match backend BigDecimal subtotal.
const roundHalfUp2 = (value: number) =>
  Math.round((value + Number.EPSILON) * 100) / 100;

const lineSubtotal = (unitPrice: number, quantity: number, discount: number) =>
  roundHalfUp2(unitPrice * quantity * (1 - discount / 100));

export default function OrderNew() {
  const navigate = useNavigate();
  const { t } = useTranslation();

  const [customers, setCustomers] = useState<CompanyOptionIfs[]>([]);
  const [shippers, setShippers] = useState<CompanyOptionIfs[]>([]);
  const [taxStatuses, setTaxStatuses] = useState<TaxStatusIfs[]>([]);
  const [products, setProducts] = useState<ProductOptionIfs[]>([]);

  const [customerId, setCustomerId] = useState<number | "">("");
  const [shipperId, setShipperId] = useState<number | "">("");
  const [taxStatusId, setTaxStatusId] = useState<number | "">("");
  const [requiredDate, setRequiredDate] = useState("");
  const [paymentType, setPaymentType] = useState("");
  const [shippingFee, setShippingFee] = useState("");
  const [notes, setNotes] = useState("");
  const [items, setItems] = useState<OrderItemRow[]>([{ ...emptyItem }]);
  const [productKeyword, setProductKeyword] = useState("");
  const [loading, setLoading] = useState(false);

  // Load company types then fetch customer/shipper options by matching label.
  useEffect(() => {
    privateApi
      .get("/v1/orders/company-types")
      .then((res) => {
        const data: ApiIfs<CompanyTypeIfs[]> = res.data;
        const types = data?.body ?? [];

        const customerType = types.find((type) =>
          isCustomerType(type.companyType),
        );
        const shipperType = types.find((type) =>
          isSupplierType(type.companyType),
        );

        privateApi
          .get("/v1/orders/companies", {
            params: customerType ? { type: customerType.id } : {},
          })
          .then((cRes) => {
            const cData: ApiIfs<CompanyOptionIfs[]> = cRes.data;
            setCustomers(cData?.body ?? []);
          })
          .catch(console.error);

        privateApi
          .get("/v1/orders/companies", {
            params: shipperType ? { type: shipperType.id } : {},
          })
          .then((sRes) => {
            const sData: ApiIfs<CompanyOptionIfs[]> = sRes.data;
            setShippers(sData?.body ?? []);
          })
          .catch(console.error);
      })
      .catch(console.error);

    privateApi
      .get("/v1/orders/tax-statuses")
      .then((res) => {
        const data: ApiIfs<TaxStatusIfs[]> = res.data;
        setTaxStatuses(data?.body ?? []);
      })
      .catch(console.error);
  }, []);

  // Debounced product search
  useEffect(() => {
    const timer = setTimeout(() => {
      const params: Record<string, unknown> = {};
      if (productKeyword.trim()) params.keyword = productKeyword.trim();
      privateApi
        .get("/v1/orders/products", { params })
        .then((res) => {
          const data: ApiIfs<ProductOptionIfs[]> = res.data;
          setProducts(data?.body ?? []);
        })
        .catch(console.error);
    }, 400);
    return () => clearTimeout(timer);
  }, [productKeyword]);

  const addItem = () => setItems((prev) => [...prev, { ...emptyItem }]);

  const removeItem = (index: number) =>
    setItems((prev) => prev.filter((_, i) => i !== index));

  const updateItem = (index: number, patch: Partial<OrderItemRow>) =>
    setItems((prev) =>
      prev.map((item, i) => (i === index ? { ...item, ...patch } : item)),
    );

  const onSelectProduct = (index: number, productId: number | "") => {
    if (productId === "") {
      updateItem(index, { productId: "", productName: "", unitPrice: 0 });
      return;
    }
    const product = products.find((p) => p.id === productId);
    updateItem(index, {
      productId,
      productName: product?.name ?? "",
      unitPrice: product?.unitPrice ?? 0,
    });
  };

  const shippingFeeNum = shippingFee === "" ? 0 : Number(shippingFee);
  const totalAmount = roundHalfUp2(
    items.reduce(
      (sum, item) =>
        sum +
        lineSubtotal(
          item.unitPrice,
          Number(item.quantity) || 0,
          Number(item.discount) || 0,
        ),
      0,
    ) + (Number.isNaN(shippingFeeNum) ? 0 : shippingFeeNum),
  );

  const handleSubmit = () => {
    if (customerId === "") {
      alert(t("page.orderNew.alerts.pleaseSelectCustomer"));
      return;
    }
    if (taxStatusId === "") {
      alert(t("page.orderNew.alerts.pleaseSelectTaxStatus"));
      return;
    }
    const validItems = items.filter((item) => item.productId !== "");
    if (validItems.length === 0) {
      alert(t("page.orderNew.alerts.pleaseAddItem"));
      return;
    }
    for (const item of validItems) {
      if (!item.quantity || Number(item.quantity) < 1) {
        alert(t("page.orderNew.alerts.minQty"));
        return;
      }
    }

    const body = {
      customerId,
      shipperId: shipperId === "" ? null : shipperId,
      requiredDate: convertEmptyStringToNull(requiredDate),
      taxStatusId,
      paymentType: convertEmptyStringToNull(paymentType),
      shippingFee: shippingFee === "" ? null : Number(shippingFee),
      notes: convertEmptyStringToNull(notes),
      orderDetails: validItems.map((item) => ({
        productId: item.productId,
        quantity: Number(item.quantity),
        discount: item.discount === "" ? 0 : Number(item.discount),
      })),
    };

    setLoading(true);
    privateApi
      .post("/v1/orders", body)
      .then((res) => {
        const data: ApiIfs<OrderIfs> = res.data;
        alert(t("page.orderNew.alerts.createSuccess"));
        if (data?.body) {
          navigate(`/orders/${data.body.id}`);
        } else {
          navigate("/orders");
        }
      })
      .catch((err) => {
        const data: ApiIfs<null> = err.response?.data;
        if (data?.result?.code === 1400) {
          const lines = Object.values(data?.body || {});
          alert(t("page.orderNew.alerts.invalidInput", { message: lines.join("\n") }));
          return;
        }
        const message = data?.result?.description ?? "";
        alert(t("page.orderNew.alerts.createFailed", { message }));
      })
      .finally(() => setLoading(false));
  };

  return (
    <Wrapper>
      <Title>{t("page.orderNew.title")}</Title>
      <Content>
        <Header>
          <BackBtn onClick={() => navigate("/orders")}>
            {t("page.orderNew.back")}
          </BackBtn>
        </Header>

        <Card>
          <CardTitle>{t("page.orderNew.orderInfo")}</CardTitle>
          <Grid>
            <FieldRow>
              <Label>{t("page.orderNew.customer")}</Label>
              <Select
                value={customerId}
                onChange={(e) =>
                  setCustomerId(
                    e.target.value === "" ? "" : Number(e.target.value),
                  )
                }
              >
                <option value="">{t("page.orderNew.selectCustomer")}</option>
                {customers.map((c) => (
                  <option key={c.id} value={c.id}>
                    {c.name}
                  </option>
                ))}
              </Select>
            </FieldRow>
            <FieldRow>
              <Label>{t("page.orderNew.shipper")}</Label>
              <Select
                value={shipperId}
                onChange={(e) =>
                  setShipperId(
                    e.target.value === "" ? "" : Number(e.target.value),
                  )
                }
              >
                <option value="">{t("page.orderNew.selectShipper")}</option>
                {shippers.map((s) => (
                  <option key={s.id} value={s.id}>
                    {s.name}
                  </option>
                ))}
              </Select>
            </FieldRow>
            <FieldRow>
              <Label>{t("page.orderNew.taxStatus")}</Label>
              <Select
                value={taxStatusId}
                onChange={(e) =>
                  setTaxStatusId(
                    e.target.value === "" ? "" : Number(e.target.value),
                  )
                }
              >
                <option value="">{t("page.orderNew.selectTaxStatus")}</option>
                {taxStatuses.map((status) => (
                  <option key={status.id} value={status.id}>
                    {status.status}
                  </option>
                ))}
              </Select>
            </FieldRow>
            <FieldRow>
              <Label>{t("page.orderNew.requiredDate")}</Label>
              <Input
                type="date"
                value={requiredDate}
                onChange={(e) => setRequiredDate(e.target.value)}
              />
            </FieldRow>
            <FieldRow>
              <Label>{t("page.orderNew.paymentType")}</Label>
              <Input
                value={paymentType}
                onChange={(e) => setPaymentType(e.target.value)}
              />
            </FieldRow>
            <FieldRow>
              <Label>{t("page.orderNew.shippingFee")}</Label>
              <Input
                type="number"
                value={shippingFee}
                onChange={(e) => setShippingFee(e.target.value)}
              />
            </FieldRow>
          </Grid>
          <FieldRow>
            <Label>{t("page.orderNew.notes")}</Label>
            <TextArea value={notes} onChange={(e) => setNotes(e.target.value)} />
          </FieldRow>
        </Card>

        <Card>
          <CardTitle>{t("page.orderNew.orderItems")}</CardTitle>
          <FieldRow>
            <Label>{t("page.orderNew.productSearch")}</Label>
            <Input
              placeholder={t("page.orderNew.productSearchPlaceholder")}
              value={productKeyword}
              onChange={(e) => setProductKeyword(e.target.value)}
            />
          </FieldRow>
          <ItemTable>
            <thead>
              <tr>
                <th>{t("page.orderNew.col.product")}</th>
                <th>{t("page.orderNew.col.unitPrice")}</th>
                <th>{t("page.orderNew.col.qty")}</th>
                <th>{t("page.orderNew.col.discount")}</th>
                <th>{t("page.orderNew.col.subtotal")}</th>
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
                      <option value="">{t("page.orderNew.selectProduct")}</option>
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
                  <td>${Number(item.unitPrice).toFixed(2)}</td>
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
                    <SmallInput
                      type="number"
                      min={0}
                      max={100}
                      value={item.discount}
                      onChange={(e) =>
                        updateItem(index, { discount: e.target.value })
                      }
                    />
                  </td>
                  <td>
                    $
                    {lineSubtotal(
                      item.unitPrice,
                      Number(item.quantity) || 0,
                      Number(item.discount) || 0,
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
                <td colSpan={4} style={{ textAlign: "right" }}>
                  <strong>{t("page.orderNew.total")}</strong>
                </td>
                <td>
                  <strong>${totalAmount.toFixed(2)}</strong>
                </td>
                <td />
              </tr>
            </tfoot>
          </ItemTable>
          <AddBtn type="button" onClick={addItem}>
            {t("page.orderNew.addItem")}
          </AddBtn>
        </Card>

        <ActionBar>
          <PrimaryBtn type="button" onClick={handleSubmit} disabled={loading}>
            {t("page.orderNew.createOrder")}
          </PrimaryBtn>
          <SecondaryBtn
            type="button"
            onClick={() => navigate("/orders")}
            disabled={loading}
          >
            {t("page.orderNew.cancel")}
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
  width: 80px;

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
