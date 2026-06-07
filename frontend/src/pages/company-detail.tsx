import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import styled from "styled-components";
import type { ApiIfs } from "../entities/app";
import type {
  CompanyIfs,
  CompanyTypeIfs,
  OrderSummaryIfs,
  PurchaseOrderSummaryIfs,
  TaxStatusIfs,
} from "../entities";
import { ContactPanel } from "../features/company";
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

interface CompanyFormState {
  name: string;
  businessPhone: string;
  website: string;
  address: string;
  city: string;
  region: string;
  zipCode: string;
  country: string;
  notes: string;
  companyTypeId: number | "";
  taxStatusId: number | "";
}

const emptyForm: CompanyFormState = {
  name: "",
  businessPhone: "",
  website: "",
  address: "",
  city: "",
  region: "",
  zipCode: "",
  country: "",
  notes: "",
  companyTypeId: "",
  taxStatusId: "",
};

const companyToForm = (company: CompanyIfs): CompanyFormState => ({
  name: company.name,
  businessPhone: company.businessPhone ?? "",
  website: company.website ?? "",
  address: company.address ?? "",
  city: company.city ?? "",
  region: company.region ?? "",
  zipCode: company.zipCode ?? "",
  country: company.country ?? "",
  notes: company.notes ?? "",
  companyTypeId: company.companyType?.id ?? "",
  taxStatusId: company.taxStatus?.id ?? "",
});

const isCustomerType = (label?: string) =>
  !!label && (label.includes("고객") || label.toLowerCase().includes("customer"));

const isSupplierType = (label?: string) =>
  !!label &&
  (label.includes("공급") ||
    label.toLowerCase().includes("supplier") ||
    label.toLowerCase().includes("vendor"));

export default function CompanyDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [company, setCompany] = useState<CompanyIfs | null>(null);
  const [form, setForm] = useState<CompanyFormState>(emptyForm);
  const [isEditing, setIsEditing] = useState(false);
  const [loading, setLoading] = useState(false);
  const [companyTypes, setCompanyTypes] = useState<CompanyTypeIfs[]>([]);
  const [taxStatuses, setTaxStatuses] = useState<TaxStatusIfs[]>([]);
  const [orders, setOrders] = useState<OrderSummaryIfs[]>([]);
  const [purchaseOrders, setPurchaseOrders] = useState<
    PurchaseOrderSummaryIfs[]
  >([]);

  const fetchCompany = () => {
    if (!id) return;
    setLoading(true);
    privateApi
      .get(`/v1/companies/${id}`)
      .then((res) => {
        const data: ApiIfs<CompanyIfs> = res.data;
        setCompany(data?.body ?? null);
        if (data?.body) setForm(companyToForm(data.body));
      })
      .catch((err) => {
        console.error("Failed to fetch company:", err);
        alert("Failed to fetch company. Please try again.");
      })
      .finally(() => setLoading(false));
  };

  const fetchHistories = () => {
    if (!id) return;
    privateApi
      .get(`/v1/companies/${id}/orders`)
      .then((res) => {
        const data: ApiIfs<OrderSummaryIfs[]> = res.data;
        setOrders(data?.body ?? []);
      })
      .catch(console.error);
    privateApi
      .get(`/v1/companies/${id}/purchase-orders`)
      .then((res) => {
        const data: ApiIfs<PurchaseOrderSummaryIfs[]> = res.data;
        setPurchaseOrders(data?.body ?? []);
      })
      .catch(console.error);
  };

  const fetchMeta = () => {
    privateApi
      .get("/v1/company-types")
      .then((res) => {
        const data: ApiIfs<CompanyTypeIfs[]> = res.data;
        setCompanyTypes(data?.body ?? []);
      })
      .catch(console.error);
    privateApi
      .get("/v1/tax-statuses")
      .then((res) => {
        const data: ApiIfs<TaxStatusIfs[]> = res.data;
        setTaxStatuses(data?.body ?? []);
      })
      .catch(console.error);
  };

  useEffect(() => {
    queueMicrotask(() => {
      fetchCompany();
      fetchHistories();
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  const updateField =
    (field: keyof CompanyFormState) =>
    (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
      setForm((prev) => ({ ...prev, [field]: e.target.value }));
    };

  const handleEditClick = () => {
    if (company) setForm(companyToForm(company));
    fetchMeta();
    setIsEditing(true);
  };

  const handleCancelClick = () => {
    if (company) setForm(companyToForm(company));
    setIsEditing(false);
  };

  const handleSaveClick = () => {
    if (!company) return;
    if (!form.name.trim()) {
      alert("Company name is required.");
      return;
    }
    if (form.companyTypeId === "" || form.taxStatusId === "") {
      alert("Please select company type and tax status.");
      return;
    }
    const body = convertEmptyStringToNull({
      name: form.name.trim(),
      businessPhone: form.businessPhone,
      website: form.website,
      address: form.address,
      city: form.city,
      region: form.region,
      zipCode: form.zipCode,
      country: form.country,
      notes: form.notes,
    }) as Record<string, unknown>;
    body.companyTypeId = form.companyTypeId;
    body.taxStatusId = form.taxStatusId;

    setLoading(true);
    privateApi
      .put(`/v1/companies/${company.id}`, body)
      .then((res) => {
        const data: ApiIfs<CompanyIfs> = res.data;
        setCompany(data?.body ?? null);
        if (data?.body) setForm(companyToForm(data.body));
        setIsEditing(false);
        alert("Company updated successfully.");
      })
      .catch((err) => {
        const data: ApiIfs<null> = err.response?.data;
        if (data?.result?.code === 1400) {
          const lines = Object.values(data?.body || {});
          alert(`Invalid input:\n${lines.join("\n")}`);
          return;
        }
        const message = data?.result?.description || "Unknown error";
        alert(`Failed to update company: ${message}`);
      })
      .finally(() => setLoading(false));
  };

  const handleDeleteClick = () => {
    if (!company) return;
    if (!window.confirm(`Delete company "${company.name}"?`)) return;
    setLoading(true);
    privateApi
      .delete(`/v1/companies/${company.id}`)
      .then(() => {
        alert("Company deleted successfully.");
        navigate("/companies");
      })
      .catch((err) => {
        const data: ApiIfs<null> = err.response?.data;
        const message = data?.result?.description || "Unknown error";
        alert(`Failed to delete company: ${message}`);
        setLoading(false);
      });
  };

  if (!company) {
    return (
      <Wrapper>
        <Title>거래처 상세</Title>
        <Content>
          <BackBtn onClick={() => navigate("/companies")}>← 목록으로</BackBtn>
          <ReadValue>{loading ? "Loading..." : "Company not found."}</ReadValue>
        </Content>
      </Wrapper>
    );
  }

  const typeLabel = company.companyType?.companyType;
  const showOrders = isCustomerType(typeLabel) || orders.length > 0;
  const showPurchaseOrders =
    isSupplierType(typeLabel) || purchaseOrders.length > 0;

  return (
    <Wrapper>
      <Title>거래처 상세</Title>
      <Content>
        <Header>
          <BackBtn onClick={() => navigate("/companies")}>← 목록으로</BackBtn>
          <HeaderTitle>{company.name}</HeaderTitle>
          {typeLabel && <BadgeBlue>{typeLabel}</BadgeBlue>}
          {company.taxStatus?.status && (
            <BadgeGray>과세: {company.taxStatus.status}</BadgeGray>
          )}
        </Header>

        <Grid>
          <Card>
            <CardTitle>기본 정보</CardTitle>
            <FieldRow>
              <Label>거래처명</Label>
              {isEditing ? (
                <Input value={form.name} onChange={updateField("name")} />
              ) : (
                <ReadValue>{company.name}</ReadValue>
              )}
            </FieldRow>
            <FieldRow>
              <Label>거래처 유형</Label>
              {isEditing ? (
                <Select
                  value={form.companyTypeId}
                  onChange={(e) =>
                    setForm((prev) => ({
                      ...prev,
                      companyTypeId:
                        e.target.value === "" ? "" : Number(e.target.value),
                    }))
                  }
                >
                  <option value="">유형 선택</option>
                  {companyTypes.map((type) => (
                    <option key={type.id} value={type.id}>
                      {type.companyType}
                    </option>
                  ))}
                </Select>
              ) : (
                <ReadValue>{typeLabel ?? ""}</ReadValue>
              )}
            </FieldRow>
            <FieldRow>
              <Label>과세 유형</Label>
              {isEditing ? (
                <Select
                  value={form.taxStatusId}
                  onChange={(e) =>
                    setForm((prev) => ({
                      ...prev,
                      taxStatusId:
                        e.target.value === "" ? "" : Number(e.target.value),
                    }))
                  }
                >
                  <option value="">과세 유형 선택</option>
                  {taxStatuses.map((status) => (
                    <option key={status.id} value={status.id}>
                      {status.status}
                    </option>
                  ))}
                </Select>
              ) : (
                <ReadValue>{company.taxStatus?.status ?? ""}</ReadValue>
              )}
            </FieldRow>
            <FieldRow>
              <Label>전화번호</Label>
              {isEditing ? (
                <Input
                  value={form.businessPhone}
                  onChange={updateField("businessPhone")}
                />
              ) : (
                <ReadValue>{company.businessPhone ?? ""}</ReadValue>
              )}
            </FieldRow>
            <FieldRow>
              <Label>홈페이지</Label>
              {isEditing ? (
                <Input value={form.website} onChange={updateField("website")} />
              ) : (
                <ReadValue>
                  {company.website ? (
                    <a
                      href={company.website}
                      target="_blank"
                      rel="noopener noreferrer"
                    >
                      {company.website}
                    </a>
                  ) : (
                    ""
                  )}
                </ReadValue>
              )}
            </FieldRow>
            <FieldRow>
              <Label>주소</Label>
              {isEditing ? (
                <Input value={form.address} onChange={updateField("address")} />
              ) : (
                <ReadValue>{company.address ?? ""}</ReadValue>
              )}
            </FieldRow>
            <FieldGroup>
              <FieldRow>
                <Label>도시</Label>
                {isEditing ? (
                  <Input value={form.city} onChange={updateField("city")} />
                ) : (
                  <ReadValue>{company.city ?? ""}</ReadValue>
                )}
              </FieldRow>
              <FieldRow>
                <Label>지역</Label>
                {isEditing ? (
                  <Input value={form.region} onChange={updateField("region")} />
                ) : (
                  <ReadValue>{company.region ?? ""}</ReadValue>
                )}
              </FieldRow>
            </FieldGroup>
            <FieldGroup>
              <FieldRow>
                <Label>우편번호</Label>
                {isEditing ? (
                  <Input
                    value={form.zipCode}
                    onChange={updateField("zipCode")}
                  />
                ) : (
                  <ReadValue>{company.zipCode ?? ""}</ReadValue>
                )}
              </FieldRow>
              <FieldRow>
                <Label>국가</Label>
                {isEditing ? (
                  <Input
                    value={form.country}
                    onChange={updateField("country")}
                  />
                ) : (
                  <ReadValue>{company.country ?? ""}</ReadValue>
                )}
              </FieldRow>
            </FieldGroup>
            <FieldRow>
              <Label>비고</Label>
              {isEditing ? (
                <TextArea value={form.notes} onChange={updateField("notes")} />
              ) : (
                <ReadValue>{company.notes ?? ""}</ReadValue>
              )}
            </FieldRow>

            <ActionBar>
              {isEditing ? (
                <>
                  <PrimaryBtn
                    type="button"
                    onClick={handleSaveClick}
                    disabled={loading}
                  >
                    저장
                  </PrimaryBtn>
                  <SecondaryBtn
                    type="button"
                    onClick={handleCancelClick}
                    disabled={loading}
                  >
                    취소
                  </SecondaryBtn>
                </>
              ) : (
                <>
                  <PrimaryBtn type="button" onClick={handleEditClick}>
                    수정
                  </PrimaryBtn>
                  <DangerBtn
                    type="button"
                    onClick={handleDeleteClick}
                    disabled={loading}
                  >
                    삭제
                  </DangerBtn>
                </>
              )}
            </ActionBar>
          </Card>

          <Card>
            <ContactPanel companyId={company.id} />
          </Card>
        </Grid>

        {showOrders && (
          <Card>
            <CardTitle>주문 이력</CardTitle>
            {orders.length === 0 ? (
              <ReadValue style={{ color: "#888" }}>
                주문 이력이 없습니다.
              </ReadValue>
            ) : (
              <HistoryTable>
                <thead>
                  <tr>
                    <th>주문번호</th>
                    <th>주문일</th>
                    <th>배송일</th>
                    <th>결제일</th>
                    <th>상태</th>
                  </tr>
                </thead>
                <tbody>
                  {orders.map((order) => (
                    <tr key={order.id}>
                      <td>{order.id}</td>
                      <td>{order.orderDate ?? ""}</td>
                      <td>{order.shippedDate ?? ""}</td>
                      <td>{order.paidDate ?? ""}</td>
                      <td>{order.status ?? ""}</td>
                    </tr>
                  ))}
                </tbody>
              </HistoryTable>
            )}
          </Card>
        )}

        {showPurchaseOrders && (
          <Card>
            <CardTitle>발주 이력</CardTitle>
            {purchaseOrders.length === 0 ? (
              <ReadValue style={{ color: "#888" }}>
                발주 이력이 없습니다.
              </ReadValue>
            ) : (
              <HistoryTable>
                <thead>
                  <tr>
                    <th>발주번호</th>
                    <th>제출일</th>
                    <th>승인일</th>
                    <th>입고일</th>
                    <th>상태</th>
                  </tr>
                </thead>
                <tbody>
                  {purchaseOrders.map((po) => (
                    <tr key={po.id}>
                      <td>{po.id}</td>
                      <td>{po.submittedDate ?? ""}</td>
                      <td>{po.approvedDate ?? ""}</td>
                      <td>{po.receivedDate ?? ""}</td>
                      <td>{po.status ?? ""}</td>
                    </tr>
                  ))}
                </tbody>
              </HistoryTable>
            )}
          </Card>
        )}
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

const HeaderTitle = styled.h2`
  font-size: 22px;
  font-weight: 700;
`;

const BadgeBlue = styled.span`
  ${commBtnSkyBlue}
  ${commBorderRadius}
  color: white;
  font-size: 12px;
  font-weight: 600;
  padding: 4px 10px;
`;

const BadgeGray = styled.span`
  ${commBorderRadius}
  background-color: #888;
  color: white;
  font-size: 12px;
  font-weight: 600;
  padding: 4px 10px;
`;

const Grid = styled.div`
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  align-items: start;

  @media (max-width: 900px) {
    grid-template-columns: 1fr;
  }
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

const FieldRow = styled.div`
  display: flex;
  flex-direction: column;
  gap: 6px;
  flex: 1;
`;

const FieldGroup = styled.div`
  display: flex;
  gap: 12px;
`;

const Label = styled.label`
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
  color: #666;
`;

const ReadValue = styled.span`
  font-size: 15px;

  a {
    ${globalTransition}
    color: #17c1ff;
    text-decoration: none;

    &:hover {
      ${commBtnHoverSkyBlue}
    }
  }
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

const TextArea = styled.textarea`
  ${commBorderRadius}
  border: 1px solid #ccc;
  font-size: 14px;
  padding: 8px 10px;
  width: 100%;
  min-height: 70px;
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

const ActionBar = styled.div`
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-top: 4px;
`;

const BtnBase = styled.button`
  ${commBorderRadius}
  ${globalTransition}
  height: 40px;
  min-width: 100px;
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

const DangerBtn = styled(BtnBase)`
  ${commBtnTomatoRed}
  &:hover {
    ${commBtnHoverTomatoRed}
  }
`;

const SecondaryBtn = styled(BtnBase)`
  background-color: #888;
  &:hover {
    background-color: #6f6f6f;
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

const HistoryTable = styled.table`
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;

  th,
  td {
    text-align: left;
    padding: 8px 10px;
    border-bottom: 1px solid #eee;
  }

  th {
    font-size: 12px;
    text-transform: uppercase;
    color: #666;
  }
`;
