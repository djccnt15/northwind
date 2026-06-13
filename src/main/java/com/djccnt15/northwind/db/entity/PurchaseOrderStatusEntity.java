package com.djccnt15.northwind.db.entity;

import com.djccnt15.northwind.db.entity.enums.SortOrderEnum;
import com.djccnt15.northwind.db.entity.id.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

import static com.djccnt15.northwind.domain.purchase.validation.PurchaseOrderStatusModelConst.CODE_MAX_LENGTH;
import static com.djccnt15.northwind.domain.purchase.validation.PurchaseOrderStatusModelConst.NAME_MAX_LENGTH;

@Getter
@Setter
@Entity
@Table(name = "purchase_order_status")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PurchaseOrderStatusEntity extends BaseEntity {

    @NotNull
    @Column(length = CODE_MAX_LENGTH, nullable = false, unique = true)
    private String code;

    @NotNull
    @Column(length = NAME_MAX_LENGTH, nullable = false, unique = true)
    private String name;

    @Column
    @Enumerated(EnumType.STRING)
    private SortOrderEnum sortOrder;

    @OneToMany(mappedBy = "status")
    @Builder.Default @Setter(AccessLevel.NONE)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Set<PurchaseOrderEntity> purchaseOrders = new HashSet<>();
}
