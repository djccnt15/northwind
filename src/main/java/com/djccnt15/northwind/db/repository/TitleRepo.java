package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.TitleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TitleRepo extends JpaRepository<TitleEntity, Long> {
    
    boolean existsByTitle(String title);
    
    boolean existsByTitleAndIdNot(String title, Long id);
    
    Optional<TitleEntity> findFirstByTitle(String title);
}
