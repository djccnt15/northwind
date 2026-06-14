package com.djccnt15.northwind.sampledata;

import com.djccnt15.northwind.annotation.DevTest;
import com.djccnt15.northwind.db.entity.CompanyEntity;
import com.djccnt15.northwind.db.entity.CompanyTypeEntity;
import com.djccnt15.northwind.db.entity.ContactEntity;
import com.djccnt15.northwind.db.entity.TaxStatusEntity;
import com.djccnt15.northwind.db.entity.embaddable.AddressEmbed;
import com.djccnt15.northwind.db.repository.CompanyRepo;
import com.djccnt15.northwind.db.repository.CompanyTypeRepo;
import com.djccnt15.northwind.db.repository.ContactRepo;
import com.djccnt15.northwind.db.repository.TaxStatusRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@DevTest
@SpringBootTest
@Commit
public class CompanyCreateTest {

    @Autowired private CompanyRepo companyRepo;
    @Autowired private ContactRepo contactRepo;
    @Autowired private CompanyTypeRepo companyTypeRepo;
    @Autowired private TaxStatusRepo taxStatusRepo;

    // depends on: CompanyTypeCreateTest, TaxStatusCreateTest
    @Test
    @Transactional
    void createCompany() {
        var customerType = findCompanyType("Customer");
        var supplierType = findCompanyType("Supplier");
        var taxable = findTaxStatus("Taxable");
        var taxExempt = findTaxStatus("Tax Exempt");

        var companyList = new ArrayList<CompanyEntity>();

        // customers
        companyList.add(buildCompany("Acme Corp", "+1-503-555-0100", "https://acme.example.com",
            customerType, taxable, "100 Market St", "Portland", "OR", "97201", "USA"));
        companyList.add(buildCompany("Globex Corporation", "+1-212-555-0101", "https://globex.example.com",
            customerType, taxable, "200 5th Ave", "New York", "NY", "10010", "USA"));
        companyList.add(buildCompany("Initech", "+1-512-555-0102", "https://initech.example.com",
            customerType, taxable, "300 Congress Ave", "Austin", "TX", "78701", "USA"));
        companyList.add(buildCompany("Umbrella Corporation", "+1-313-555-0103", "https://umbrella.example.com",
            customerType, taxExempt, "400 Woodward Ave", "Detroit", "MI", "48226", "USA"));
        companyList.add(buildCompany("Stark Industries", "+1-310-555-0104", "https://stark.example.com",
            customerType, taxable, "500 Sunset Blvd", "Los Angeles", "CA", "90028", "USA"));
        companyList.add(buildCompany("Wayne Enterprises", "+1-312-555-0105", "https://wayne.example.com",
            customerType, taxable, "600 Wacker Dr", "Chicago", "IL", "60601", "USA"));
        companyList.add(buildCompany("Wonka Industries", "+1-206-555-0106", "https://wonka.example.com",
            customerType, taxExempt, "700 Pike St", "Seattle", "WA", "98101", "USA"));
        companyList.add(buildCompany("Soylent Corp", "+1-617-555-0107", "https://soylent.example.com",
            customerType, taxable, "800 Boylston St", "Boston", "MA", "02199", "USA"));

        // suppliers (also used as shippers)
        companyList.add(buildCompany("Fast Shipping Inc", "+1-901-555-0200", "https://fastshipping.example.com",
            supplierType, taxable, "10 Logistics Way", "Memphis", "TN", "38103", "USA"));
        companyList.add(buildCompany("Pacific Coffee Traders", "+1-808-555-0201", "https://pacificcoffee.example.com",
            supplierType, taxable, "20 Harbor Rd", "Honolulu", "HI", "96813", "USA"));
        companyList.add(buildCompany("Grandma Kelly's Homestead", "+1-515-555-0202", "https://grandmakellys.example.com",
            supplierType, taxExempt, "30 Farm Rd", "Des Moines", "IA", "50309", "USA"));
        companyList.add(buildCompany("Tokyo Traders", "+81-3-5555-0203", "https://tokyotraders.example.com",
            supplierType, taxable, "1-1 Marunouchi", "Tokyo", null, "100-0005", "Japan"));
        companyList.add(buildCompany("Mayumi's", "+81-6-5555-0204", "https://mayumis.example.com",
            supplierType, taxable, "2-2 Namba", "Osaka", null, "542-0076", "Japan"));
        companyList.add(buildCompany("New England Seafood Cannery", "+1-207-555-0205", "https://neseafood.example.com",
            supplierType, taxExempt, "40 Wharf St", "Portland", "ME", "04101", "USA"));

        companyRepo.saveAll(companyList);

        var contactNames = List.of(
            "Maria Anders", "Ana Trujillo", "Antonio Moreno", "Thomas Hardy",
            "Christina Berglund", "Hanna Moos", "Frederique Citeaux", "Martin Sommer",
            "Laurence Lebihan", "Elizabeth Lincoln", "Victoria Ashworth", "Patricio Simpson",
            "Yoshi Tannamuri", "Howard Snyder"
        );

        var contactList = new ArrayList<ContactEntity>();
        for (var i = 0; i < companyList.size(); i++) {
            var company = companyList.get(i);
            var name = contactNames.get(i % contactNames.size()).split(" ");

            contactList.add(buildContact(company, name[0], name[1],
                "Purchasing Manager", "%s.%s@%s".formatted(name[0], name[1], emailDomain(company)).toLowerCase(),
                "+1-555-010-%04d".formatted(i)));
        }

        contactRepo.saveAll(contactList);
    }

    private CompanyEntity buildCompany(
        String name, String businessPhone, String website,
        CompanyTypeEntity companyType, TaxStatusEntity taxStatus,
        String address, String city, String region, String zipCode, String country
    ) {
        return CompanyEntity.builder()
            .name(name)
            .businessPhone(businessPhone)
            .website(website)
            .companyType(companyType)
            .taxStatus(taxStatus)
            .address(AddressEmbed.builder()
                .address(address)
                .city(city)
                .region(region)
                .zipCode(zipCode)
                .country(country)
                .build())
            .build();
    }

    private ContactEntity buildContact(
        CompanyEntity company, String firstName, String lastName, String jobTitle, String email, String primaryPhone
    ) {
        return ContactEntity.builder()
            .firstName(firstName)
            .lastName(lastName)
            .jobTitle(jobTitle)
            .email(email)
            .primaryPhone(primaryPhone)
            .company(company)
            .build();
    }

    private String emailDomain(CompanyEntity company) {
        return company.getName().toLowerCase()
            .replaceAll("[^a-z0-9]+", "") + ".example.com";
    }

    private CompanyTypeEntity findCompanyType(String name) {
        return companyTypeRepo.findAll().stream()
            .filter(it -> it.getCompanyType().equals(name))
            .findFirst().orElseThrow();
    }

    private TaxStatusEntity findTaxStatus(String name) {
        return taxStatusRepo.findAll().stream()
            .filter(it -> it.getStatus().equals(name))
            .findFirst().orElseThrow();
    }
}
