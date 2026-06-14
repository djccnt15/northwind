package com.djccnt15.northwind.sampledata;

import com.djccnt15.northwind.annotation.DevTest;
import com.djccnt15.northwind.db.entity.CompanyEntity;
import com.djccnt15.northwind.db.entity.ProductCategoryEntity;
import com.djccnt15.northwind.db.entity.ProductEntity;
import com.djccnt15.northwind.db.entity.ProductPriceHistoryEntity;
import com.djccnt15.northwind.db.entity.ProductVendorEntity;
import com.djccnt15.northwind.db.repository.CompanyRepo;
import com.djccnt15.northwind.db.repository.ProductCategoryRepo;
import com.djccnt15.northwind.db.repository.ProductPriceHistoryRepo;
import com.djccnt15.northwind.db.repository.ProductRepo;
import com.djccnt15.northwind.db.repository.ProductVendorRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@DevTest
@SpringBootTest
@Commit
public class ProductCreateTest {

    @Autowired private ProductRepo productRepo;
    @Autowired private ProductCategoryRepo productCategoryRepo;
    @Autowired private ProductVendorRepo productVendorRepo;
    @Autowired private ProductPriceHistoryRepo productPriceHistoryRepo;
    @Autowired private CompanyRepo companyRepo;

    private record ProductSeed(
        String code, String name, String categoryCode,
        BigDecimal standardUnitCost, BigDecimal unitPrice,
        int reorderLevel, int targetLevel, int quantityPerUnit, int minimumReorderQuantity,
        boolean discontinued
    ) {}

    private static final List<ProductSeed> SEEDS = List.of(
        new ProductSeed("P101", "Chai", "BEV", bd(10), bd(18), 10, 40, 10, 20, false),
        new ProductSeed("P102", "Chang", "BEV", bd(12), bd(19), 15, 50, 24, 25, false),
        new ProductSeed("P103", "Aniseed Syrup", "CON", bd(6), bd(10), 10, 30, 12, 15, false),
        new ProductSeed("P104", "Chef Anton's Cajun Seasoning", "CON", bd(14), bd(22), 5, 20, 48, 10, false),
        new ProductSeed("P105", "Grandma's Boysenberry Spread", "CON", bd(15), bd(25), 10, 30, 12, 10, false),
        new ProductSeed("P106", "Northwoods Cranberry Sauce", "CON", bd(25), bd(40), 5, 15, 12, 5, true),
        new ProductSeed("P107", "Teatime Chocolate Biscuits", "CFN", bd(6), bd(9), 10, 40, 10, 20, false),
        new ProductSeed("P108", "Sir Rodney's Marmalade", "CFN", bd(50), bd(81), 0, 10, 30, 5, false),
        new ProductSeed("P109", "Pavlova", "CFN", bd(12), bd(17), 10, 30, 32, 10, false),
        new ProductSeed("P110", "Gumbar Gummibarchen", "CFN", bd(20), bd(31), 15, 40, 100, 20, false),
        new ProductSeed("P111", "Queso Cabrales", "DAI", bd(14), bd(21), 15, 50, 1, 20, false),
        new ProductSeed("P112", "Queso Manchego La Pastora", "DAI", bd(22), bd(38), 10, 30, 10, 10, false),
        new ProductSeed("P113", "Gorgonzola Telino", "DAI", bd(8), bd(12), 20, 60, 12, 25, false),
        new ProductSeed("P114", "Gustaf's Knackebrod", "GRC", bd(12), bd(21), 10, 30, 24, 10, false),
        new ProductSeed("P115", "Tunnbrod", "GRC", bd(6), bd(9), 15, 40, 12, 20, false),
        new ProductSeed("P116", "Mishi Kobe Niku", "MEA", bd(60), bd(97), 0, 10, 18, 5, true),
        new ProductSeed("P117", "Alice Mutton", "MEA", bd(24), bd(39), 5, 20, 20, 10, false),
        new ProductSeed("P118", "Tofu", "PRD", bd(14), bd(23), 20, 60, 40, 25, false),
        new ProductSeed("P119", "Rossle Sauerkraut", "PRD", bd(28), bd(45), 10, 30, 25, 10, false),
        new ProductSeed("P120", "Ikura", "SEA", bd(19), bd(31), 10, 30, 12, 10, false),
        new ProductSeed("P121", "Konbu", "SEA", bd(4), bd(6), 15, 40, 2, 20, false),
        new ProductSeed("P122", "Carnarvon Tigers", "SEA", bd(50), bd(62), 5, 20, 16, 10, false)
    );

    private static BigDecimal bd(int value) {
        return BigDecimal.valueOf(value).setScale(2);
    }

    // depends on: ProductCategoryCreateTest
    @Test
    @Transactional
    void createProduct() {
        var categoryList = productCategoryRepo.findAll();

        var productList = SEEDS.stream()
            .map(seed -> ProductEntity.builder()
                .code(seed.code())
                .name(seed.name())
                .standardUnitCost(seed.standardUnitCost())
                .unitPrice(seed.unitPrice())
                .reorderLevel(seed.reorderLevel())
                .targetLevel(seed.targetLevel())
                .quantityPerUnit(seed.quantityPerUnit())
                .minimumReorderQuantity(seed.minimumReorderQuantity())
                .discontinued(seed.discontinued())
                .productCategory(findCategory(categoryList, seed.categoryCode()))
                .build()).toList();

        productRepo.saveAll(productList);
    }

    // depends on: createProduct, CompanyCreateTest
    @Test
    @Transactional
    void createProductVendor() {
        var productList = productRepo.findAll();
        var vendorList = companyRepo.findAll().stream()
            .filter(it -> it.getCompanyType().getCompanyType().equals("Supplier")).toList();

        var productVendorList = new ArrayList<ProductVendorEntity>();
        for (var i = 0; i < productList.size(); i++) {
            var vendor = vendorList.get(i % vendorList.size());
            productVendorList.add(ProductVendorEntity.builder()
                .product(productList.get(i))
                .vendor(vendor)
                .build());
        }

        productVendorRepo.saveAll(productVendorList);
    }

    // depends on: createProduct
    @Test
    @Transactional
    void createProductPriceHistory() {
        var productList = productRepo.findAll();

        var priceHistoryList = productList.stream()
            .map(product -> ProductPriceHistoryEntity.builder()
                .product(product)
                .unitPrice(product.getUnitPrice())
                .standardUnitCost(product.getStandardUnitCost())
                .effectiveFrom(LocalDate.of(2025, 1, 1))
                .build()).toList();

        productPriceHistoryRepo.saveAll(priceHistoryList);
    }

    private ProductCategoryEntity findCategory(List<ProductCategoryEntity> categoryList, String code) {
        return categoryList.stream()
            .filter(it -> it.getCode().equals(code))
            .findFirst().orElseThrow();
    }
}
