package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepo extends JpaRepository<ProductEntity, Long> {

    @Query(value = """
        SELECT p FROM ProductEntity p JOIN FETCH p.productCategory c
        WHERE (p.name LIKE :kw OR p.code LIKE :kw)
        AND (:categoryId IS NULL OR c.id = :categoryId)
        AND (:discontinued IS NULL OR p.discontinued = :discontinued)
        """,
        countQuery = """
        SELECT COUNT(p) FROM ProductEntity p JOIN p.productCategory c
        WHERE (p.name LIKE :kw OR p.code LIKE :kw)
        AND (:categoryId IS NULL OR c.id = :categoryId)
        AND (:discontinued IS NULL OR p.discontinued = :discontinued)
        """)
    Page<ProductEntity> findByFilter(@Param("kw") String kw,
        @Param("categoryId") Long categoryId,
        @Param("discontinued") Boolean discontinued,
        Pageable pageable);

    @EntityGraph(attributePaths = {"productCategory"})
    Optional<ProductEntity> findWithCategoryById(Long id);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
}
