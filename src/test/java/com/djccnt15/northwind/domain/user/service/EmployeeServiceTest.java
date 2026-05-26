package com.djccnt15.northwind.domain.user.service;

import com.djccnt15.northwind.domain.user.model.EmployeeReq;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class EmployeeServiceTest {
    
    @Autowired EmployeeService service;
    
    @Test
    @Transactional
    void createEmployee() {
        // given
        var request = new EmployeeReq(
            "John",
            "Doe",
            null,
            "Software Engineer",
            null,
            null,
            null,
            "Mr.",
            null,
            LocalDate.now(),
            null,
            null,
            null,
            null,
            null,
            null
        );
        var testUser = new AppUserEntity();
        
        // when
        var employee = service.createEmployee(request, testUser);
        
        // then
        assertNotNull(employee);
        
        // given
        var errorRequest = new EmployeeReq(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );
        var errorUser = new AppUserEntity();
        
        // when & then
        assertThrows(
            ConstraintViolationException.class,
            () -> service.createEmployee(errorRequest, errorUser)
        );
    }
}
