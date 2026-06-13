package com.djccnt15.northwind.db.entity;

import com.djccnt15.northwind.db.entity.id.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "stock_take")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class StockTakeEntity extends BaseEntity {

    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "stock_take_date")
    private LocalDate stockTakeDate;
    
    @Column(name = "quantity_on_hand")
    private Long quantityOnHand;
    
    @Column(name = "expected_quantity")
    private Long expectedQuantity;
    
    @NotNull
    @JoinColumn(name = "product_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private ProductEntity product;
}
