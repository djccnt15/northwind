import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import styled from "styled-components";
import type { ApiIfs } from "../entities/app";
import type {
  OrderDetailStatusIfs,
  OrderIfs,
  OrderStatusIfs,
} from "../entities";
import { privateApi } from "../shared/api";
import {
  commBorderRadius,
  commBtnHoverSkyBlue,
  commBtnSkyBlue,
  commBtnSkyBlueBoxShadow,
  globalTransition,
  PageWrapper,
  Title,
} from "../shared/ui";

// Header status rank for forward-only transition (matches backend rule)
const STATUS_RANK: Record<string, number> = {
  PENDING: 1,
  PAID: 2,
  SHIPPED: 3,
  DELIVERED: 4,
};
const TERMINAL_CODES = ["DELIVERED", "CANCELLED"];

// Compute which target statuses are selectable from the current header status.
const allowedNextStatuses = (
  current: { code: string },
  all: OrderStatusIfs[],
): OrderStatusIfs[] => {
  if (TERMINAL_CODES.includes(current.code)) return [];
  return all.filter((s) => {
    if (s.code === current.code) return false;
    if (s.code === "CANCELLED") return true;
    const currentRank = STATUS_RANK[current.code];
    const targetRank = STATUS_RANK[s.code];
    if (currentRank == null || targetRank == null) return false;
    return targetRank > currentRank;
  });
};

export default function OrderDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { t } = useTranslation();

  const [order, setOrder] = useState<OrderIfs | null>(null);
  const [loading, setLoading] = useState(false);
  const [orderStatuses, setOrderStatuses] = useState<OrderStatusIfs[]>([]);
  const [detailStatuses, setDetailStatuses] = useState<OrderDetailStatusIfs[]>(
    [],
  );

  const fetchOrder = () => {
    if (!id) return;
    setLoading(true);
    privateApi
      .get(`/v1/orders/${id}`)
      .then((res) => {
        const data: ApiIfs<OrderIfs> = res.data;
        setOrder(data?.body ?? null);
      })
      .catch((err) => {
        console.error("Failed to fetch order:", err);
        alert(t("page.orderDetail.fetchFailed"));
      })
      .finally(() => setLoading(false));
  };

  const fetchMeta = () => {
    privateApi
      .get("/v1/order-statuses")
      .then((res) => {
        const data: ApiIfs<OrderStatusIfs[]> = res.data;
        setOrderStatuses(data?.body ?? []);
      })
      .catch(console.error);
    privateApi
      .get("/v1/order-detail-statuses")
      .then((res) => {
        const data: ApiIfs<OrderDetailStatusIfs[]> = res.data;
        setDetailStatuses(data?.body ?? []);
      })
      .catch(console.error);
  };

  useEffect(() => {
    queueMicrotask(() => {
      fetchOrder();
      fetchMeta();
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  const handleStatusChange = (statusId: number) => {
    if (!order) return;
    setLoading(true);
    privateApi
      .patch(`/v1/orders/${order.id}/status`, { statusId })
      .then((res) => {
        const data: ApiIfs<OrderIfs> = res.data;
        setOrder(data?.body ?? null);
      })
      .catch((err) => {
        const data: ApiIfs<null> = err.response?.data;
        const message = data?.result?.description ?? "";
        alert(t("page.orderDetail.updateStatusFailed", { message }));
      })
      .finally(() => setLoading(false));
  };

  const handleDetailStatusChange = (detailId: number, statusId: number) => {
    if (!order) return;
    setLoading(true);
    privateApi
      .patch(`/v1/orders/${order.id}/details/${detailId}/status`, { statusId })
      .then((res) => {
        const data: ApiIfs<OrderIfs> = res.data;
        setOrder(data?.body ?? null);
      })
      .catch((err) => {
        const data: ApiIfs<null> = err.response?.data;
        const message = data?.result?.description ?? "";
        alert(t("page.orderDetail.updateItemFailed", { message }));
      })
      .finally(() => setLoading(false));
  };

  if (!order) {
    return (
      <Wrapper>
        <Title>{t("page.orderDetail.title")}</Title>
        <Content>
          <BackBtn onClick={() => navigate("/orders")}>
            {t("page.orderDetail.back")}
          </BackBtn>
          <ReadValue>
            {loading
              ? t("page.orderDetail.loading")
              : t("page.orderDetail.notFound")}
          </ReadValue>
        </Content>
      </Wrapper>
    );
  }

  const nextStatuses = allowedNextStatuses(order.status, orderStatuses);

  return (
    <Wrapper>
      <Title>{t("page.orderDetail.title")}</Title>
      <Content>
        <Header>
          <BackBtn onClick={() => navigate("/orders")}>
            {t("page.orderDetail.back")}
          </BackBtn>
          <HeaderTitle>
            {t("page.orderDetail.orderTitle", { id: order.id })}
          </HeaderTitle>
          <BadgeBlue>{order.status.name}</BadgeBlue>
          {nextStatuses.length > 0 && (
            <StatusSelect
              value=""
              disabled={loading}
              onChange={(e) => {
                if (e.target.value !== "")
                  handleStatusChange(Number(e.target.value));
              }}
            >
              <option value="">{t("page.orderDetail.changeStatus")}</option>
              {nextStatuses.map((s) => (
                <option key={s.id} value={s.id}>
                  {s.name}
                </option>
              ))}
            </StatusSelect>
          )}
        </Header>

        <Card>
          <CardTitle>{t("page.orderDetail.basicInfo")}</CardTitle>
          <Grid>
            <FieldRow>
              <Label>{t("page.orderDetail.customer")}</Label>
              <ReadValue>{order.customer.name}</ReadValue>
            </FieldRow>
            <FieldRow>
              <Label>{t("page.orderDetail.shipper")}</Label>
              <ReadValue>{order.shipper?.name ?? "-"}</ReadValue>
            </FieldRow>
            <FieldRow>
              <Label>{t("page.orderDetail.orderDate")}</Label>
              <ReadValue>{order.orderDate}</ReadValue>
            </FieldRow>
            <FieldRow>
              <Label>{t("page.orderDetail.requiredDate")}</Label>
              <ReadValue>{order.requiredDate ?? "-"}</ReadValue>
            </FieldRow>
            <FieldRow>
              <Label>{t("page.orderDetail.shippedDate")}</Label>
              <ReadValue>{order.shippedDate ?? "-"}</ReadValue>
            </FieldRow>
            <FieldRow>
              <Label>{t("page.orderDetail.paidDate")}</Label>
              <ReadValue>{order.paidDate ?? "-"}</ReadValue>
            </FieldRow>
            <FieldRow>
              <Label>{t("page.orderDetail.taxStatus")}</Label>
              <ReadValue>{order.taxStatus?.status ?? "-"}</ReadValue>
            </FieldRow>
            <FieldRow>
              <Label>{t("page.orderDetail.paymentType")}</Label>
              <ReadValue>{order.paymentType ?? "-"}</ReadValue>
            </FieldRow>
            <FieldRow>
              <Label>{t("page.orderDetail.shippingFee")}</Label>
              <ReadValue>
                {order.shippingFee == null
                  ? "-"
                  : `$${Number(order.shippingFee).toFixed(2)}`}
              </ReadValue>
            </FieldRow>
            <FieldRow>
              <Label>{t("page.orderDetail.notes")}</Label>
              <ReadValue>{order.notes ?? "-"}</ReadValue>
            </FieldRow>
          </Grid>
        </Card>

        <Card>
          <CardTitle>{t("page.orderDetail.orderItems")}</CardTitle>
          <ItemTable>
            <thead>
              <tr>
                <th>{t("page.orderDetail.col.product")}</th>
                <th>{t("page.orderDetail.col.unitPrice")}</th>
                <th>{t("page.orderDetail.col.qty")}</th>
                <th>{t("page.orderDetail.col.discount")}</th>
                <th>{t("page.orderDetail.col.subtotal")}</th>
                <th>{t("page.orderDetail.col.itemStatus")}</th>
              </tr>
            </thead>
            <tbody>
              {order.orderDetails.map((detail) => (
                <tr key={detail.id}>
                  <td>{detail.product.name}</td>
                  <td>${Number(detail.unitPrice).toFixed(2)}</td>
                  <td>{detail.quantity}</td>
                  <td>{detail.discount}%</td>
                  <td>${Number(detail.subtotal).toFixed(2)}</td>
                  <td>
                    <StatusSelect
                      value={detail.status.id}
                      disabled={loading}
                      onChange={(e) =>
                        handleDetailStatusChange(
                          detail.id,
                          Number(e.target.value),
                        )
                      }
                    >
                      {detailStatuses.map((s) => (
                        <option key={s.id} value={s.id}>
                          {s.name}
                        </option>
                      ))}
                    </StatusSelect>
                  </td>
                </tr>
              ))}
            </tbody>
            <tfoot>
              <tr>
                <td colSpan={4} />
                <td>
                  <strong>{t("page.orderDetail.total")}</strong>
                </td>
                <td colSpan={1}>
                  <strong>${Number(order.totalAmount).toFixed(2)}</strong>
                </td>
              </tr>
            </tfoot>
          </ItemTable>
        </Card>
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

const ReadValue = styled.span`
  font-size: 15px;
`;

const StatusSelect = styled.select`
  ${commBorderRadius}
  border: 1px solid #ccc;
  font-size: 14px;
  height: 36px;
  padding: 0 8px;
  background-color: white;

  &:focus {
    outline: none;
    ${commBtnSkyBlueBoxShadow}
  }

  &:disabled {
    opacity: 0.6;
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
    ${commBtnHoverSkyBlue}
    background-color: #d4d4d4;
  }
`;
