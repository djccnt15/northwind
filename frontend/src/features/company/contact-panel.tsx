import { useEffect, useState } from "react";
import styled from "styled-components";
import type { ApiIfs } from "../../entities/app";
import type { ContactIfs } from "../../entities";
import { privateApi } from "../../shared/api";
import {
  commBorderRadius,
  commBtnHoverSkyBlue,
  commBtnHoverTomatoRed,
  commBtnSkyBlue,
  commBtnSkyBlueBoxShadow,
  commBtnTomatoRed,
  globalTransition,
} from "../../shared/ui";
import { convertEmptyStringToNull } from "../../shared/utils";

interface ContactFormState {
  firstName: string;
  lastName: string;
  email: string;
  jobTitle: string;
  primaryPhone: string;
  secondaryPhone: string;
  notes: string;
}

interface ContactPanelProps {
  companyId: number;
}

const emptyForm: ContactFormState = {
  firstName: "",
  lastName: "",
  email: "",
  jobTitle: "",
  primaryPhone: "",
  secondaryPhone: "",
  notes: "",
};

const contactToForm = (contact: ContactIfs): ContactFormState => ({
  firstName: contact.firstName,
  lastName: contact.lastName,
  email: contact.email ?? "",
  jobTitle: contact.jobTitle ?? "",
  primaryPhone: contact.primaryPhone ?? "",
  secondaryPhone: contact.secondaryPhone ?? "",
  notes: contact.notes ?? "",
});

export default function ContactPanel({ companyId }: ContactPanelProps) {
  const [contacts, setContacts] = useState<ContactIfs[]>([]);
  const [loading, setLoading] = useState(false);
  // editingId: existing contact id being edited, "new" for add form, null for none
  const [editingId, setEditingId] = useState<number | "new" | null>(null);
  const [form, setForm] = useState<ContactFormState>(emptyForm);

  const fetchContacts = () => {
    setLoading(true);
    privateApi
      .get(`/v1/companies/${companyId}/contacts`)
      .then((res) => {
        const data: ApiIfs<ContactIfs[]> = res.data;
        setContacts(data?.body ?? []);
      })
      .catch(console.error)
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    queueMicrotask(fetchContacts);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [companyId]);

  const updateField =
    (field: keyof ContactFormState) =>
    (
      e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>,
    ) => {
      setForm((prev) => ({ ...prev, [field]: e.target.value }));
    };

  const handleAddClick = () => {
    setForm(emptyForm);
    setEditingId("new");
  };

  const handleEditClick = (contact: ContactIfs) => {
    setForm(contactToForm(contact));
    setEditingId(contact.id);
  };

  const handleCancelClick = () => {
    setEditingId(null);
    setForm(emptyForm);
  };

  const handleSaveClick = () => {
    if (!form.firstName.trim() || !form.lastName.trim()) {
      alert("First name and last name are required.");
      return;
    }
    const body = convertEmptyStringToNull({
      firstName: form.firstName.trim(),
      lastName: form.lastName.trim(),
      email: form.email,
      jobTitle: form.jobTitle,
      primaryPhone: form.primaryPhone,
      secondaryPhone: form.secondaryPhone,
      notes: form.notes,
    });

    const request =
      editingId === "new"
        ? privateApi.post(`/v1/companies/${companyId}/contacts`, body)
        : privateApi.put(
            `/v1/companies/${companyId}/contacts/${editingId}`,
            body,
          );

    setLoading(true);
    request
      .then(() => {
        handleCancelClick();
        fetchContacts();
      })
      .catch((err) => {
        const data: ApiIfs<null> = err.response?.data;
        if (data?.result?.code === 1400) {
          const lines = Object.values(data?.body || {});
          alert(`Invalid input:\n${lines.join("\n")}`);
          return;
        }
        const message = data?.result?.description || "Unknown error";
        alert(`Failed to save contact: ${message}`);
        setLoading(false);
      });
  };

  const handleDeleteClick = (contact: ContactIfs) => {
    if (
      !window.confirm(
        `Delete contact "${contact.firstName} ${contact.lastName}"?`,
      )
    ) {
      return;
    }
    setLoading(true);
    privateApi
      .delete(`/v1/companies/${companyId}/contacts/${contact.id}`)
      .then(fetchContacts)
      .catch((err) => {
        const data: ApiIfs<null> = err.response?.data;
        const message = data?.result?.description || "Unknown error";
        alert(`Failed to delete contact: ${message}`);
        setLoading(false);
      });
  };

  const renderForm = () => (
    <FormBox>
      <FormRow>
        <FieldWrapper>
          <Label>First Name *</Label>
          <Input value={form.firstName} onChange={updateField("firstName")} />
        </FieldWrapper>
        <FieldWrapper>
          <Label>Last Name *</Label>
          <Input value={form.lastName} onChange={updateField("lastName")} />
        </FieldWrapper>
      </FormRow>
      <FormRow>
        <FieldWrapper>
          <Label>Job Title</Label>
          <Input value={form.jobTitle} onChange={updateField("jobTitle")} />
        </FieldWrapper>
        <FieldWrapper>
          <Label>Email</Label>
          <Input value={form.email} onChange={updateField("email")} />
        </FieldWrapper>
      </FormRow>
      <FormRow>
        <FieldWrapper>
          <Label>Primary Phone</Label>
          <Input
            value={form.primaryPhone}
            onChange={updateField("primaryPhone")}
          />
        </FieldWrapper>
        <FieldWrapper>
          <Label>Secondary Phone</Label>
          <Input
            value={form.secondaryPhone}
            onChange={updateField("secondaryPhone")}
          />
        </FieldWrapper>
      </FormRow>
      <FieldWrapper>
        <Label>Notes</Label>
        <TextArea value={form.notes} onChange={updateField("notes")} />
      </FieldWrapper>
      <BtnRow>
        <PrimaryBtn type="button" onClick={handleSaveClick} disabled={loading}>
          Save
        </PrimaryBtn>
        <SecondaryBtn
          type="button"
          onClick={handleCancelClick}
          disabled={loading}
        >
          Cancel
        </SecondaryBtn>
      </BtnRow>
    </FormBox>
  );

  return (
    <Panel>
      <PanelHeader>
        <PanelTitle>Contacts</PanelTitle>
        {editingId === null && (
          <AddBtn type="button" onClick={handleAddClick}>
            + Add Contact
          </AddBtn>
        )}
      </PanelHeader>

      {editingId === "new" && renderForm()}

      {contacts.length === 0 && editingId !== "new" ? (
        <EmptyText>{loading ? "Loading..." : "No contacts yet."}</EmptyText>
      ) : (
        <ContactList>
          {contacts.map((contact) =>
            editingId === contact.id ? (
              <li key={contact.id}>{renderForm()}</li>
            ) : (
              <ContactItem key={contact.id}>
                <ContactInfo>
                  <ContactName>
                    {contact.firstName} {contact.lastName}
                  </ContactName>
                  {contact.jobTitle && (
                    <ContactMeta>{contact.jobTitle}</ContactMeta>
                  )}
                  {contact.email && (
                    <ContactMeta>
                      <a href={`mailto:${contact.email}`}>{contact.email}</a>
                    </ContactMeta>
                  )}
                  {contact.primaryPhone && (
                    <ContactMeta>{contact.primaryPhone}</ContactMeta>
                  )}
                </ContactInfo>
                <ContactActions>
                  <EditBtn
                    type="button"
                    onClick={() => handleEditClick(contact)}
                  >
                    Edit
                  </EditBtn>
                  <DeleteBtn
                    type="button"
                    onClick={() => handleDeleteClick(contact)}
                  >
                    Delete
                  </DeleteBtn>
                </ContactActions>
              </ContactItem>
            ),
          )}
        </ContactList>
      )}
    </Panel>
  );
}

const Panel = styled.div`
  display: flex;
  flex-direction: column;
  gap: 12px;
`;

const PanelHeader = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #eee;
  padding-bottom: 8px;
`;

const PanelTitle = styled.h3`
  font-size: 16px;
  font-weight: 700;
  text-transform: uppercase;
`;

const EmptyText = styled.span`
  font-size: 14px;
  color: #888;
`;

const ContactList = styled.ul`
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 10px;
`;

const ContactItem = styled.li`
  ${commBorderRadius}
  border: 1px solid #e0e0e0;
  padding: 12px 14px;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 10px;
`;

const ContactInfo = styled.div`
  display: flex;
  flex-direction: column;
  gap: 2px;
`;

const ContactName = styled.span`
  font-size: 15px;
  font-weight: 600;
`;

const ContactMeta = styled.span`
  font-size: 13px;
  color: #666;

  a {
    ${globalTransition}
    color: #17c1ff;
    text-decoration: none;

    &:hover {
      ${commBtnHoverSkyBlue}
    }
  }
`;

const ContactActions = styled.div`
  display: flex;
  gap: 6px;
  flex-shrink: 0;
`;

const SmallBtn = styled.button`
  ${commBorderRadius}
  ${globalTransition}
  height: 30px;
  padding: 0 12px;
  border: none;
  color: white;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
`;

const EditBtn = styled(SmallBtn)`
  ${commBtnSkyBlue}
  &:hover {
    ${commBtnHoverSkyBlue}
  }
`;

const DeleteBtn = styled(SmallBtn)`
  ${commBtnTomatoRed}
  &:hover {
    ${commBtnHoverTomatoRed}
  }
`;

const AddBtn = styled(SmallBtn)`
  ${commBtnSkyBlue}
  height: 32px;
  &:hover {
    ${commBtnHoverSkyBlue}
  }
`;

const FormBox = styled.div`
  ${commBorderRadius}
  border: 1px solid #d0d0d0;
  background-color: #fafafa;
  padding: 14px;
  display: flex;
  flex-direction: column;
  gap: 12px;
`;

const FormRow = styled.div`
  display: flex;
  gap: 12px;

  @media (max-width: 600px) {
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
  gap: 8px;
`;

const PrimaryBtn = styled(SmallBtn)`
  ${commBtnSkyBlue}
  height: 36px;
  min-width: 80px;
  &:hover {
    ${commBtnHoverSkyBlue}
  }
`;

const SecondaryBtn = styled(SmallBtn)`
  background-color: #888;
  height: 36px;
  min-width: 80px;
  &:hover {
    background-color: #6f6f6f;
  }
`;
