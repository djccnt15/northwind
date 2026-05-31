package com.djccnt15.northwind.db.repository;

import com.djccnt15.northwind.db.entity.AppUserEntity;
import com.djccnt15.northwind.db.projection.UserEmployeeProjection;
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
    
    @EntityGraph(attributePaths = {"appUserRole", "appUserRole.userRole"})
    Optional<AppUserEntity> findWithRoleFirstByUsername(String username);
    
    @Modifying
    @Query("""
        UPDATE AppUserEntity u
        SET u.loginFailedCount = u.loginFailedCount + 1
        WHERE u.username = :name
        """)
    void increaseLoginFailedCount(@Param("name") String username);
    
    @Modifying
    @Query("""
        UPDATE AppUserEntity u
        SET
            u.loginFailedCount = 0
            , u.lastLoginAt = :now
        WHERE u.id = :id
        """)
    void handleLoginSuccess(@Param("id") Long id, @Param("now") LocalDateTime lastLoginAt);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    boolean existsByEmailAndIdNot(String email, Long id);
    
    boolean existsByUsernameAndIdNot(String username, Long id);
    
    Page<AppUserEntity> findByUsernameLikeOrEmailLike(String usernameKw, String emailKw, Pageable pageable);
    
    // @EntityGraph(attributePaths = {"appUserRole", "appUserRole.userRole", "team"})
    @Query("""
        SELECT u as appUser, e as employee
        FROM AppUserEntity u
        LEFT JOIN FETCH u.appUserRole ur
        LEFT JOIN FETCH ur.userRole r
        LEFT JOIN FETCH u.team t
        LEFT JOIN FETCH EmployeeEntity e ON e.appUser.id = u.id
        LEFT JOIN FETCH TitleEntity ti ON ti.id = e.title.id
        WHERE u.id IN :ids
        ORDER BY u.id
        """)
    List<UserEmployeeProjection> findFullByIdInOrderById(List<Long> ids);
    
    @Query("""
        SELECT DISTINCT u.id
        FROM AppUserEntity u
        JOIN u.appUserRole ur
        JOIN ur.userRole r
        WHERE r.name = :name
        """)
    List<Long> findIdsByRoleName(String name);
    
    @EntityGraph(attributePaths = {"appUserRole", "appUserRole.userRole", "team"})
    Optional<AppUserEntity> findFullFirstById(Long userId);
}
