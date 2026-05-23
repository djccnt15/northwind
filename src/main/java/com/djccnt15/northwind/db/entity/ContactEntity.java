package com.djccnt15.northwind.db.entity;

import com.djccnt15.northwind.db.entity.id.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;

import static com.djccnt15.northwind.domain.user.validation.EmployeeModelConst.*;
import static com.djccnt15.northwind.domain.user.validation.EmployeeModelConst.JOB_TITLE_MAX_LENGTH;
import static com.djccnt15.northwind.domain.user.validation.EmployeeModelConst.PRIMARY_PHONE_MAX_LENGTH;
import static com.djccnt15.northwind.domain.user.validation.EmployeeModelConst.SECONDARY_PHONE_MAX_LENGTH;

@Getter
@Setter
@Entity
@Table(name = "Contact")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ContactEntity extends BaseEntity {
    
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
    @JdbcTypeCode(Types.LONGNVARCHAR)
    private String notes;
    
    @JoinColumn(name = "company_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private CompanyEntity company;
}
