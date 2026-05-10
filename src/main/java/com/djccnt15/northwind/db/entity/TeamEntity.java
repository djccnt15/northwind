package com.djccnt15.northwind.db.entity;

import com.djccnt15.northwind.db.entity.id.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "team")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TeamEntity extends BaseEntity {
    
    @Column(unique = true)
    private String name;
    
    @OneToMany(mappedBy = "team")
    @Builder.Default
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Set<AppUserEntity> members = new java.util.HashSet<>();
}
