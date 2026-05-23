package com.djccnt15.northwind.db.entity;

import com.djccnt15.northwind.db.entity.id.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

import static com.djccnt15.northwind.domain.team.validation.TeamModelConst.NAME_MAX_LENGTH;

@Getter
@Setter
@Entity
@Table(name = "team")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TeamEntity extends BaseEntity {
    
    @NotNull
    @Column(length= NAME_MAX_LENGTH, nullable = false, unique = true)
    private String name;
    
    @OneToMany(mappedBy = "team")
    @Builder.Default @Setter(AccessLevel.NONE)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Set<AppUserEntity> members = new HashSet<>();
    
    public TeamEntity(String name) {
        this.name = name;
    }
    
    public void addMember(AppUserEntity user) {
        members.add(user);
        user.setTeam(this);
    }
    
    public void removeMember(AppUserEntity user) {
        members.remove(user);
        user.setTeam(null);
    }
}
