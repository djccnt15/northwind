package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.ContactEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContactRepo extends JpaRepository<ContactEntity, Long> {

    List<ContactEntity> findByCompanyIdOrderByLastNameAscFirstNameAsc(Long companyId);

    Optional<ContactEntity> findByIdAndCompanyId(Long id, Long companyId);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);
}
