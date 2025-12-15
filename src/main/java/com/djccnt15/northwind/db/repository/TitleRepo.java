package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.TitleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TitleRepo extends JpaRepository<TitleEntity, Long> {
}
