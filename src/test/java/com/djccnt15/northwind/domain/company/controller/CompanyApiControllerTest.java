package com.djccnt15.northwind.domain.company.controller;

import com.djccnt15.northwind.domain.company.business.CompanyBusiness;
import com.djccnt15.northwind.domain.company.model.CompanyCreateReq;
import com.djccnt15.northwind.domain.company.model.CompanyRes;
import com.djccnt15.northwind.domain.company.model.CompanyTypeRes;
import com.djccnt15.northwind.domain.company.model.ContactCreateReq;
import com.djccnt15.northwind.domain.company.model.ContactRes;
import com.djccnt15.northwind.domain.company.model.OrderSummaryRes;
import com.djccnt15.northwind.domain.company.model.PurchaseOrderSummaryRes;
import com.djccnt15.northwind.domain.tax.model.TaxStatusRes;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.djccnt15.northwind.constants.ApiTestConst.API_CODE_PATH;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(CompanyApiController.class)
class CompanyApiControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private CompanyBusiness business;

    private CompanyCreateReq sampleCompanyRequest() {
        return new CompanyCreateReq(
            "Acme Corp",
            "555-1234",
            "https://acme.example.com",
            "Sample notes",
            "1 Market St",
            "Seattle",
            "WA",
            "98101",
            "USA",
            1L,
            1L
        );
    }

    private ContactCreateReq sampleContactRequest() {
        return new ContactCreateReq(
            "John",
            "Doe",
            "john.doe@example.com",
            "Manager",
            "555-1111",
            "555-2222",
            "Sample contact notes"
        );
    }

    @Test
    void unauthorized() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/companies"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "COMPANY")
    void getCompanyTypes() throws Exception {
        // given
        when(business.getCompanyTypes())
            .thenReturn(List.of(CompanyTypeRes.builder().id(1L).companyType("Customer").build()));

        // when & then
        mockMvc.perform(get("/api/v1/company-types"))
            .andExpect(status().isOk())
            .andExpect(jsonPath(API_CODE_PATH).value(200));
    }

    @Test
    @WithMockUser(authorities = "COMPANY")
    void getTaxStatuses() throws Exception {
        // given
        when(business.getTaxStatuses())
            .thenReturn(List.of(TaxStatusRes.builder().id(1L).status("Taxable").build()));

        // when & then
        mockMvc.perform(get("/api/v1/tax-statuses"))
            .andExpect(status().isOk())
            .andExpect(jsonPath(API_CODE_PATH).value(200));
    }

    @Test
    @WithMockUser(authorities = "COMPANY")
    void getCompanies() throws Exception {
        // given
        when(business.getCompanies(anyInt(), anyInt(), any(), any()))
            .thenReturn(new PageImpl<>(List.of(
                CompanyRes.builder().id(1L).name("Acme Corp").build()
            )));

        // when & then
        mockMvc.perform(get("/api/v1/companies"))
            .andExpect(status().isOk())
            .andExpect(jsonPath(API_CODE_PATH).value(200));
    }

    @Test
    @WithMockUser(authorities = "COMPANY")
    void getCompany() throws Exception {
        // given
        when(business.getCompany(any()))
            .thenReturn(CompanyRes.builder().id(1L).name("Acme Corp").build());

        // when & then
        mockMvc.perform(get("/api/v1/companies/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath(API_CODE_PATH).value(200));
    }

    @Test
    @WithMockUser(authorities = "COMPANY")
    void createCompany() throws Exception {
        // given
        when(business.createCompany(any()))
            .thenReturn(CompanyRes.builder().id(1L).name("Acme Corp").build());

        // when & then
        mockMvc.perform(post("/api/v1/companies")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleCompanyRequest())))
            .andExpect(status().isOk())
            .andExpect(jsonPath(API_CODE_PATH).value(201));
    }

    @Test
    @WithMockUser(authorities = "COMPANY")
    void createCompanyValidationFail() throws Exception {
        // given
        var request = sampleCompanyRequest();
        request.setName("");
        request.setCompanyTypeId(null);

        // when & then
        mockMvc.perform(post("/api/v1/companies")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(API_CODE_PATH).value(1400));
    }

    @Test
    @WithMockUser(authorities = "COMPANY")
    void updateCompany() throws Exception {
        // given
        when(business.updateCompany(any(), any()))
            .thenReturn(CompanyRes.builder().id(1L).name("Acme Corp").build());

        // when & then
        mockMvc.perform(put("/api/v1/companies/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleCompanyRequest())))
            .andExpect(status().isOk())
            .andExpect(jsonPath(API_CODE_PATH).value(200));
    }

    @Test
    @WithMockUser(authorities = "COMPANY")
    void deleteCompany() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/v1/companies/1")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath(API_CODE_PATH).value(200));
        verify(business).deleteCompany(1L);
    }

    @Test
    @WithMockUser(authorities = "COMPANY")
    void getContacts() throws Exception {
        // given
        when(business.getContacts(any()))
            .thenReturn(List.of(
                ContactRes.builder().id(1L).firstName("John").lastName("Doe").companyId(1L).build()
            ));

        // when & then
        mockMvc.perform(get("/api/v1/companies/1/contacts"))
            .andExpect(status().isOk())
            .andExpect(jsonPath(API_CODE_PATH).value(200));
    }

    @Test
    @WithMockUser(authorities = "COMPANY")
    void createContact() throws Exception {
        // given
        when(business.createContact(any(), any()))
            .thenReturn(ContactRes.builder().id(1L).firstName("John").lastName("Doe").companyId(1L).build());

        // when & then
        mockMvc.perform(post("/api/v1/companies/1/contacts")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleContactRequest())))
            .andExpect(status().isOk())
            .andExpect(jsonPath(API_CODE_PATH).value(201));
    }

    @Test
    @WithMockUser(authorities = "COMPANY")
    void createContactValidationFail() throws Exception {
        // given
        var request = sampleContactRequest();
        request.setFirstName("");
        request.setLastName("");
        request.setEmail("not-an-email");

        // when & then
        mockMvc.perform(post("/api/v1/companies/1/contacts")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(API_CODE_PATH).value(1400));
    }

    @Test
    @WithMockUser(authorities = "COMPANY")
    void updateContact() throws Exception {
        // given
        when(business.updateContact(any(), any(), any()))
            .thenReturn(ContactRes.builder().id(1L).firstName("John").lastName("Doe").companyId(1L).build());

        // when & then
        mockMvc.perform(put("/api/v1/companies/1/contacts/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleContactRequest())))
            .andExpect(status().isOk())
            .andExpect(jsonPath(API_CODE_PATH).value(200));
    }

    @Test
    @WithMockUser(authorities = "COMPANY")
    void deleteContact() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/v1/companies/1/contacts/1")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath(API_CODE_PATH).value(200));
        verify(business).deleteContact(1L, 1L);
    }

    @Test
    @WithMockUser(authorities = "COMPANY")
    void getOrders() throws Exception {
        // given
        when(business.getOrders(any()))
            .thenReturn(List.of(OrderSummaryRes.builder().id(1L).status("New").build()));

        // when & then
        mockMvc.perform(get("/api/v1/companies/1/orders"))
            .andExpect(status().isOk())
            .andExpect(jsonPath(API_CODE_PATH).value(200));
    }

    @Test
    @WithMockUser(authorities = "COMPANY")
    void getPurchaseOrders() throws Exception {
        // given
        when(business.getPurchaseOrders(any()))
            .thenReturn(List.of(PurchaseOrderSummaryRes.builder().id(1L).status("New").build()));

        // when & then
        mockMvc.perform(get("/api/v1/companies/1/purchase-orders"))
            .andExpect(status().isOk())
            .andExpect(jsonPath(API_CODE_PATH).value(200));
    }
}
