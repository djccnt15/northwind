package com.djccnt15.northwind.db.entity.id;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass  // annotation for abstract table class
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @Column(insertable = false)
    @UpdateTimestamp
    @EqualsAndHashCode.Exclude
    private LocalDateTime updatedAt;
}
