package com.djccnt15.northwind.db.entity;

import com.djccnt15.northwind.db.entity.id.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

import static com.djccnt15.northwind.global.constants.validation.TitleModelConst.TITLE_MAX_LENGTH;

@Getter
@Setter
@Entity
@Table(name = "title")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TitleEntity extends BaseEntity {
    
    @NotNull
    @Column(length = TITLE_MAX_LENGTH, nullable = false, unique = true)
    private String title;
    
    @OneToMany(mappedBy = "title")
    @Builder.Default
    @ToString.Exclude @EqualsAndHashCode.Exclude @Setter(AccessLevel.NONE)
    private Set<EmployeeEntity> employees = new HashSet<>();
    
    public TitleEntity(String title) {
        this.title = title;
    }
}
