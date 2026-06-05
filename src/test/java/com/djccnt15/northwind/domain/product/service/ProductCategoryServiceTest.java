package com.djccnt15.northwind.domain.product.service;

import com.djccnt15.northwind.domain.product.model.ProductCategoryCreateReq;
import com.djccnt15.northwind.global.exception.exceptions.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static com.djccnt15.northwind.global.util.RandomUtil.getRandUuidString;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class ProductCategoryServiceTest {

    @Autowired private ProductCategoryService service;

    private ProductCategoryCreateReq randomReq() {
        return new ProductCategoryCreateReq(getRandUuidString(), getRandUuidString(), "desc");
    }

    @Test
    void getCategory() {
        var category = service.getCategory(1L);
        assertNotNull(category);
        assertEquals(1L, category.getId());
        assertThrows(ApiException.class, () -> service.getCategory(-1L));
    }

    @Test
    void validateCategory() {
        // given
        var dupName = new ProductCategoryCreateReq("Beverages", getRandUuidString(), null);
        var dupCode = new ProductCategoryCreateReq(getRandUuidString(), "BEV", null);

        // when & then
        assertThrows(ApiException.class, () -> service.validateCategory(dupName));
        assertThrows(ApiException.class, () -> service.validateCategory(dupCode));
        assertDoesNotThrow(() -> service.validateCategory(randomReq()));
    }

    @Test
    void validateCategoryForUpdate() {
        // given
        var og = service.createCategory(randomReq());
        var other = service.createCategory(randomReq());
        var conflict = new ProductCategoryCreateReq(other.getName(), getRandUuidString(), null);
        var self = new ProductCategoryCreateReq(og.getName(), og.getCode(), null);

        // when & then
        assertThrows(ApiException.class, () -> service.validateCategory(og.getId(), conflict));
        assertDoesNotThrow(() -> service.validateCategory(og.getId(), self));
    }

    @Test
    void createCategory() {
        var req = randomReq();
        var entity = service.createCategory(req);
        assertNotNull(entity.getId());
        assertEquals(req.getName(), entity.getName());
        assertEquals(req.getCode(), entity.getCode());
    }

    @Test
    void getCategories() {
        var all = service.getCategories();
        assertNotNull(all);
        assertFalse(all.isEmpty());
    }

    @Test
    void updateCategory() {
        // given
        var entity = service.createCategory(randomReq());
        var req = randomReq();

        // when
        var updated = service.updateCategory(entity, req);

        // then
        assertEquals(entity.getId(), updated.getId());
        assertEquals(req.getName(), updated.getName());
        assertEquals(req.getCode(), updated.getCode());
    }

    @Test
    void deleteCategory() {
        // given
        var entity = service.createCategory(randomReq());

        // when
        service.deleteCategory(entity);

        // then
        assertThrows(ApiException.class, () -> service.getCategory(entity.getId()));
    }

    @Test
    @Transactional
    void validateCategoryDeletion() {
        // given — category 1 has product Chai associated
        var withProducts = service.getCategory(1L);
        assertThrows(ApiException.class, () -> service.validateCategoryDeletion(withProducts));

        // empty category
        var empty = service.createCategory(randomReq());
        assertDoesNotThrow(() -> service.validateCategoryDeletion(service.getCategory(empty.getId())));
    }
}
