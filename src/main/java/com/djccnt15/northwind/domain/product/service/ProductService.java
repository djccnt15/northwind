package com.djccnt15.northwind.domain.product.service;

import com.djccnt15.northwind.db.entity.ProductCategoryEntity;
import com.djccnt15.northwind.db.entity.ProductEntity;
import com.djccnt15.northwind.db.repository.ProductRepo;
import com.djccnt15.northwind.domain.product.converter.ProductConverter;
import com.djccnt15.northwind.domain.product.model.ProductCreateReq;
import com.djccnt15.northwind.global.exception.exceptions.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static com.djccnt15.northwind.global.code.StatusCode.BAD_REQUEST;
import static com.djccnt15.northwind.global.code.StatusCode.NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepo repository;
    private final ProductConverter converter;

    public ProductEntity getProduct(Long id) {
        return repository.findWithCategoryById(id)
            .orElseThrow(() -> new ApiException(NOT_FOUND, "Product not found"));
    }

    public void validateProduct(ProductCreateReq request) {
        if (repository.existsByCode(request.getCode())) {
            throw new ApiException(BAD_REQUEST, "Product code already exists");
        }
        if (repository.existsByName(request.getName())) {
            throw new ApiException(BAD_REQUEST, "Product name already exists");
        }
    }

    public void validateProduct(Long id, ProductCreateReq request) {
        if (repository.existsByCodeAndIdNot(request.getCode(), id)) {
            throw new ApiException(BAD_REQUEST, "Product code already exists");
        }
        if (repository.existsByNameAndIdNot(request.getName(), id)) {
            throw new ApiException(BAD_REQUEST, "Product name already exists");
        }
    }

    public ProductEntity createProduct(ProductCreateReq request, ProductCategoryEntity category) {
        var entity = converter.toEntity(request, category);
        repository.save(entity);
        return entity;
    }

    public Page<ProductEntity> getProducts(String kw, Long categoryId, Boolean discontinued, Pageable pageable) {
        return repository.findByFilter(kw, categoryId, discontinued, pageable);
    }

    public ProductEntity updateProduct(ProductEntity entity, ProductCreateReq request, ProductCategoryEntity category) {
        entity.setCode(request.getCode());
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setStandardUnitCost(request.getStandardUnitCost());
        entity.setUnitPrice(request.getUnitPrice());
        entity.setReorderLevel(request.getReorderLevel());
        entity.setTargetLevel(request.getTargetLevel());
        entity.setQuantityPerUnit(request.getQuantityPerUnit());
        entity.setMinimumReorderQuantity(request.getMinimumReorderQuantity());
        entity.setDiscontinued(request.getDiscontinued());
        entity.setProductCategory(category);
        repository.save(entity);
        return entity;
    }

    public void discontinueProduct(ProductEntity entity) {
        entity.setDiscontinued(true);
        repository.save(entity);
    }
}
