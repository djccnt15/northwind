package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.SupportedLangEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SupportedLangRepo extends JpaRepository<SupportedLangEntity, Long> {

    Optional<SupportedLangEntity> findFirstByLang(String lang);
}
