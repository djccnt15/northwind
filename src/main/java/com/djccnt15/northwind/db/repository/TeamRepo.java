package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.TeamEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepo extends JpaRepository<TeamEntity, Long> {
}
