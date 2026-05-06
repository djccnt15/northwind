package com.djccnt15.northwind.db.entity;

import com.djccnt15.northwind.db.entity.id.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "app_user",
    indexes = {
        @Index(columnList = "username"),
        @Index(columnList = "email")
    })
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AppUserEntity extends BaseEntity {
    
    @Column(unique = true, length = 25, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(unique = true)
    private String email;
    
    @Column(name = "is_verified", nullable = false)
    @ColumnDefault(value = "false")  // annotation for ddl-auto
    @Builder.Default  // annotation for lombok default
    private boolean isVerified = false;
    
    @Column(name = "live_until")
    private LocalDateTime liveUntil;
    
    @Column(name = "password_changed_at")
    private LocalDateTime passwordChangedAt;
    
    @Column(name = "login_failed_count", nullable = false)
    @ColumnDefault(value = "0")
    @Builder.Default
    private int loginFailedCount = 0;
    
    @OneToMany(mappedBy = "appUser", cascade = CascadeType.REMOVE)
    @Builder.Default
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Set<AppUserRoleEntity> appUserRole = new HashSet<>();

    public void addAppUserRole(AppUserRoleEntity roleLink) {
        if (roleLink == null) return;
        appUserRole.add(roleLink);
        roleLink.setAppUser(this);
    }

    public void removeAppUserRole(AppUserRoleEntity roleLink) {
        if (roleLink == null) return;
        appUserRole.remove(roleLink);
        if (roleLink.getAppUser() == this) {
            roleLink.setAppUser(null);
        }
    }
}
