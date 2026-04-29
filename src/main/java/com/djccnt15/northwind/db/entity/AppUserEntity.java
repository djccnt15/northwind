package com.djccnt15.northwind.db.entity;

import com.djccnt15.northwind.db.entity.id.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "app_user", indexes = @Index(columnList = "username"))
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AppUserEntity extends BaseEntity {
    
    @Column(unique = true, length = 25)
    private String username;
    
    @Column
    private String password;
    
    @Column(unique = true)
    private String email;
    
    @Column(name = "is_verified", nullable = false)
    @ColumnDefault(value = "false")  // annotation for ddl-auto
    @Builder.Default  // annotation for lombok default
    @NotNull
    private boolean isVerified = false;
    
    @OneToMany(mappedBy = "appUser", cascade = CascadeType.REMOVE)
    @Builder.Default
    @ToString.Exclude
    private Set<AppUserRoleEntity> appUserRole = new HashSet<>();
}
