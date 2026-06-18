package com.djccnt15.northwind.domain.product.service;

import com.djccnt15.northwind.db.entity.ProductEntity;
import com.djccnt15.northwind.global.exception.exceptions.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class ProductServiceTest {

    // seeded product (P001 / Chai)
    private static final Long PRODUCT_ID = 1L;
    private static final Long MISSING_PRODUCT_ID = 9999L;

    @Autowired private ProductService productService;

    @Test
    void getProducts_batch_returnsMapKeyedById() {
        var map = productService.getProducts(List.of(PRODUCT_ID));

        assertEquals(1, map.size());
        var product = map.get(PRODUCT_ID);
        assertNotNull(product);
        assertEquals(PRODUCT_ID, product.getId());
    }

    @Test
    void getProducts_batch_deduplicatesRepeatedIds() {
        // a repeated id must not count as "missing" against the distinct id set
        var map = productService.getProducts(List.of(PRODUCT_ID, PRODUCT_ID));

        assertEquals(1, map.size());
        assertNotNull(map.get(PRODUCT_ID));
    }

    @Test
    void getProducts_batch_missingId_throwsNotFound() {
        // any requested id absent from the result triggers NOT_FOUND, matching getProduct(Long)
        var ids = List.of(PRODUCT_ID, MISSING_PRODUCT_ID);
        assertThrows(ApiException.class, () -> productService.getProducts(ids));
    }
}
