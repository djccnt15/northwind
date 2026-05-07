package com.djccnt15.northwind.db.entity;

import com.djccnt15.northwind.db.entity.id.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "employees")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EmployeeEntity extends BaseEntity {
    
    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    @Column(name = "last_name", nullable = false)
    private String lastName;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(name = "job_title")
    private String JobTitle;
    
    @Column(name = "primary_phone", nullable = false)
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
    private String address;
    
    @Column
    private String city;
    
    @Column
    private String region;
    
    @Column
    private String zipCode;
    
    @Column
    private String country;
    
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
    private List<EmployeeEntity> subordinate = new ArrayList<>();
}
