import { Box } from "@mui/material";
import { DataGrid, type GridColDef } from "@mui/x-data-grid";
import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import styled from "styled-components";
import type { ApiIfs, PageIfs } from "../entities/app";
import type { CompanyIfs, CompanyTypeIfs, TaxStatusIfs } from "../entities";
import { CompanyCreateModal } from "../features/company";
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
  width: 260px;

  &:focus {
    outline: none;
    ${commBtnSkyBlueBoxShadow}
  }
`;

const Tabs = styled.div`
  display: flex;
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

export default function Companies() {
  const navigate = useNavigate();

  const [rows, setRows] = useState<CompanyIfs[]>([]);
  const [rowCount, setRowCount] = useState(0);
  const [loading, setLoading] = useState(false);
  const [paginationModel, setPaginationModel] = useState(
    dataGridInitialState.pagination.paginationModel,
  );
  const [keyword, setKeyword] = useState("");
  const [debouncedKeyword, setDebouncedKeyword] = useState("");
  const [typeFilter, setTypeFilter] = useState<number | "">("");
  const [companyTypes, setCompanyTypes] = useState<CompanyTypeIfs[]>([]);
  const [taxStatuses, setTaxStatuses] = useState<TaxStatusIfs[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const isFirstFilterRun = useRef(true);

  const columns: GridColDef[] = [
    {
      field: "name",
      headerName: "Company",
      flex: 1,
      minWidth: 180,
      renderCell: (params) => (
        <span
          style={{ cursor: "pointer", color: "#17c1ff" }}
          onClick={() => navigate(`/companies/${params.row.id}`)}
        >
          {params.value}
        </span>
      ),
    },
    {
      field: "companyType",
      headerName: "Type",
      width: 140,
      valueGetter: (_value, row) => row.companyType?.companyType ?? "",
    },
    {
      field: "businessPhone",
      headerName: "Phone",
      width: 160,
      valueGetter: (value) => value ?? "",
    },
    {
      field: "city",
      headerName: "City",
      width: 140,
      valueGetter: (value) => value ?? "",
    },
    {
      field: "taxStatus",
      headerName: "Tax Status",
      width: 140,
      valueGetter: (_value, row) => row.taxStatus?.status ?? "",
    },
  ];

  const fetchCompanies = (page: number, size: number) => {
    setLoading(true);
    const params: Record<string, unknown> = { page, size };
    if (debouncedKeyword.trim()) params.keyword = debouncedKeyword.trim();
    if (typeFilter !== "") params.type = typeFilter;

    privateApi
      .get("/v1/companies", { params })
      .then((res) => {
        const data: ApiIfs<PageIfs<CompanyIfs>> = res.data;
        setRows(data?.body?.content ?? []);
        setRowCount(data?.body?.page?.totalElements ?? 0);
      })
      .catch(console.error)
      .finally(() => setLoading(false));
  };

  // Load metadata (for tab/modal selects)
  useEffect(() => {
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
  }, []);

  // Debounce search keyword (400ms)
  useEffect(() => {
    const timer = setTimeout(() => setDebouncedKeyword(keyword), 400);
    return () => clearTimeout(timer);
  }, [keyword]);

  // Refetch on pagination change
  useEffect(() => {
    queueMicrotask(() => {
      fetchCompanies(paginationModel.page, paginationModel.pageSize);
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [paginationModel]);

  // Reset to first page and refetch when filters (keyword/type) change
  useEffect(() => {
    if (isFirstFilterRun.current) {
      isFirstFilterRun.current = false;
      return;
    }
    queueMicrotask(() => {
      setPaginationModel((prev) => ({ ...prev, page: 0 }));
      fetchCompanies(0, paginationModel.pageSize);
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [debouncedKeyword, typeFilter]);

  return (
    <Wrapper>
      <Title>Company Management</Title>
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
            placeholder="Search by company name"
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
          />
          <Tabs>
            <Tab
              type="button"
              $active={typeFilter === ""}
              onClick={() => setTypeFilter("")}
            >
              All
            </Tab>
            {companyTypes.map((type) => (
              <Tab
                key={type.id}
                type="button"
                $active={typeFilter === type.id}
                onClick={() => setTypeFilter(type.id)}
              >
                {type.companyType}
              </Tab>
            ))}
          </Tabs>
          <Spacer />
          <NewBtn type="button" onClick={() => setIsModalOpen(true)}>
            + New Company
          </NewBtn>
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

      {isModalOpen && (
        <CompanyCreateModal
          companyTypes={companyTypes}
          taxStatuses={taxStatuses}
          onClose={() => setIsModalOpen(false)}
          onCreated={(company) => {
            setIsModalOpen(false);
            navigate(`/companies/${company.id}`);
          }}
        />
      )}
    </Wrapper>
  );
}
