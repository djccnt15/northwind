package com.djccnt15.northwind.db.entity;

import com.djccnt15.northwind.db.entity.enums.SortOrderEnum;
import com.djccnt15.northwind.db.entity.id.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

import static com.djccnt15.northwind.domain.order.validation.OrderStatusModelConst.CODE_MAX_LENGTH;
import static com.djccnt15.northwind.domain.order.validation.OrderStatusModelConst.NAME_MAX_LENGTH;

@Getter
@Setter
@Entity
@Table(name = "order_status")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OrderStatusEntity extends BaseEntity {
    
    @NotNull
    @Column(length = CODE_MAX_LENGTH, nullable = false, unique = true)
    private String code;
    
    @NotNull
    @Column(length = NAME_MAX_LENGTH, nullable = false, unique = true)
    private String name;
    
    @Column
    @Enumerated(EnumType.STRING)
    private SortOrderEnum sortOrder;
    
    @OneToMany(mappedBy = "orderStatus")
    @Builder.Default @Setter(AccessLevel.NONE)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Set<OrdersEntity> orders = new HashSet<>();
}
