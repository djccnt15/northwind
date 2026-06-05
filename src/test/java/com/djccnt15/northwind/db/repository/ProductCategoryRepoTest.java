package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.ProductCategoryEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;

import static com.djccnt15.northwind.global.util.RandomUtil.getRandUuidString;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class ProductCategoryRepoTest {

    @Autowired private ProductCategoryRepo repository;

    @Test
    void uniqueName() {
        // given
        var name = getRandUuidString();
        repository.save(ProductCategoryEntity.builder().name(name).code(getRandUuidString()).build());

        // when
        var duplicate = ProductCategoryEntity.builder().name(name).code(getRandUuidString()).build();

        // then
        assertThrows(
            DataIntegrityViolationException.class,
            () -> repository.saveAndFlush(duplicate)
        );
    }

    @Test
    void uniqueCode() {
        // given
        var code = getRandUuidString();
        repository.save(ProductCategoryEntity.builder().name(getRandUuidString()).code(code).build());

        // when
        var duplicate = ProductCategoryEntity.builder().name(getRandUuidString()).code(code).build();

        // then
        assertThrows(
            DataIntegrityViolationException.class,
            () -> repository.saveAndFlush(duplicate)
        );
    }

    @Test
    void existsByName() {
        assertTrue(repository.existsByName("Beverages"));
        assertFalse(repository.existsByName(getRandUuidString()));
    }

    @Test
    void existsByNameAndIdNot() {
        assertFalse(repository.existsByNameAndIdNot("Beverages", 1L));
        assertTrue(repository.existsByNameAndIdNot("Beverages", -1L));
    }

    @Test
    void existsByCode() {
        assertTrue(repository.existsByCode("BEV"));
        assertFalse(repository.existsByCode(getRandUuidString()));
    }

    @Test
    void existsByCodeAndIdNot() {
        assertFalse(repository.existsByCodeAndIdNot("BEV", 1L));
        assertTrue(repository.existsByCodeAndIdNot("BEV", -1L));
    }

    @Test
    void findByNameLike() {
        var kw = "%%%s%%".formatted("Bev");
        var result = repository.findByNameLike(kw, Pageable.unpaged());
        assertFalse(result.isEmpty());
    }
}
