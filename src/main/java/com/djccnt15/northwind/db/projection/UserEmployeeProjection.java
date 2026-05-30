package com.djccnt15.northwind.db.projection;

import com.djccnt15.northwind.db.entity.AppUserEntity;
import com.djccnt15.northwind.db.entity.EmployeeEntity;

public interface UserEmployeeProjection {
    
    AppUserEntity getAppUser();
    
    EmployeeEntity getEmployee();
}
