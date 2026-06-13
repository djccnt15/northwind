package com.djccnt15.northwind.db.entity;

import com.djccnt15.northwind.db.entity.id.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;

import java.math.BigDecimal;
import java.sql.Types;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "purchase_orders")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PurchaseOrderEntity extends BaseEntity {
    
    @NotNull
    @Column(name = "submitted_date", nullable = false)
    private LocalDate submittedDate;
    
    @Column(name = "approved_date")
    private LocalDate approvedDate;
    
    @Column(name = "received_date")
    private LocalDate receivedDate;
    
    @Column(name = "shipping_fee")
    private Integer shippingFee;
    
    @Column(name = "tax_amount")
    private BigDecimal taxAmount;
    
    @Column(name = "payment_date")
    private LocalDate paymentDate;
    
    @Column(name = "payment_amount")
    private Integer paymentAmount;
    
    @Column(name = "payment_method")
    private String paymentMethod;
    
    @Column
    @JdbcTypeCode(Types.LONGNVARCHAR)
    private String note;
    
    @JoinColumn(name = "vendor_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private CompanyEntity vendor;
    
    @NotNull
    @JoinColumn(name = "submitted_by", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private EmployeeEntity submittedBy;
    
    @JoinColumn(name = "approved_by")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private EmployeeEntity approvedBy;
    
    @JoinColumn(name = "status_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private PurchaseOrderStatusEntity status;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default @Setter(AccessLevel.NONE)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Set<PurchaseOrderDetailEntity> purchaseOrderDetails = new HashSet<>();
}
