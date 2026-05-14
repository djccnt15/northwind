package com.djccnt15.northwind.db.entity;

import com.djccnt15.northwind.db.entity.embaddable.AddressEmbed;
import com.djccnt15.northwind.db.entity.id.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "employee")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EmployeeEntity extends BaseEntity {
    
    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    @Column(name = "last_name", nullable = false)
    private String lastName;
    
    @Column(unique = true)
    private String email;
    
    @Column(name = "job_title")
    private String jobTitle;
    
    @Column(name = "primary_phone")
    private String primaryPhone;
    
    @Column(name = "secondary_phone")
    private String secondaryPhone;
    
    @Column
    private String notes;
    
    @Column(name = "title_of_courtesy")
    private String titleOfCourtesy;
    
    @Column(name = "birth_date")
    private LocalDate birthDate;
    
    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;
    
    @Column
    @Embedded
    private AddressEmbed address;
    
    @Column
    private byte[] photo;
    
    @JoinColumn(name = "title_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private TitleEntity title;
    
    @JoinColumn(name = "supervisor_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private EmployeeEntity supervisor;
    
    @OneToMany(mappedBy = "supervisor", cascade = CascadeType.ALL)
    @Builder.Default
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Set<EmployeeEntity> subordinates = new HashSet<>();
    
    @JoinColumn(name = "app_user_id", unique = true)
    @OneToOne(fetch = FetchType.LAZY)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private AppUserEntity appUser;
}
