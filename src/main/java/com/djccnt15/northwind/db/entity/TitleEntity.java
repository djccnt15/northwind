package com.djccnt15.northwind.db.entity;

import com.djccnt15.northwind.db.entity.id.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "title")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TitleEntity extends BaseEntity {
    
    @Column(unique = true)
    private String title;
    
    @OneToMany(mappedBy = "title")
    @Builder.Default
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Set<EmployeeEntity> employees = new HashSet<>();
}
