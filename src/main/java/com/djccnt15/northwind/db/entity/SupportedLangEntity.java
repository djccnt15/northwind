package com.djccnt15.northwind.db.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "supported_lang")
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupportedLangEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(length = 50, nullable = false, unique = true)
    private String lang;
    
    @OneToMany(mappedBy = "preferredLang")
    @Builder.Default @Setter(AccessLevel.NONE)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Set<AppUserEntity> appUser = new HashSet<>();
    
    public SupportedLangEntity(String lang) {
        this.lang = lang;
    }
}
