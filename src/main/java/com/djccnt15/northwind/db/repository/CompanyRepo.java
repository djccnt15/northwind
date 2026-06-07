package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.CompanyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CompanyRepo extends JpaRepository<CompanyEntity, Long> {

    @Query(value = """
        SELECT c FROM CompanyEntity c JOIN FETCH c.companyType t JOIN FETCH c.taxStatus s
        WHERE c.name LIKE :kw
        AND (:typeId IS NULL OR t.id = :typeId)
        """,
        countQuery = """
        SELECT COUNT(c) FROM CompanyEntity c JOIN c.companyType t
        WHERE c.name LIKE :kw
        AND (:typeId IS NULL OR t.id = :typeId)
        """)
    Page<CompanyEntity> findByFilter(@Param("kw") String kw,
        @Param("typeId") Long typeId,
        Pageable pageable);

    @EntityGraph(attributePaths = {"companyType", "taxStatus"})
    Optional<CompanyEntity> findWithRelationById(Long id);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
}
