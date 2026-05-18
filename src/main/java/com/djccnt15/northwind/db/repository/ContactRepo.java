package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.ContactEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepo extends JpaRepository<ContactEntity, Long> {
}
