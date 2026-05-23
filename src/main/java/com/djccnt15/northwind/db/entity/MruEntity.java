package com.djccnt15.northwind.db.entity;

import com.djccnt15.northwind.db.entity.id.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Entity
@Table(name = "mru")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MruEntity extends BaseEntity {
    
    @NotNull
    @Column(name = "table_name", nullable = false)
    private String tableName;
    
    @NotNull
    @Column(nullable = false)
    private Long pkValue;
    
    @NotNull
    @JoinColumn(name = "employee_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private EmployeeEntity employee;
}
