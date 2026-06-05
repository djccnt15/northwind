package com.djccnt15.northwind.domain.product.service;

import com.djccnt15.northwind.db.entity.ProductCategoryEntity;
import com.djccnt15.northwind.db.repository.ProductCategoryRepo;
import com.djccnt15.northwind.domain.product.converter.ProductCategoryConverter;
import com.djccnt15.northwind.domain.product.model.ProductCategoryCreateReq;
import com.djccnt15.northwind.global.exception.exceptions.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.djccnt15.northwind.global.code.StatusCode.BAD_REQUEST;
import static com.djccnt15.northwind.global.code.StatusCode.NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCategoryService {

    private final ProductCategoryRepo repository;
    private final ProductCategoryConverter converter;

    public ProductCategoryEntity getCategory(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ApiException(NOT_FOUND, "Product category not found"));
    }

    public void validateCategory(ProductCategoryCreateReq request) {
        if (repository.existsByName(request.getName())) {
            throw new ApiException(BAD_REQUEST, "Category name already exists");
        }
        if (repository.existsByCode(request.getCode())) {
            throw new ApiException(BAD_REQUEST, "Category code already exists");
        }
    }

    public void validateCategory(Long id, ProductCategoryCreateReq request) {
        if (repository.existsByNameAndIdNot(request.getName(), id)) {
            throw new ApiException(BAD_REQUEST, "Category name already exists");
        }
        if (repository.existsByCodeAndIdNot(request.getCode(), id)) {
            throw new ApiException(BAD_REQUEST, "Category code already exists");
        }
    }

    public void validateCategoryDeletion(ProductCategoryEntity entity) {
        if (!entity.getProductEntitySet().isEmpty()) {
            throw new ApiException(BAD_REQUEST, "Category has associated products");
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
