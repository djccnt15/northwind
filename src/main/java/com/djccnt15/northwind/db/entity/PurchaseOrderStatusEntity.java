package com.djccnt15.northwind.db.entity;

import com.djccnt15.northwind.db.entity.enums.SortOrderEnum;
import com.djccnt15.northwind.db.entity.id.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import static com.djccnt15.northwind.global.constants.validation.PurchaseOrderModelConst.NAME_MAX_LENGTH;

@Getter
@Setter
@Entity
@Table(name = "purchase_order_status")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PurchaseOrderStatusEntity extends BaseEntity {
    
    @NotNull
    @Column(length = NAME_MAX_LENGTH, nullable = false, unique = true)
    private String name;
    
    @Column
    @Enumerated(EnumType.STRING)
    private SortOrderEnum sortOrder;
}
