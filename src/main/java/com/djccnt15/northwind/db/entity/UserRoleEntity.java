package com.djccnt15.northwind.db.entity;

import com.djccnt15.northwind.db.entity.id.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "user_role")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserRoleEntity extends BaseEntity {
    
    @Column(nullable = false)
    @NotNull
    private String name;
    
    @OneToMany(mappedBy = "userRole", cascade = CascadeType.REMOVE)
    @Builder.Default
    @ToString.Exclude
    private Set<AppUserRoleEntity> appUserRole = new HashSet<>();
}
