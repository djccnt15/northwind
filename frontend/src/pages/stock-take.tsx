import { Box } from "@mui/material";
import {
  DataGrid,
  type GridColDef,
  type GridRenderCellParams,
} from "@mui/x-data-grid";
import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import styled from "styled-components";
import type { ApiIfs, PageIfs, StockTakeRowIfs } from "../entities";
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

const FilterBar = styled.div`
  display: flex;
  flex-wrap: wrap;
  align-items: flex-end;
  gap: 10px;
  padding: 0 20px 15px;
`;

const FieldWrapper = styled.div<{ width?: string }>`
  display: flex;
  flex-direction: column;
  width: ${(props) => props.width || "200px"};
`;

const Label = styled.label`
  padding-bottom: 6px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
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

const SearchBtn = styled.button`
  ${commBorderRadius}
  ${commBtnSkyBlue}
  ${globalTransition}
  height: 38px;
  width: 100px;
  border: none;
  color: white;
  font-size: 14px;
  cursor: pointer;

  &:hover {
    ${commBtnHoverSkyBlue}
    ${globalTransition}
  }

  &:focus {
    outline: none;
    ${commBtnSkyBlueBoxShadow}
  }
`;

const Spacer = styled.div`
  flex: 1;
`;

const SaveBtn = styled.button`
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

  &:hover:not(:disabled) {
    ${commBtnHoverSkyBlue}
    ${globalTransition}
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }

  &:focus {
    outline: none;
    ${commBtnSkyBlueBoxShadow}
  }
`;

const MessageBar = styled.div<{ $error?: boolean }>`
  padding: 0 20px 10px;
  font-size: 14px;
  font-weight: 600;
  color: ${(props) => (props.$error ? "#ff4d4f" : "#52c41a")};
`;

const todayString = () => {
  const now = new Date();
  const yyyy = now.getFullYear();
  const mm = String(now.getMonth() + 1).padStart(2, "0");
  const dd = String(now.getDate()).padStart(2, "0");
  return `${yyyy}-${mm}-${dd}`;
};

// 행의 현재 실사 입력값(편집값 우선, 없으면 draft, 없으면 전산재고)
const currentActual = (
  row: StockTakeRowIfs,
  edits: Map<number, number>,
): number => {
  const edited = edits.get(row.productId);
  if (edited !== undefined) return edited;
  if (row.quantityOnHand !== null) return row.quantityOnHand;
  return row.expectedQuantity;
};

export default function StockTake() {
  const { t } = useTranslation();
  const [rows, setRows] = useState<StockTakeRowIfs[]>([]);
  const [rowCount, setRowCount] = useState(0);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [paginationModel, setPaginationModel] = useState(
    dataGridInitialState.pagination.paginationModel,
  );
  const [keyword, setKeyword] = useState("");
  const [edits, setEdits] = useState<Map<number, number>>(new Map());
  const [message, setMessage] = useState("");
  const [isError, setIsError] = useState(false);

  const updateActual = (productId: number, value: number) => {
    setEdits((prev) => {
      const next = new Map(prev);
      next.set(productId, value);
      return next;
    });
  };

  const columns: GridColDef<StockTakeRowIfs>[] = [
    { field: "productCode", headerName: t("page.stockTake.col.productCode"), width: 130 },
    { field: "productName", headerName: t("page.stockTake.col.productName"), flex: 1, minWidth: 200 },
    {
      field: "expectedQuantity",
      headerName: t("page.stockTake.col.systemQty"),
      width: 120,
      type: "number",
      align: "right",
      headerAlign: "right",
    },
    {
      field: "actualQuantity",
      headerName: t("page.stockTake.col.actualQty"),
      width: 140,
      sortable: false,
      align: "right",
      headerAlign: "right",
      renderCell: (params: GridRenderCellParams<StockTakeRowIfs>) => (
        <Input
          type="number"
          min={0}
          value={currentActual(params.row, edits)}
          onChange={(e) => {
            const raw = e.target.value;
            const parsed = raw === "" ? 0 : Number(raw);
            updateActual(
              params.row.productId,
              Number.isNaN(parsed) || parsed < 0 ? 0 : parsed,
            );
          }}
          style={{ height: "32px", textAlign: "right" }}
        />
      ),
    },
    {
      field: "difference",
      headerName: t("page.stockTake.col.diff"),
      width: 110,
      sortable: false,
      align: "right",
      headerAlign: "right",
      renderCell: (params: GridRenderCellParams<StockTakeRowIfs>) => {
        const diff =
          currentActual(params.row, edits) - params.row.expectedQuantity;
        if (diff === 0) return <span>0</span>;
        return (
          <span style={{ color: diff > 0 ? "#52c41a" : "#ff4d4f", fontWeight: 700 }}>
            {diff > 0 ? `+${diff}` : diff}
          </span>
        );
      },
    },
  ];

  const fetchRows = (page: number, size: number) => {
    setLoading(true);
    const params: Record<string, unknown> = { page, size };
    if (keyword.trim()) params.keyword = keyword.trim();

    privateApi
      .get("/v1/stock-takes", { params })
      .then((res) => {
        const data: ApiIfs<PageIfs<StockTakeRowIfs>> = res.data;
        setRows(data?.body?.content ?? []);
        setRowCount(data?.body?.page?.totalElements ?? 0);
        // 새 페이지 데이터 로드 시 편집 버퍼 초기화
        setEdits(new Map());
      })
      .catch(console.error)
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    queueMicrotask(() => {
      fetchRows(paginationModel.page, paginationModel.pageSize);
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [paginationModel]);

  const handleSearch = () => {
    setPaginationModel((prev) => ({ ...prev, page: 0 }));
    fetchRows(0, paginationModel.pageSize);
  };

  const onKeywordKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") {
      handleSearch();
    }
  };

  // 변경된 행만 추출: 현재 입력값이 기존 draft(quantityOnHand)와 다른 행
  const changedItems = rows
    .filter((row) => {
      const edited = edits.get(row.productId);
      if (edited === undefined) return false;
      return edited !== row.quantityOnHand;
    })
    .map((row) => ({
      productId: row.productId,
      quantityOnHand: edits.get(row.productId) as number,
    }));

  const handleSaveClick = () => {
    if (changedItems.length === 0) return;
    setSaving(true);
    setMessage("");
    setIsError(false);

    const body = {
      stockTakeDate: todayString(),
      items: changedItems,
    };

    privateApi
      .post("/v1/stock-takes", body)
      .then((res) => {
        const data: ApiIfs<StockTakeRowIfs[]> = res.data;
        const saved = data?.body ?? [];
        const savedMap = new Map<number, StockTakeRowIfs>(
          saved.map((row) => [row.productId, row]),
        );
        setRows((prev) =>
          prev.map((row) => savedMap.get(row.productId) ?? row),
        );
        // 저장된 행의 편집 버퍼 제거
        setEdits((prev) => {
          const next = new Map(prev);
          saved.forEach((row) => next.delete(row.productId));
          return next;
        });
        setIsError(false);
        setMessage(t("page.stockTake.savedCount", { count: saved.length }));
      })
      .catch((err) => {
        const data: ApiIfs<Record<string, string> | null> = err.response?.data;
        setIsError(true);
        if (data?.result?.code === 1400 && data.body) {
          const fieldMsgs = Object.values(data.body).join(", ");
          setMessage(fieldMsgs || t("page.stockTake.validationError"));
        } else {
          setMessage(
            data?.result?.description ?? t("page.stockTake.saveError"),
          );
        }
      })
      .finally(() => setSaving(false));
  };

  return (
    <Wrapper>
      <Title>{t("page.stockTake.title")}</Title>
      <Box
        sx={{
          height: "100%",
          width: "100%",
          display: "flex",
          flexDirection: "column",
          overflowY: "auto",
        }}
      >
        <FilterBar>
          <FieldWrapper width="240px">
            <Label>{t("page.stockTake.keywordLabel")}</Label>
            <Input
              type="text"
              placeholder={t("page.stockTake.searchPlaceholder")}
              value={keyword}
              onChange={(e) => setKeyword(e.target.value)}
              onKeyDown={onKeywordKeyDown}
            />
          </FieldWrapper>
          <SearchBtn type="button" onClick={handleSearch}>
            {t("page.stockTake.search")}
          </SearchBtn>
          <Spacer />
          <SaveBtn
            type="button"
            onClick={handleSaveClick}
            disabled={saving || changedItems.length === 0}
          >
            {t("page.stockTake.saveBtn")}
            {changedItems.length > 0 ? ` (${changedItems.length})` : ""}
          </SaveBtn>
        </FilterBar>
        {message && <MessageBar $error={isError}>{message}</MessageBar>}
        <Box sx={{ flex: 1, minHeight: 0, width: "100%" }}>
          <DataGrid
            rows={rows}
            columns={columns}
            getRowId={(row) => row.productId}
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
