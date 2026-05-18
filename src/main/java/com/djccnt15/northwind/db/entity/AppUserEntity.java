package com.djccnt15.northwind.db.entity;

import com.djccnt15.northwind.db.entity.id.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static com.djccnt15.northwind.domain.user.validation.AppUserModelConst.EMAIL_MAX_LENGTH;
import static com.djccnt15.northwind.domain.user.validation.AppUserModelConst.USERNAME_MAX_LENGTH;

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
    
    @NotNull
    @Column(unique = true, length = USERNAME_MAX_LENGTH, nullable = false)
    private String username;
    
    @NotNull
    @Column(nullable = false)
    private String password;
    
    @Column(unique = true, length = EMAIL_MAX_LENGTH)
    private String email;
    
    @NotNull
    @Column(name = "is_verified", nullable = false)
    @ColumnDefault(value = "false")  // annotation for ddl-auto
    @Builder.Default  // annotation for lombok default
    private boolean isVerified = false;
    
    @Column(name = "live_until")
    private LocalDateTime liveUntil;
    
    @Column(name = "password_changed_at")
    private LocalDateTime passwordChangedAt;
    
    @NotNull
    @Column(name = "login_failed_count", nullable = false)
    @ColumnDefault(value = "0")
    @Builder.Default
    private int loginFailedCount = 0;
    
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
    
    @OneToMany(mappedBy = "appUser", cascade = CascadeType.REMOVE)
    @Builder.Default
    @ToString.Exclude @EqualsAndHashCode.Exclude @Setter(AccessLevel.NONE)
    private Set<AppUserRoleEntity> appUserRole = new HashSet<>();
    
    @NotNull
    @JoinColumn(name = "team_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private TeamEntity team;
    
    @OneToOne(mappedBy = "appUser", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private EmployeeEntity employee;
    
    @OneToMany(mappedBy = "appUser")
    @Builder.Default @Setter(AccessLevel.NONE)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Set<OrdersEntity> orders = new HashSet<>();

    public void addAppUserRole(AppUserRoleEntity roleLink) {
        if (roleLink == null) return;
        appUserRole.add(roleLink);
        roleLink.setAppUser(this);
    }
    
    public void resetAppUserRole() {
        this.appUserRole = new HashSet<>();
    }

    public void removeAppUserRole(AppUserRoleEntity roleLink) {
        if (roleLink == null) return;
        appUserRole.remove(roleLink);
        if (roleLink.getAppUser() == this) {
            roleLink.setAppUser(null);
        }
    }
    
    public void updateEmployee(EmployeeEntity employee) {
        this.employee = employee;
        if (employee.getAppUser() != this) {
            employee.setAppUser(this);
        }
    }
}
