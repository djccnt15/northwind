package com.djccnt15.northwind.db.entity;

import com.djccnt15.northwind.db.entity.embaddable.AddressEmbed;
import com.djccnt15.northwind.db.entity.id.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static com.djccnt15.northwind.domain.user.validation.EmployeeModelConst.*;

@Getter
@Setter
@Entity
@Table(name = "employee")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EmployeeEntity extends BaseEntity {
    
    @NotNull
    @Column(name = "first_name", length = FIRST_NAME_MAX_LENGTH, nullable = false)
    private String firstName;
    
    @NotNull
    @Column(name = "last_name", length = LAST_NAME_MAX_LENGTH, nullable = false)
    private String lastName;
    
    @Column(length = EMAIL_MAX_LENGTH, unique = true)
    private String email;
    
    @Column(name = "job_title", length = JOB_TITLE_MAX_LENGTH)
    private String jobTitle;
    
    @Column(name = "primary_phone", length = PRIMARY_PHONE_MAX_LENGTH)
    private String primaryPhone;
    
    @Column(name = "secondary_phone", length = SECONDARY_PHONE_MAX_LENGTH)
    private String secondaryPhone;
    
    @Column
    private String notes;
    
    @Column(name = "title_of_courtesy", length = TITLE_OF_COURTESY_MAX_LENGTH)
    private String titleOfCourtesy;
    
    @Column(name = "birth_date")
    private LocalDate birthDate;
    
    @NotNull
    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;
    
    @Column
    @Embedded
    private AddressEmbed address;
    
    @Column
    private byte[] photo;
    
    @NotNull
    @JoinColumn(name = "title_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private TitleEntity title;
    
    @JoinColumn(name = "supervisor_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private EmployeeEntity supervisor;
    
    @OneToMany(mappedBy = "supervisor", cascade = CascadeType.ALL)
    @Builder.Default @Setter(AccessLevel.NONE)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Set<EmployeeEntity> subordinates = new HashSet<>();
    
    @JoinColumn(name = "app_user_id", unique = true)
    @OneToOne(fetch = FetchType.LAZY)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private AppUserEntity appUser;
    
    @OneToMany(mappedBy = "submittedBy", cascade = CascadeType.ALL)
    @Builder.Default @Setter(AccessLevel.NONE)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Set<PurchaseOrderEntity> purchaseOrders = new HashSet<>();
    
    @OneToMany(mappedBy = "approvedBy", cascade = CascadeType.ALL)
    @Builder.Default @Setter(AccessLevel.NONE)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Set<PurchaseOrderEntity> approvedPurchaseOrders = new HashSet<>();
}
