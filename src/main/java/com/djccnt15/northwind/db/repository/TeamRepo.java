package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.TeamEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamRepo extends JpaRepository<TeamEntity, Long> {
    
    Optional<TeamEntity> findFirstByName(String name);
    
    Optional<TeamEntity> findFirstByNameAndIdNot(String name, Long id);
    
    List<TeamEntity> findByNameLike(String name, Pageable pageable);
    
    Integer countByNameLike(String name);
}
