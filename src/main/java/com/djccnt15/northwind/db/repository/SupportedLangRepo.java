package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.SupportedLangEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupportedLangRepo extends JpaRepository<SupportedLangEntity, Long> {
}
