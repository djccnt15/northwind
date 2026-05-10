package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.AppUserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppUserRepo extends JpaRepository<AppUserEntity, Long> {
    
    @EntityGraph(attributePaths = {"appUserRole", "appUserRole.userRole", "team"})
    Optional<AppUserEntity> findFullFirstByUsername(String username);
    
    Optional<AppUserEntity> findFirstByUsername(String username);
    
    Optional<AppUserEntity> findFirstByUsernameAndIdNot(String username, Long id);
    
    @Modifying
    @Query("UPDATE AppUserEntity u SET u.loginFailedCount = u.loginFailedCount + 1 WHERE u.username = :name")
    void increaseLoginFailedCount(@Param("name") String username);
    
    @Modifying
    @Query("""
        UPDATE AppUserEntity u
        SET
            u.loginFailedCount = 0,
            u.lastLoginAt = :now
        WHERE u.id = :id
        """)
    void handleLoginSuccess(@Param("id") Long id, @Param("now") LocalDateTime lastLoginAt);
    
    Optional<AppUserEntity> findFirstByEmail(String email);
    
    Optional<AppUserEntity> findFirstByEmailAndIdNot(String email, Long id);
    
    Page<AppUserEntity> findByUsernameLikeOrEmailLike(String usernameKw, String emailKw, Pageable pageable);
    
    @EntityGraph(attributePaths = {"appUserRole", "appUserRole.userRole", "team"})
    List<AppUserEntity> findFullByIdInOrderById(List<Long> ids);
    
    @Query("""
        SELECT u FROM AppUserEntity u
        JOIN FETCH u.appUserRole ur
        JOIN FETCH ur.userRole r
        WHERE r.name = :name
    """)
    List<AppUserEntity> findByRoleName(String name);
}
