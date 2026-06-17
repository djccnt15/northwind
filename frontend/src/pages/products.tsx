import { Box } from "@mui/material";
import { DataGrid, type GridColDef } from "@mui/x-data-grid";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import styled from "styled-components";
import type { ApiIfs, PageIfs } from "../entities/app";
import type { ProductCategoryIfs, ProductIfs } from "../entities";
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

const NewProductBtn = styled.button`
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

  &:focus {
    outline: none;
    ${commBtnSkyBlueBoxShadow}
  }
`;

export default function Products() {
  const { t } = useTranslation();
  const navigate = useNavigate();

  const [rows, setRows] = useState<ProductIfs[]>([]);
  const [rowCount, setRowCount] = useState(0);
  const [loading, setLoading] = useState(false);
  const [paginationModel, setPaginationModel] = useState(
    dataGridInitialState.pagination.paginationModel,
  );
  const [keyword, setKeyword] = useState("");
  const [categoryId, setCategoryId] = useState<number | "">("");
  const [discontinued, setDiscontinued] = useState<boolean | "">("");
  const [categories, setCategories] = useState<ProductCategoryIfs[]>([]);

  const columns: GridColDef[] = [
    { field: "code", headerName: t("page.products.col.code"), width: 120 },
    {
      field: "name",
      headerName: t("page.products.col.name"),
      flex: 1,
      renderCell: (params) => (
        <span
          style={{ cursor: "pointer", color: "#17c1ff" }}
          onClick={() => navigate(`/products/${params.row.id}`)}
        >
          {params.value}
        </span>
      ),
    },
    {
      field: "categoryName",
      headerName: t("page.products.col.category"),
      width: 150,
      valueGetter: (_value, row) => row.category?.name ?? "",
    },
    {
      field: "unitPrice",
      headerName: t("page.products.col.unitPrice"),
      width: 120,
      valueFormatter: (value) => `$${Number(value).toFixed(2)}`,
    },
    {
      field: "discontinued",
      headerName: t("page.products.col.status"),
      width: 120,
      renderCell: (params) =>
        params.value ? (
          <span style={{ color: "#ff4d4f" }}>
            {t("page.products.discontinued")}
          </span>
        ) : (
          <span style={{ color: "#52c41a" }}>{t("page.products.active")}</span>
        ),
    },
  ];

  const fetchProducts = (page: number, size: number) => {
    setLoading(true);
    const params: Record<string, unknown> = { page, size };
    if (keyword.trim()) params.keyword = keyword.trim();
    if (categoryId !== "") params.categoryId = categoryId;
    if (discontinued !== "") params.discontinued = discontinued;

    privateApi
      .get("/v1/products", { params })
      .then((res) => {
        const data: ApiIfs<PageIfs<ProductIfs>> = res.data;
        setRows(data?.body?.content ?? []);
        setRowCount(data?.body?.page?.totalElements ?? 0);
      })
      .catch(console.error)
      .finally(() => setLoading(false));
  };

  const fetchCategories = () => {
    privateApi
      .get("/v1/categories/all")
      .then((res) => {
        const data: ApiIfs<ProductCategoryIfs[]> = res.data;
        setCategories(data?.body ?? []);
      })
      .catch(console.error);
  };

  useEffect(() => {
    fetchCategories();
  }, []);

  useEffect(() => {
    queueMicrotask(() => {
      fetchProducts(paginationModel.page, paginationModel.pageSize);
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [paginationModel]);

  const handleSearch = () => {
    setPaginationModel((prev) => ({ ...prev, page: 0 }));
    fetchProducts(0, paginationModel.pageSize);
  };

  const onKeywordKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") {
      handleSearch();
    }
  };

  return (
    <Wrapper>
      <Title>{t("page.products.title")}</Title>
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
            <Label>{t("page.products.keyword")}</Label>
            <Input
              type="text"
              placeholder={t("page.products.searchPlaceholder")}
              value={keyword}
              onChange={(e) => setKeyword(e.target.value)}
              onKeyDown={onKeywordKeyDown}
            />
          </FieldWrapper>
          <FieldWrapper>
            <Label>{t("page.products.category")}</Label>
            <Select
              value={categoryId}
              onChange={(e) =>
                setCategoryId(
                  e.target.value === "" ? "" : Number(e.target.value),
                )
              }
            >
              <option value="">{t("page.products.all")}</option>
              {categories.map((category) => (
                <option key={category.id} value={category.id}>
                  {category.name}
                </option>
              ))}
            </Select>
          </FieldWrapper>
          <FieldWrapper width="150px">
            <Label>{t("page.products.status")}</Label>
            <Select
              value={discontinued === "" ? "" : String(discontinued)}
              onChange={(e) =>
                setDiscontinued(
                  e.target.value === "" ? "" : e.target.value === "true",
                )
              }
            >
              <option value="">{t("page.products.all")}</option>
              <option value="false">{t("page.products.active")}</option>
              <option value="true">{t("page.products.discontinued")}</option>
            </Select>
          </FieldWrapper>
          <SearchBtn type="button" onClick={handleSearch}>
            {t("page.products.search")}
          </SearchBtn>
          <Spacer />
          <NewProductBtn type="button" onClick={() => navigate("/products/new")}>
            {t("page.products.newProduct")}
          </NewProductBtn>
        </FilterBar>
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
