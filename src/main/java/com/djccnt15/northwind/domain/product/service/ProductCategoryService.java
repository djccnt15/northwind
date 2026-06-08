package com.djccnt15.northwind.domain.product.service;

import com.djccnt15.northwind.db.entity.ProductCategoryEntity;
import com.djccnt15.northwind.db.repository.ProductCategoryRepo;
import com.djccnt15.northwind.domain.product.converter.ProductCategoryConverter;
import com.djccnt15.northwind.domain.product.model.ProductCategoryCreateReq;
import com.djccnt15.northwind.global.exception.exceptions.ApiException;
import com.djccnt15.northwind.global.message.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.djccnt15.northwind.global.code.StatusCode.BAD_REQUEST;
import static com.djccnt15.northwind.global.code.StatusCode.NOT_FOUND;
import static com.djccnt15.northwind.domain.product.validation.ProductCategoryErrorConst.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCategoryService {

    private final ProductCategoryRepo repository;
    private final ProductCategoryConverter converter;
    private final MessageUtil messageUtil;

    public ProductCategoryEntity getCategory(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ApiException(NOT_FOUND, messageUtil.getMessage(NOT_FOUND_ERR_MSG)));
    }

    public void validateCategory(ProductCategoryCreateReq request) {
        if (repository.existsByName(request.getName())) {
            throw new ApiException(BAD_REQUEST, messageUtil.getMessage(NAME_DUPLICATE_ERR_MSG));
        }
        if (repository.existsByCode(request.getCode())) {
            throw new ApiException(BAD_REQUEST, messageUtil.getMessage(CODE_DUPLICATE_ERR_MSG));
        }
    }

    public void validateCategory(Long id, ProductCategoryCreateReq request) {
        if (repository.existsByNameAndIdNot(request.getName(), id)) {
            throw new ApiException(BAD_REQUEST, messageUtil.getMessage(NAME_DUPLICATE_ERR_MSG));
        }
        if (repository.existsByCodeAndIdNot(request.getCode(), id)) {
            throw new ApiException(BAD_REQUEST, messageUtil.getMessage(CODE_DUPLICATE_ERR_MSG));
        }
    }

    public void validateCategoryDeletion(ProductCategoryEntity entity) {
        if (!entity.getProductEntitySet().isEmpty()) {
            throw new ApiException(BAD_REQUEST, messageUtil.getMessage(HAS_PRODUCTS_ERR_MSG));
        }
    }

    public ProductCategoryEntity createCategory(ProductCategoryCreateReq request) {
        var entity = converter.toEntity(request);
        repository.save(entity);
        return entity;
    }

    public Page<ProductCategoryEntity> getCategories(String kw, Pageable pageable) {
        return repository.findByNameLike(kw, pageable);
    }

    public List<ProductCategoryEntity> getCategories() {
        return repository.findAll(Sort.by("name"));
    }

    public ProductCategoryEntity updateCategory(ProductCategoryEntity entity, ProductCategoryCreateReq request) {
        entity.setName(request.getName());
        entity.setCode(request.getCode());
        entity.setDescription(request.getDescription());
        repository.save(entity);
        return entity;
    }

    public void deleteCategory(ProductCategoryEntity entity) {
        repository.delete(entity);
    }
}
