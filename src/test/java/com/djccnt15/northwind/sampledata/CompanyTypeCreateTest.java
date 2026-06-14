package com.djccnt15.northwind.sampledata;

import com.djccnt15.northwind.annotation.DevTest;
import com.djccnt15.northwind.db.entity.CompanyTypeEntity;
import com.djccnt15.northwind.db.repository.CompanyTypeRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import java.util.List;

@DevTest
@SpringBootTest
@Commit
public class CompanyTypeCreateTest {

    @Autowired private CompanyTypeRepo companyTypeRepo;

    @Test
    void createCompanyType() {
        var typeList = List.of("Customer", "Supplier");
        var typeEntityList = typeList.stream()
            .map(CompanyTypeEntity::new).toList();

        companyTypeRepo.saveAll(typeEntityList);
    }
}
