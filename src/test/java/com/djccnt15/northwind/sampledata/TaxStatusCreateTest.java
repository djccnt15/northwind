package com.djccnt15.northwind.sampledata;

import com.djccnt15.northwind.annotation.DevTest;
import com.djccnt15.northwind.db.entity.TaxStatusEntity;
import com.djccnt15.northwind.db.repository.TaxStatusRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import java.util.List;

@DevTest
@SpringBootTest
@Commit
public class TaxStatusCreateTest {

    @Autowired private TaxStatusRepo taxStatusRepo;

    @Test
    void createTaxStatus() {
        var statusList = List.of("Taxable", "Tax Exempt");
        var statusEntityList = statusList.stream()
            .map(TaxStatusEntity::new).toList();

        taxStatusRepo.saveAll(statusEntityList);
    }
}
