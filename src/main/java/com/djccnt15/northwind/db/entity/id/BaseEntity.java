package com.djccnt15.northwind.db.entity.id;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass  // annotation for abstract table class
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(updatable = false)
    @CreationTimestamp  // @CreatedDate
    private LocalDateTime createdAt;
    
    @Column(insertable = false)
    @UpdateTimestamp  // @LastModifiedDate
    @EqualsAndHashCode.Exclude
    private LocalDateTime updatedAt;
    
    @CreatedBy
    @Column(updatable = false)
    private Long createdBy;  // PK of AccountEntity
    
    @LastModifiedBy
    @Column  // creator can also be the last modifier
    private Long lastModifiedBy;
}
