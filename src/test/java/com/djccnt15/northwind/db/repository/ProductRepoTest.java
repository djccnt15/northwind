package com.djccnt15.northwind.db.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class ProductRepoTest {

    @Autowired private ProductRepo repository;

    @Test
    void findByFilterKeyword() {
        var kw = "%%%s%%".formatted("Chai");
        var result = repository.findByFilter(kw, null, null, Pageable.unpaged());
        assertFalse(result.isEmpty());
        assertEquals("Chai", result.getContent().get(0).getName());
    }

    @Test
    void findByFilterCategoryId() {
        var kw = "%%%s%%".formatted("");
        var matched = repository.findByFilter(kw, 1L, null, Pageable.unpaged());
        assertFalse(matched.isEmpty());

        var unmatched = repository.findByFilter(kw, -1L, null, Pageable.unpaged());
        assertTrue(unmatched.isEmpty());
    }

    @Test
    void findByFilterDiscontinued() {
        var kw = "%%%s%%".formatted("");
        var active = repository.findByFilter(kw, null, false, Pageable.unpaged());
        assertFalse(active.isEmpty());

        var discontinued = repository.findByFilter(kw, null, true, Pageable.unpaged());
        assertTrue(discontinued.isEmpty());
    }

    @Test
    void findWithCategoryById() {
        var product = repository.findWithCategoryById(1L).orElseThrow();
        assertEquals("Chai", product.getName());
        assertNotNull(product.getProductCategory());
        assertEquals("Beverages", product.getProductCategory().getName());
    }
}
