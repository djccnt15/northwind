import { useState } from "react";
import styled from "styled-components";
import type { ApiIfs } from "../../entities/app";
import type { CompanyIfs, CompanyTypeIfs, TaxStatusIfs } from "../../entities";
import { privateApi } from "../../shared/api";
import {
  commBorderRadius,
  commBtnHoverSkyBlue,
  commBtnSkyBlue,
  commBtnSkyBlueBoxShadow,
  globalTransition,
  ModalDefault,
  ModalOverlay,
} from "../../shared/ui";
import { convertEmptyStringToNull } from "../../shared/utils";
import { useKeyDown } from "../../shared/useKeyDown";

interface CompanyCreateModalProps {
  companyTypes: CompanyTypeIfs[];
  taxStatuses: TaxStatusIfs[];
  onClose: () => void;
  onCreated: (company: CompanyIfs) => void;
}

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

export default function CompanyCreateModal({
  companyTypes,
  taxStatuses,
  onClose,
  onCreated,
}: CompanyCreateModalProps) {
  const [form, setForm] = useState<CompanyFormState>(emptyForm);
  const [loading, setLoading] = useState(false);

  useKeyDown("Escape", onClose);

  // Derive default selection from the first option until the user picks one
  const selectedCompanyTypeId =
    form.companyTypeId !== "" ? form.companyTypeId : (companyTypes.at(0)?.id ?? "");
  const selectedTaxStatusId =
    form.taxStatusId !== "" ? form.taxStatusId : (taxStatuses.at(0)?.id ?? "");

  const updateField =
    (field: keyof CompanyFormState) =>
    (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
      setForm((prev) => ({ ...prev, [field]: e.target.value }));
    };

  const handleSaveClick = () => {
    if (!form.name.trim()) {
      alert("Company name is required.");
      return;
    }
    if (selectedCompanyTypeId === "" || selectedTaxStatusId === "") {
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
    body.companyTypeId = selectedCompanyTypeId;
    body.taxStatusId = selectedTaxStatusId;

    setLoading(true);
    privateApi
      .post("/v1/companies", body)
      .then((res) => {
        const data: ApiIfs<CompanyIfs> = res.data;
        if (data?.body) onCreated(data.body);
      })
      .catch((err) => {
        const data: ApiIfs<null> = err.response?.data;
        if (data?.result?.code === 1400) {
          const lines = Object.values(data?.body || {});
          alert(`Invalid input:\n${lines.join("\n")}`);
          return;
        }
        const message = data?.result?.description || "Unknown error";
        alert(`Failed to create company: ${message}`);
      })
      .finally(() => setLoading(false));
  };

  return (
    <ModalOverlay onClick={onClose}>
      <Modal onClick={(e) => e.stopPropagation()}>
        <ModalTitle>New Company</ModalTitle>
        <FieldWrapper>
          <Label>Name *</Label>
          <Input value={form.name} onChange={updateField("name")} />
        </FieldWrapper>
        <FormRow>
          <FieldWrapper>
            <Label>Company Type *</Label>
            <Select
              value={selectedCompanyTypeId}
              onChange={(e) =>
                setForm((prev) => ({
                  ...prev,
                  companyTypeId:
                    e.target.value === "" ? "" : Number(e.target.value),
                }))
              }
            >
              <option value="">Select type</option>
              {companyTypes.map((type) => (
                <option key={type.id} value={type.id}>
                  {type.companyType}
                </option>
              ))}
            </Select>
          </FieldWrapper>
          <FieldWrapper>
            <Label>Tax Status *</Label>
            <Select
              value={selectedTaxStatusId}
              onChange={(e) =>
                setForm((prev) => ({
                  ...prev,
                  taxStatusId:
                    e.target.value === "" ? "" : Number(e.target.value),
                }))
              }
            >
              <option value="">Select status</option>
              {taxStatuses.map((status) => (
                <option key={status.id} value={status.id}>
                  {status.status}
                </option>
              ))}
            </Select>
          </FieldWrapper>
        </FormRow>
        <FormRow>
          <FieldWrapper>
            <Label>Business Phone</Label>
            <Input
              value={form.businessPhone}
              onChange={updateField("businessPhone")}
            />
          </FieldWrapper>
          <FieldWrapper>
            <Label>Website</Label>
            <Input value={form.website} onChange={updateField("website")} />
          </FieldWrapper>
        </FormRow>
        <FieldWrapper>
          <Label>Address</Label>
          <Input value={form.address} onChange={updateField("address")} />
        </FieldWrapper>
        <FormRow>
          <FieldWrapper>
            <Label>City</Label>
            <Input value={form.city} onChange={updateField("city")} />
          </FieldWrapper>
          <FieldWrapper>
            <Label>Region</Label>
            <Input value={form.region} onChange={updateField("region")} />
          </FieldWrapper>
        </FormRow>
        <FormRow>
          <FieldWrapper>
            <Label>Zip Code</Label>
            <Input value={form.zipCode} onChange={updateField("zipCode")} />
          </FieldWrapper>
          <FieldWrapper>
            <Label>Country</Label>
            <Input value={form.country} onChange={updateField("country")} />
          </FieldWrapper>
        </FormRow>
        <FieldWrapper>
          <Label>Notes</Label>
          <TextArea value={form.notes} onChange={updateField("notes")} />
        </FieldWrapper>
        <BtnRow>
          <PrimaryBtn type="button" onClick={handleSaveClick} disabled={loading}>
            Create
          </PrimaryBtn>
          <SecondaryBtn type="button" onClick={onClose} disabled={loading}>
            Cancel
          </SecondaryBtn>
        </BtnRow>
      </Modal>
    </ModalOverlay>
  );
}

const Modal = styled(ModalDefault)`
  background-color: white;
  padding: 24px;
  width: 520px;
  max-width: 90vw;
  max-height: 90vh;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 14px;
`;

const ModalTitle = styled.h2`
  font-size: 20px;
  font-weight: 700;
`;

const FormRow = styled.div`
  display: flex;
  gap: 12px;

  @media (max-width: 500px) {
    flex-direction: column;
  }
`;

const FieldWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 6px;
  flex: 1;
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

const BtnRow = styled.div`
  display: flex;
  gap: 10px;
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

const SecondaryBtn = styled(BtnBase)`
  background-color: #888;
  &:hover {
    background-color: #6f6f6f;
  }
`;
