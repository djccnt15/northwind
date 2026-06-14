package com.djccnt15.northwind.sampledata;

import com.djccnt15.northwind.annotation.DevTest;
import com.djccnt15.northwind.db.entity.ProductCategoryEntity;
import com.djccnt15.northwind.db.repository.ProductCategoryRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import java.util.List;

@DevTest
@SpringBootTest
@Commit
public class ProductCategoryCreateTest {

    @Autowired private ProductCategoryRepo productCategoryRepo;

    @Test
    void createProductCategory() {
        var categoryList = List.of(
            ProductCategoryEntity.builder()
                .name("Beverages").code("BEV")
                .description("Soft drinks, coffees, teas, beers, and ales")
                .build(),
            ProductCategoryEntity.builder()
                .name("Condiments").code("CON")
                .description("Sweet and savory sauces, relishes, spreads, and seasonings")
                .build(),
            ProductCategoryEntity.builder()
                .name("Confections").code("CFN")
                .description("Desserts, candies, and sweet breads")
                .build(),
            ProductCategoryEntity.builder()
                .name("Dairy Products").code("DAI")
                .description("Cheeses")
                .build(),
            ProductCategoryEntity.builder()
                .name("Grains/Cereals").code("GRC")
                .description("Breads, crackers, pasta, and cereal")
                .build(),
            ProductCategoryEntity.builder()
                .name("Meat/Poultry").code("MEA")
                .description("Prepared meats")
                .build(),
            ProductCategoryEntity.builder()
                .name("Produce").code("PRD")
                .description("Dried fruit and bean curd")
                .build(),
            ProductCategoryEntity.builder()
                .name("Seafood").code("SEA")
                .description("Seaweed and fish")
                .build()
        );

        productCategoryRepo.saveAll(categoryList);
    }
}
