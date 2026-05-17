package com.djccnt15.northwind.db.entity;

import com.djccnt15.northwind.db.entity.id.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

import static com.djccnt15.northwind.global.constants.validation.UserRoleModelConst.NAME_MAX_LENGTH;

@Getter
@Setter
@Entity
@Table(name = "user_role", indexes = @Index(columnList = "name"))
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserRoleEntity extends BaseEntity {
    
    @NotNull
    @Column(length = NAME_MAX_LENGTH, nullable = false, unique = true)
    private String name;
    
    @OneToMany(mappedBy = "userRole", cascade = CascadeType.REMOVE)
    @Builder.Default
    @ToString.Exclude @EqualsAndHashCode.Exclude @Setter(AccessLevel.NONE)
    private Set<AppUserRoleEntity> appUserRole = new HashSet<>();
    
    public UserRoleEntity(String name) {
        this.name = name;
    }
}
