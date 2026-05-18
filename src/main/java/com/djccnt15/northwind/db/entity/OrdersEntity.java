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
@Table(name = "orders")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OrdersEntity extends BaseEntity {
    
    @NotNull
    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;
    
    @Column(name = "required_date")
    private LocalDate invoiceDate;
    
    @Column(name = "shipped_date")
    private LocalDate shippedDate;
    
    @Column(name = "shipping_fee")
    private Integer shippingFee;
    
    @Column(name = "tax_rate")
    private Integer taxRate;
    
    @Column(name = "payment_type")
    private String paymentType;
    
    @Column(name = "paid_date")
    private LocalDate paidDate;
    
    @Column
    private String notes;
    
    @NotNull
    @JoinColumn(name = "app_user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private AppUserEntity appUser;
    
    @NotNull
    @JoinColumn(name = "customer_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private CompanyEntity customer;
    
    @JoinColumn(name = "shipper_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private CompanyEntity shipper;
    
    @JoinColumn(name = "tax_status_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private TaxStatusEntity taxStatus;
    
    @JoinColumn(name = "status_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private OrderStatusEntity orderStatus;
}
