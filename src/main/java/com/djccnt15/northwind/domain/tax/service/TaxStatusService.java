package com.djccnt15.northwind.domain.tax.service;

import com.djccnt15.northwind.db.entity.TaxStatusEntity;
import com.djccnt15.northwind.db.repository.TaxStatusRepo;
import com.djccnt15.northwind.global.exception.exceptions.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.djccnt15.northwind.global.code.StatusCode.NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaxStatusService {

    private final TaxStatusRepo repository;

    public List<TaxStatusEntity> getTaxStatuses() {
        return repository.findAll(Sort.by("status"));
    }

    public TaxStatusEntity getTaxStatus(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ApiException(NOT_FOUND, "Tax status not found"));
    }
}
