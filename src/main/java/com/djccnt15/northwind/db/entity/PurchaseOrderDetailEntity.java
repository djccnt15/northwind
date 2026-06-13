package com.djccnt15.northwind.db.entity;

import com.djccnt15.northwind.db.entity.id.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "purchase_order_detail")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PurchaseOrderDetailEntity extends BaseEntity {

    @Column
    private Integer quantity;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @NotNull
    @JoinColumn(name = "product_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private ProductEntity product;

    @NotNull
    @JoinColumn(name = "purchase_order_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private PurchaseOrderEntity purchaseOrder;
}
