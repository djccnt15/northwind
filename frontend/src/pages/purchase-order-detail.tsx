import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import styled from "styled-components";
import type { ApiIfs } from "../entities/app";
import type { PurchaseOrderIfs, PurchaseOrderStatusIfs } from "../entities";
import { privateApi } from "../shared/api";
import {
  commBorderRadius,
  commBtnHoverSkyBlue,
  commBtnHoverTomatoRed,
  commBtnSkyBlue,
  commBtnSkyBlueBoxShadow,
  commBtnTomatoRed,
  globalTransition,
  ModalDefault,
  ModalOverlay,
  PageWrapper,
  Title,
} from "../shared/ui";
import { useKeyDown } from "../shared/useKeyDown";

// Forward-only workflow rank (matches backend transition rule).
const STATUS_RANK: Record<string, number> = {
  DRAFT: 1,
  PENDING_APPROVAL: 2,
  APPROVED: 3,
  RECEIVED: 4,
  PAID: 5,
};
const TERMINAL_CODES = ["PAID", "REJECTED"];
// REJECTED is only reachable from DRAFT or PENDING_APPROVAL.
const REJECTABLE_CODES = ["DRAFT", "PENDING_APPROVAL"];

const LABEL_BY_CODE: Record<string, string> = {
  PENDING_APPROVAL: "Request Approval",
  APPROVED: "Approve",
  RECEIVED: "Receive",
  PAID: "Mark as Paid",
  REJECTED: "Reject",
};

// Action buttons available from the current header status.
const allowedActions = (
  current: { code: string },
  all: PurchaseOrderStatusIfs[],
): { id: number; label: string; code: string }[] => {
  if (TERMINAL_CODES.includes(current.code)) return [];

  const currentRank = STATUS_RANK[current.code];
  const actions: { id: number; label: string; code: string }[] = [];

  for (const s of all) {
    if (s.code === current.code) continue;
    if (s.code === "REJECTED") {
      if (REJECTABLE_CODES.includes(current.code)) {
        actions.push({ id: s.id, label: LABEL_BY_CODE.REJECTED, code: s.code });
      }
      continue;
    }
    const targetRank = STATUS_RANK[s.code];
    if (currentRank == null || targetRank == null) continue;
    // Only offer the immediate next forward step to keep the workflow clear.
    if (targetRank === currentRank + 1) {
      actions.push({
        id: s.id,
        label: LABEL_BY_CODE[s.code] ?? s.name,
        code: s.code,
      });
    }
  }

  return actions;
};

export default function PurchaseOrderDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [order, setOrder] = useState<PurchaseOrderIfs | null>(null);
  const [loading, setLoading] = useState(false);
  const [statuses, setStatuses] = useState<PurchaseOrderStatusIfs[]>([]);

  // Payment modal state (for PAID transition)
  const [paymentModalOpen, setPaymentModalOpen] = useState(false);
  const [paidStatusId, setPaidStatusId] = useState<number | null>(null);
  const [paymentDate, setPaymentDate] = useState("");
  const [paymentAmount, setPaymentAmount] = useState("");
  const [paymentMethod, setPaymentMethod] = useState("");

  useKeyDown("Escape", () => setPaymentModalOpen(false));

  const fetchOrder = () => {
    if (!id) return;
    setLoading(true);
    privateApi
      .get(`/v1/purchase-orders/${id}`)
      .then((res) => {
        const data: ApiIfs<PurchaseOrderIfs> = res.data;
        setOrder(data?.body ?? null);
      })
      .catch((err) => {
        console.error("Failed to fetch purchase order:", err);
        alert("Failed to fetch purchase order. Please try again.");
      })
      .finally(() => setLoading(false));
  };

  const fetchStatuses = () => {
    privateApi
      .get("/v1/purchase-order-statuses")
      .then((res) => {
        const data: ApiIfs<PurchaseOrderStatusIfs[]> = res.data;
        setStatuses(data?.body ?? []);
      })
      .catch(console.error);
  };

  useEffect(() => {
    queueMicrotask(() => {
      fetchOrder();
      fetchStatuses();
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  const patchStatus = (body: Record<string, unknown>) => {
    if (!order) return;
    setLoading(true);
    privateApi
      .patch(`/v1/purchase-orders/${order.id}/status`, body)
      .then((res) => {
        const data: ApiIfs<PurchaseOrderIfs> = res.data;
        setOrder(data?.body ?? null);
      })
      .catch((err) => {
        const data: ApiIfs<null> = err.response?.data;
        const message = data?.result?.description || "Unknown error";
        alert(`Failed to update status: ${message}`);
      })
      .finally(() => setLoading(false));
  };

  const handleAction = (statusId: number) => {
    const target = statuses.find((s) => s.id === statusId);
    if (target?.code === "PAID") {
      // Open payment input modal for PAID transition.
      setPaidStatusId(statusId);
      setPaymentDate("");
      setPaymentAmount(
        order?.totalAmount != null ? String(order.totalAmount) : "",
      );
      setPaymentMethod("");
      setPaymentModalOpen(true);
      return;
    }
    if (
      target?.code === "REJECTED" &&
      !window.confirm("Reject this purchase order?")
    ) {
      return;
    }
    patchStatus({ statusId });
  };

  const handlePaymentSubmit = () => {
    if (paidStatusId == null) return;
    patchStatus({
      statusId: paidStatusId,
      paymentDate: paymentDate === "" ? null : paymentDate,
      paymentAmount: paymentAmount === "" ? null : Number(paymentAmount),
      paymentMethod: paymentMethod === "" ? null : paymentMethod,
    });
    setPaymentModalOpen(false);
  };

  if (!order) {
    return (
      <Wrapper>
        <Title>Purchase Order Detail</Title>
        <Content>
          <BackBtn onClick={() => navigate("/purchase-orders")}>
            ← Back to list
          </BackBtn>
          <ReadValue>
            {loading ? "Loading..." : "Purchase order not found."}
          </ReadValue>
        </Content>
      </Wrapper>
    );
  }

  const actions = allowedActions(order.status, statuses);

  return (
    <Wrapper>
      <Title>Purchase Order Detail</Title>
      <Content>
        <Header>
          <BackBtn onClick={() => navigate("/purchase-orders")}>
            ← Back to list
          </BackBtn>
          <HeaderTitle>Purchase Order #{order.id}</HeaderTitle>
          <BadgeBlue>{order.status.name}</BadgeBlue>
          {actions.map((action) =>
            action.code === "REJECTED" ? (
              <RejectBtn
                key={action.id}
                type="button"
                disabled={loading}
                onClick={() => handleAction(action.id)}
              >
                {action.label}
              </RejectBtn>
            ) : (
              <ActionBtn
                key={action.id}
                type="button"
                disabled={loading}
                onClick={() => handleAction(action.id)}
              >
                {action.label}
              </ActionBtn>
            ),
          )}
        </Header>

        <Card>
          <CardTitle>Basic Information</CardTitle>
          <Grid>
            <FieldRow>
              <Label>Vendor</Label>
              <ReadValue>{order.vendor?.name ?? "-"}</ReadValue>
            </FieldRow>
            <FieldRow>
              <Label>Status</Label>
              <ReadValue>{order.status.name}</ReadValue>
            </FieldRow>
            <FieldRow>
              <Label>Submitted Date</Label>
              <ReadValue>{order.submittedDate}</ReadValue>
            </FieldRow>
            <FieldRow>
              <Label>Approved Date</Label>
              <ReadValue>{order.approvedDate ?? "-"}</ReadValue>
            </FieldRow>
            <FieldRow>
              <Label>Received Date</Label>
              <ReadValue>{order.receivedDate ?? "-"}</ReadValue>
            </FieldRow>
            <FieldRow>
              <Label>Payment Date</Label>
              <ReadValue>{order.paymentDate ?? "-"}</ReadValue>
            </FieldRow>
            <FieldRow>
              <Label>Submitted By</Label>
              <ReadValue>
                {order.submittedBy
                  ? `${order.submittedBy.firstName} ${order.submittedBy.lastName}`
                  : "-"}
              </ReadValue>
            </FieldRow>
            <FieldRow>
              <Label>Approved By</Label>
              <ReadValue>
                {order.approvedBy
                  ? `${order.approvedBy.firstName} ${order.approvedBy.lastName}`
                  : "-"}
              </ReadValue>
            </FieldRow>
            <FieldRow>
              <Label>Shipping Fee</Label>
              <ReadValue>
                {order.shippingFee == null
                  ? "-"
                  : `$${Number(order.shippingFee).toFixed(2)}`}
              </ReadValue>
            </FieldRow>
            <FieldRow>
              <Label>Tax Amount</Label>
              <ReadValue>
                {order.taxAmount == null
                  ? "-"
                  : `$${Number(order.taxAmount).toFixed(2)}`}
              </ReadValue>
            </FieldRow>
            <FieldRow>
              <Label>Payment Amount</Label>
              <ReadValue>
                {order.paymentAmount == null
                  ? "-"
                  : `$${Number(order.paymentAmount).toFixed(2)}`}
              </ReadValue>
            </FieldRow>
            <FieldRow>
              <Label>Payment Method</Label>
              <ReadValue>{order.paymentMethod ?? "-"}</ReadValue>
            </FieldRow>
            <FieldRow>
              <Label>Notes</Label>
              <ReadValue>{order.note ?? "-"}</ReadValue>
            </FieldRow>
          </Grid>
        </Card>

        <Card>
          <CardTitle>Purchase Order Items</CardTitle>
          <ItemTable>
            <thead>
              <tr>
                <th>Product</th>
                <th>Unit Price</th>
                <th>Qty</th>
                <th>Subtotal</th>
              </tr>
            </thead>
            <tbody>
              {order.purchaseOrderDetails.map((detail) => (
                <tr key={detail.id}>
                  <td>{detail.product.name}</td>
                  <td>${Number(detail.unitPrice).toFixed(2)}</td>
                  <td>{detail.quantity}</td>
                  <td>${Number(detail.subtotal).toFixed(2)}</td>
                </tr>
              ))}
            </tbody>
            <tfoot>
              <tr>
                <td colSpan={2} />
                <td>
                  <strong>Total</strong>
                </td>
                <td>
                  <strong>${Number(order.totalAmount).toFixed(2)}</strong>
                </td>
              </tr>
            </tfoot>
          </ItemTable>
        </Card>
      </Content>

      {paymentModalOpen && (
        <ModalOverlay onClick={() => setPaymentModalOpen(false)}>
          <Modal onClick={(e) => e.stopPropagation()}>
            <CardTitle>Payment Information</CardTitle>
            <FieldRow>
              <Label>Payment Date</Label>
              <Input
                type="date"
                value={paymentDate}
                onChange={(e) => setPaymentDate(e.target.value)}
              />
            </FieldRow>
            <FieldRow>
              <Label>Payment Amount</Label>
              <Input
                type="number"
                value={paymentAmount}
                onChange={(e) => setPaymentAmount(e.target.value)}
              />
            </FieldRow>
            <FieldRow>
              <Label>Payment Method</Label>
              <Input
                value={paymentMethod}
                onChange={(e) => setPaymentMethod(e.target.value)}
              />
            </FieldRow>
            <ModalActions>
              <ActionBtn
                type="button"
                disabled={loading}
                onClick={handlePaymentSubmit}
              >
                Confirm Payment
              </ActionBtn>
              <SecondaryBtn
                type="button"
                disabled={loading}
                onClick={() => setPaymentModalOpen(false)}
              >
                Cancel
              </SecondaryBtn>
            </ModalActions>
          </Modal>
        </ModalOverlay>
      )}
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

const ActionBtn = styled.button`
  ${commBorderRadius}
  ${commBtnSkyBlue}
  ${globalTransition}
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

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
`;

const RejectBtn = styled.button`
  ${commBorderRadius}
  ${commBtnTomatoRed}
  ${globalTransition}
  height: 36px;
  padding: 0 16px;
  border: none;
  color: white;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;

  &:hover {
    ${commBtnHoverTomatoRed}
  }

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
`;

const SecondaryBtn = styled.button`
  ${commBorderRadius}
  ${globalTransition}
  height: 36px;
  padding: 0 16px;
  border: none;
  background-color: #888;
  color: white;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;

  &:hover {
    background-color: #6f6f6f;
  }

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
`;

const Modal = styled(ModalDefault)`
  display: flex;
  flex-direction: column;
  gap: 14px;
  min-width: 360px;
`;

const ModalActions = styled.div`
  display: flex;
  gap: 10px;
  margin-top: 6px;
`;
