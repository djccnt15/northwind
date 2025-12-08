package com.djccnt15.northwind.db.entity;

import com.djccnt15.northwind.db.entity.id.AppUserRoleId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "app_user_role")
@IdClass(AppUserRoleId.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUserRoleEntity {
    
    @Id
    @JoinColumn(name = "app_user_id")
    @ManyToOne
    private AppUserEntity appUser;
    
    @Id
    @JoinColumn(name = "user_role_id")
    @ManyToOne
    private UserRoleEntity userRole;
    
    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdDatetime;
}
