import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import styled from "styled-components";
import type { ApiIfs } from "../entities/app";
import type { ProductCategoryIfs, ProductIfs } from "../entities";
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

interface ProductFormState {
  code: string;
  name: string;
  description: string;
  standardUnitCost: string;
  unitPrice: string;
  reorderLevel: string;
  targetLevel: string;
  quantityPerUnit: string;
  minimumReorderQuantity: string;
  discontinued: boolean;
  categoryId: number | "";
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
  gap: 16px;
  flex-wrap: wrap;
`;

const HeaderTitle = styled.h2`
  font-size: 22px;
  font-weight: 700;
`;

const Badge = styled.span`
  ${commBtnTomatoRed}
  ${commBorderRadius}
  color: white;
  font-size: 12px;
  font-weight: 600;
  padding: 4px 10px;
`;

const Grid = styled.div`
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;

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

const Checkbox = styled.input`
  width: 18px;
  height: 18px;
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

const emptyForm: ProductFormState = {
  code: "",
  name: "",
  description: "",
  standardUnitCost: "",
  unitPrice: "",
  reorderLevel: "",
  targetLevel: "",
  quantityPerUnit: "",
  minimumReorderQuantity: "",
  discontinued: false,
  categoryId: "",
};

const productToForm = (product: ProductIfs): ProductFormState => ({
  code: product.code,
  name: product.name,
  description: product.description ?? "",
  standardUnitCost: String(product.standardUnitCost),
  unitPrice: String(product.unitPrice),
  reorderLevel: String(product.reorderLevel),
  targetLevel: String(product.targetLevel),
  quantityPerUnit: String(product.quantityPerUnit),
  minimumReorderQuantity: String(product.minimumReorderQuantity),
  discontinued: product.discontinued,
  categoryId: product.category?.id ?? "",
});

export default function ProductDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { t } = useTranslation();

  const isCreateMode = !id || id === "new";

  const [product, setProduct] = useState<ProductIfs | null>(null);
  const [isEditing, setIsEditing] = useState(isCreateMode);
  const [form, setForm] = useState<ProductFormState>(emptyForm);
  const [categories, setCategories] = useState<ProductCategoryIfs[]>([]);
  const [loading, setLoading] = useState(false);

  const fetchProduct = () => {
    if (isCreateMode) return;
    setLoading(true);
    privateApi
      .get(`/v1/products/${id}`)
      .then((res) => {
        const data: ApiIfs<ProductIfs> = res.data;
        setProduct(data?.body ?? null);
        if (data?.body) setForm(productToForm(data.body));
      })
      .catch((err) => {
        console.error("Failed to fetch product:", err);
        alert(t("page.productDetail.alerts.fetchFailed"));
      })
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
    queueMicrotask(() => {
      if (isCreateMode) {
        setProduct(null);
        setForm(emptyForm);
        setIsEditing(true);
        fetchCategories();
        return;
      }
      fetchProduct();
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  const updateField =
    (field: keyof ProductFormState) =>
    (
      e: React.ChangeEvent<
        HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement
      >,
    ) => {
      setForm((prev) => ({ ...prev, [field]: e.target.value }));
    };

  const handleEditClick = () => {
    if (product) setForm(productToForm(product));
    fetchCategories();
    setIsEditing(true);
  };

  const handleCancelClick = () => {
    if (isCreateMode) {
      navigate("/products");
      return;
    }
    if (product) setForm(productToForm(product));
    setIsEditing(false);
  };

  const handleSaveClick = () => {
    if (!isCreateMode && !product) return;
    if (form.categoryId === "") {
      alert(t("page.productDetail.alerts.pleaseSelectCategory"));
      return;
    }
    setLoading(true);

    const body = {
      code: form.code,
      name: form.name,
      description: form.description,
      standardUnitCost: Number(form.standardUnitCost),
      unitPrice: Number(form.unitPrice),
      reorderLevel: Number(form.reorderLevel),
      targetLevel: Number(form.targetLevel),
      quantityPerUnit: Number(form.quantityPerUnit),
      minimumReorderQuantity: Number(form.minimumReorderQuantity),
      discontinued: form.discontinued,
      categoryId: form.categoryId,
    };

    const request = isCreateMode
      ? privateApi.post("/v1/products", body)
      : privateApi.put(`/v1/products/${product?.id}`, body);

    request
      .then((res) => {
        const data: ApiIfs<ProductIfs> = res.data;
        if (isCreateMode) {
          alert(t("page.productDetail.alerts.createSuccess"));
          navigate("/products");
          return;
        }
        setProduct(data?.body ?? null);
        if (data?.body) setForm(productToForm(data.body));
        setIsEditing(false);
        alert(t("page.productDetail.alerts.updateSuccess"));
      })
      .catch((err) => {
        const data: ApiIfs<null> = err.response?.data;
        if (data?.result?.code === 1400) {
          const lines = Object.values(data?.body || {});
          alert(t("page.productDetail.alerts.invalidInput", { message: lines.join("\n") }));
          return;
        }
        const message = data?.result?.description ?? "";
        alert(
          isCreateMode
            ? t("page.productDetail.alerts.createFailed", { message })
            : t("page.productDetail.alerts.updateFailed", { message }),
        );
      })
      .finally(() => setLoading(false));
  };

  const handleDiscontinueClick = () => {
    if (!product) return;
    if (!window.confirm(t("page.productDetail.alerts.discontinueConfirm"))) {
      return;
    }
    setLoading(true);
    privateApi
      .delete(`/v1/products/${product.id}`)
      .then(() => {
        alert(t("page.productDetail.alerts.discontinueSuccess"));
        fetchProduct();
      })
      .catch((err) => {
        const data: ApiIfs<null> = err.response?.data;
        const message = data?.result?.description ?? "";
        alert(t("page.productDetail.alerts.discontinueFailed", { message }));
      })
      .finally(() => setLoading(false));
  };

  if (!isCreateMode && !product) {
    return (
      <Wrapper>
        <Title>{t("page.productDetail.title")}</Title>
        <Content>
          <BackBtn onClick={() => navigate("/products")}>
            {t("page.productDetail.back")}
          </BackBtn>
          <ReadValue>
            {loading
              ? t("page.productDetail.loading")
              : t("page.productDetail.notFound")}
          </ReadValue>
        </Content>
      </Wrapper>
    );
  }

  return (
    <Wrapper>
      <Title>
        {isCreateMode
          ? t("page.productDetail.newProductTitle")
          : t("page.productDetail.title")}
      </Title>
      <Content>
        <Header>
          <BackBtn onClick={() => navigate("/products")}>
            {t("page.productDetail.back")}
          </BackBtn>
          <HeaderTitle>
            {isCreateMode
              ? t("page.productDetail.newProductTitle")
              : product?.name}
          </HeaderTitle>
          {product?.discontinued && (
            <Badge>{t("page.productDetail.discontinuedBadge")}</Badge>
          )}
        </Header>

        <Grid>
          <Card>
            <CardTitle>{t("page.productDetail.basicInfo")}</CardTitle>
            <FieldRow>
              <Label>{t("page.productDetail.code")}</Label>
              {isEditing ? (
                <Input value={form.code} onChange={updateField("code")} />
              ) : (
                <ReadValue>{product?.code}</ReadValue>
              )}
            </FieldRow>
            <FieldRow>
              <Label>{t("page.productDetail.name")}</Label>
              {isEditing ? (
                <Input value={form.name} onChange={updateField("name")} />
              ) : (
                <ReadValue>{product?.name}</ReadValue>
              )}
            </FieldRow>
            <FieldRow>
              <Label>{t("page.productDetail.category")}</Label>
              {isEditing ? (
                <Select
                  value={form.categoryId}
                  onChange={(e) =>
                    setForm((prev) => ({
                      ...prev,
                      categoryId:
                        e.target.value === "" ? "" : Number(e.target.value),
                    }))
                  }
                >
                  <option value="">
                    {t("page.productDetail.selectCategory")}
                  </option>
                  {categories.map((category) => (
                    <option key={category.id} value={category.id}>
                      {category.name}
                    </option>
                  ))}
                </Select>
              ) : (
                <ReadValue>{product?.category?.name ?? ""}</ReadValue>
              )}
            </FieldRow>
            <FieldRow>
              <Label>{t("page.productDetail.standardUnitCost")}</Label>
              {isEditing ? (
                <Input
                  type="number"
                  value={form.standardUnitCost}
                  onChange={updateField("standardUnitCost")}
                />
              ) : (
                <ReadValue>
                  ${Number(product?.standardUnitCost).toFixed(2)}
                </ReadValue>
              )}
            </FieldRow>
            <FieldRow>
              <Label>{t("page.productDetail.unitPrice")}</Label>
              {isEditing ? (
                <Input
                  type="number"
                  value={form.unitPrice}
                  onChange={updateField("unitPrice")}
                />
              ) : (
                <ReadValue>${Number(product?.unitPrice).toFixed(2)}</ReadValue>
              )}
            </FieldRow>
            <FieldRow>
              <Label>{t("page.productDetail.description")}</Label>
              {isEditing ? (
                <TextArea
                  value={form.description}
                  onChange={updateField("description")}
                />
              ) : (
                <ReadValue>{product?.description ?? ""}</ReadValue>
              )}
            </FieldRow>
            {isEditing && (
              <FieldRow>
                <Label>{t("page.productDetail.discontinuedLabel")}</Label>
                <Checkbox
                  type="checkbox"
                  checked={form.discontinued}
                  onChange={(e) =>
                    setForm((prev) => ({
                      ...prev,
                      discontinued: e.target.checked,
                    }))
                  }
                />
              </FieldRow>
            )}
          </Card>

          <Card>
            <CardTitle>{t("page.productDetail.stockInfo")}</CardTitle>
            <FieldRow>
              <Label>{t("page.productDetail.reorderLevel")}</Label>
              {isEditing ? (
                <Input
                  type="number"
                  value={form.reorderLevel}
                  onChange={updateField("reorderLevel")}
                />
              ) : (
                <ReadValue>{product?.reorderLevel}</ReadValue>
              )}
            </FieldRow>
            <FieldRow>
              <Label>{t("page.productDetail.targetLevel")}</Label>
              {isEditing ? (
                <Input
                  type="number"
                  value={form.targetLevel}
                  onChange={updateField("targetLevel")}
                />
              ) : (
                <ReadValue>{product?.targetLevel}</ReadValue>
              )}
            </FieldRow>
            <FieldRow>
              <Label>{t("page.productDetail.minimumReorderQty")}</Label>
              {isEditing ? (
                <Input
                  type="number"
                  value={form.minimumReorderQuantity}
                  onChange={updateField("minimumReorderQuantity")}
                />
              ) : (
                <ReadValue>{product?.minimumReorderQuantity}</ReadValue>
              )}
            </FieldRow>
            <FieldRow>
              <Label>{t("page.productDetail.quantityPerUnit")}</Label>
              {isEditing ? (
                <Input
                  type="number"
                  value={form.quantityPerUnit}
                  onChange={updateField("quantityPerUnit")}
                />
              ) : (
                <ReadValue>{product?.quantityPerUnit}</ReadValue>
              )}
            </FieldRow>
          </Card>
        </Grid>

        <ActionBar>
          {isEditing ? (
            <>
              <PrimaryBtn
                type="button"
                onClick={handleSaveClick}
                disabled={loading}
              >
                {isCreateMode
                  ? t("page.productDetail.create")
                  : t("page.productDetail.save")}
              </PrimaryBtn>
              <SecondaryBtn
                type="button"
                onClick={handleCancelClick}
                disabled={loading}
              >
                {t("page.productDetail.cancel")}
              </SecondaryBtn>
            </>
          ) : (
            <>
              <PrimaryBtn type="button" onClick={handleEditClick}>
                {t("page.productDetail.edit")}
              </PrimaryBtn>
              <DangerBtn
                type="button"
                onClick={handleDiscontinueClick}
                disabled={loading || product?.discontinued}
              >
                {t("page.productDetail.discontinue")}
              </DangerBtn>
            </>
          )}
        </ActionBar>
      </Content>
    </Wrapper>
  );
}
