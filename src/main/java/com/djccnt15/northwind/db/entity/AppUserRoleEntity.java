package com.djccnt15.northwind.db.entity;

import com.djccnt15.northwind.db.entity.id.AppUserRoleId;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "app_user_role")
@IdClass(AppUserRoleId.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUserRoleEntity {
    
    @Id
    @JoinColumn(name = "app_user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private AppUserEntity appUser;
    
    @Id
    @JoinColumn(name = "user_role_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserRoleEntity userRole;
}
