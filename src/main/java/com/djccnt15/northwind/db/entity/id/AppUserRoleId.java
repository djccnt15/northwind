package com.djccnt15.northwind.db.entity.id;

import lombok.Data;

import java.io.Serializable;

@Data
public class AppUserRoleId implements Serializable {
    
    private Long appUser;
    
    private Long userRole;
}
