package com.djccnt15.northwind.domain.company.service;

import com.djccnt15.northwind.db.entity.CompanyEntity;
import com.djccnt15.northwind.db.entity.ContactEntity;
import com.djccnt15.northwind.db.repository.ContactRepo;
import com.djccnt15.northwind.domain.company.converter.ContactConverter;
import com.djccnt15.northwind.domain.company.model.ContactCreateReq;
import com.djccnt15.northwind.global.exception.exceptions.ApiException;
import com.djccnt15.northwind.global.message.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.djccnt15.northwind.global.code.StatusCode.BAD_REQUEST;
import static com.djccnt15.northwind.global.code.StatusCode.NOT_FOUND;
import static com.djccnt15.northwind.domain.company.validation.ContactErrorConst.EMAIL_DUPLICATE_ERR_MSG;
import static com.djccnt15.northwind.domain.company.validation.ContactErrorConst.NOT_FOUND_ERR_MSG;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepo repository;
    private final ContactConverter converter;
    private final MessageUtil messageUtil;

    public List<ContactEntity> getContacts(Long companyId) {
        return repository.findByCompanyIdOrderByLastNameAscFirstNameAsc(companyId);
    }

    public ContactEntity getContact(Long companyId, Long contactId) {
        return repository.findByIdAndCompanyId(contactId, companyId)
            .orElseThrow(() -> new ApiException(NOT_FOUND, messageUtil.getMessage(NOT_FOUND_ERR_MSG)));
    }

    public void validateContact(ContactCreateReq request) {
        if (StringUtils.hasText(request.getEmail()) && repository.existsByEmail(request.getEmail())) {
            throw new ApiException(BAD_REQUEST, messageUtil.getMessage(EMAIL_DUPLICATE_ERR_MSG));
        }
    }

    public void validateContact(Long contactId, ContactCreateReq request) {
        if (StringUtils.hasText(request.getEmail())
            && repository.existsByEmailAndIdNot(request.getEmail(), contactId)) {
            throw new ApiException(BAD_REQUEST, messageUtil.getMessage(EMAIL_DUPLICATE_ERR_MSG));
        }
    }

    public ContactEntity createContact(ContactCreateReq request, CompanyEntity company) {
        var entity = converter.toEntity(request, company);
        repository.save(entity);
        return entity;
    }

    public ContactEntity updateContact(ContactEntity entity, ContactCreateReq request) {
        entity.setFirstName(request.getFirstName());
        entity.setLastName(request.getLastName());
        entity.setEmail(request.getEmail());
        entity.setJobTitle(request.getJobTitle());
        entity.setPrimaryPhone(request.getPrimaryPhone());
        entity.setSecondaryPhone(request.getSecondaryPhone());
        entity.setNotes(request.getNotes());
        repository.save(entity);
        return entity;
    }

    public void deleteContact(ContactEntity entity) {
        repository.delete(entity);
    }
}
