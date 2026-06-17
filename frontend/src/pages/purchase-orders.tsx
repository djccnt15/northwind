import { Box } from "@mui/material";
import { DataGrid, type GridColDef } from "@mui/x-data-grid";
import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import styled from "styled-components";
import type { ApiIfs, PageIfs } from "../entities/app";
import type {
  PurchaseOrderListItemIfs,
  PurchaseOrderStatusIfs,
} from "../entities";
import { dataGridInitialState } from "../features/data-grid";
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

const Wrapper = styled(PageWrapper)``;

const Toolbar = styled.div`
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
  padding: 0 20px 15px;
`;

const Input = styled.input`
  ${commBorderRadius}
  border: 1px solid #ccc;
  font-size: 14px;
  height: 38px;
  padding: 0 10px;
  width: 220px;

  &:focus {
    outline: none;
    ${commBtnSkyBlueBoxShadow}
  }
`;

const DateInput = styled.input`
  ${commBorderRadius}
  border: 1px solid #ccc;
  font-size: 14px;
  height: 38px;
  padding: 0 10px;

  &:focus {
    outline: none;
    ${commBtnSkyBlueBoxShadow}
  }
`;

const Tabs = styled.div`
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
`;

const Tab = styled.button<{ $active: boolean }>`
  ${commBorderRadius}
  ${globalTransition}
  height: 38px;
  padding: 0 16px;
  border: 1px solid ${(props) => (props.$active ? "#17c1ff" : "#ccc")};
  background-color: ${(props) => (props.$active ? "#17c1ff" : "white")};
  color: ${(props) => (props.$active ? "white" : "#333")};
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;

  &:hover {
    ${globalTransition}
    border-color: #17c1ff;
  }
`;

const Spacer = styled.div`
  flex: 1;
`;

const NewBtn = styled.button`
  ${commBorderRadius}
  ${commBtnSkyBlue}
  ${globalTransition}
  height: 38px;
  padding: 0 16px;
  border: none;
  color: white;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;

  &:hover {
    ${commBtnHoverSkyBlue}
    ${globalTransition}
  }
`;

export default function PurchaseOrders() {
  const navigate = useNavigate();
  const { t } = useTranslation();

  const [rows, setRows] = useState<PurchaseOrderListItemIfs[]>([]);
  const [rowCount, setRowCount] = useState(0);
  const [loading, setLoading] = useState(false);
  const [paginationModel, setPaginationModel] = useState(
    dataGridInitialState.pagination.paginationModel,
  );
  const [keyword, setKeyword] = useState("");
  const [debouncedKeyword, setDebouncedKeyword] = useState("");
  const [statusFilter, setStatusFilter] = useState<number | "">("");
  const [dateFrom, setDateFrom] = useState("");
  const [dateTo, setDateTo] = useState("");
  const [statuses, setStatuses] = useState<PurchaseOrderStatusIfs[]>([]);

  const isFirstFilterRun = useRef(true);

  const columns: GridColDef[] = [
    { field: "id", headerName: t("page.purchaseOrders.col.no"), width: 100 },
    {
      field: "vendorName",
      headerName: t("page.purchaseOrders.col.vendor"),
      flex: 1,
      minWidth: 180,
      valueGetter: (value) => value ?? "",
      renderCell: (params) => (
        <span
          style={{ cursor: "pointer", color: "#17c1ff" }}
          onClick={() => navigate(`/purchase-orders/${params.row.id}`)}
        >
          {params.row.vendorName ?? "-"}
        </span>
      ),
    },
    { field: "submittedDate", headerName: t("page.purchaseOrders.col.submittedDate"), width: 150 },
    {
      field: "totalAmount",
      headerName: t("page.purchaseOrders.col.total"),
      width: 130,
      valueGetter: (value) =>
        value == null ? "" : `$${Number(value).toFixed(2)}`,
    },
    {
      field: "status",
      headerName: t("page.purchaseOrders.col.status"),
      width: 150,
      valueGetter: (_value, row) => row.status?.name ?? "",
    },
  ];

  const fetchPurchaseOrders = (page: number, size: number) => {
    setLoading(true);
    const params: Record<string, unknown> = { page, size };
    if (debouncedKeyword.trim()) params.keyword = debouncedKeyword.trim();
    if (statusFilter !== "") params.status = statusFilter;
    if (dateFrom) params.dateFrom = dateFrom;
    if (dateTo) params.dateTo = dateTo;

    privateApi
      .get("/v1/purchase-orders", { params })
      .then((res) => {
        const data: ApiIfs<PageIfs<PurchaseOrderListItemIfs>> = res.data;
        setRows(data?.body?.content ?? []);
        setRowCount(data?.body?.page?.totalElements ?? 0);
      })
      .catch(console.error)
      .finally(() => setLoading(false));
  };

  // Load purchase order statuses for the filter tabs
  useEffect(() => {
    privateApi
      .get("/v1/purchase-order-statuses")
      .then((res) => {
        const data: ApiIfs<PurchaseOrderStatusIfs[]> = res.data;
        setStatuses(data?.body ?? []);
      })
      .catch(console.error);
  }, []);

  // Debounce search keyword (400ms)
  useEffect(() => {
    const timer = setTimeout(() => setDebouncedKeyword(keyword), 400);
    return () => clearTimeout(timer);
  }, [keyword]);

  // Refetch on pagination change
  useEffect(() => {
    queueMicrotask(() => {
      fetchPurchaseOrders(paginationModel.page, paginationModel.pageSize);
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [paginationModel]);

  // Reset to first page and refetch when filters change
  useEffect(() => {
    if (isFirstFilterRun.current) {
      isFirstFilterRun.current = false;
      return;
    }
    queueMicrotask(() => {
      setPaginationModel((prev) => ({ ...prev, page: 0 }));
      fetchPurchaseOrders(0, paginationModel.pageSize);
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [debouncedKeyword, statusFilter, dateFrom, dateTo]);

  return (
    <Wrapper>
      <Title>{t("page.purchaseOrders.title")}</Title>
      <Box
        sx={{
          height: "100%",
          width: "100%",
          display: "flex",
          flexDirection: "column",
          overflowY: "auto",
        }}
      >
        <Toolbar>
          <Input
            type="text"
            placeholder={t("page.purchaseOrders.searchPlaceholder")}
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
          />
          <DateInput
            type="date"
            value={dateFrom}
            onChange={(e) => setDateFrom(e.target.value)}
          />
          <span>~</span>
          <DateInput
            type="date"
            value={dateTo}
            onChange={(e) => setDateTo(e.target.value)}
          />
          <Spacer />
          <NewBtn
            type="button"
            onClick={() => navigate("/purchase-orders/new")}
          >
            {t("page.purchaseOrders.newPO")}
          </NewBtn>
        </Toolbar>
        <Toolbar>
          <Tabs>
            <Tab
              type="button"
              $active={statusFilter === ""}
              onClick={() => setStatusFilter("")}
            >
              {t("page.purchaseOrders.all")}
            </Tab>
            {statuses.map((status) => (
              <Tab
                key={status.id}
                type="button"
                $active={statusFilter === status.id}
                onClick={() => setStatusFilter(status.id)}
              >
                {status.name}
              </Tab>
            ))}
          </Tabs>
        </Toolbar>
        <Box sx={{ flex: 1, minHeight: 0, width: "100%" }}>
          <DataGrid
            rows={rows}
            columns={columns}
            loading={loading}
            pagination
            paginationMode="server"
            rowCount={rowCount}
            paginationModel={paginationModel}
            onPaginationModelChange={setPaginationModel}
            pageSizeOptions={[10, 20, 50, 100]}
            disableColumnFilter
            disableColumnSorting
          />
        </Box>
      </Box>
    </Wrapper>
  );
}
