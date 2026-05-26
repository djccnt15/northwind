package com.djccnt15.northwind.domain.user.service;

import com.djccnt15.northwind.db.entity.AppUserEntity;
import com.djccnt15.northwind.db.repository.AppUserRepo;
import com.djccnt15.northwind.domain.user.model.EmployeeReq;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static com.djccnt15.northwind.global.util.RandomUtil.getRandUuidString;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class EmployeeServiceTest {
    
    @Autowired EmployeeService service;
    @Autowired AppUserRepo userRepo;
    
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
    
    @Test
    void getEmployee() {
        // given
        var user = userRepo.findById(1L).orElseThrow();
        
        // when
        var employee = service.getEmployee(user);
        
        // then
        assertTrue(employee.isEmpty());
    }
    
    @Test
    void updateEmployee() {
        // given
        var user = userRepo.findById(1L).orElseThrow();
        var request = new EmployeeReq(
            getRandUuidString(),
            getRandUuidString(),
            null,
            getRandUuidString(),
            null,
            null,
            null,
            getRandUuidString(),
            null,
            LocalDate.now(),
            null,
            null,
            null,
            null,
            null,
            null
        );
        var employee = service.createEmployee(request, user);
        
        var updateRequest = new EmployeeReq(
            getRandUuidString(),
            getRandUuidString(),
            null,
            getRandUuidString(),
            null,
            null,
            null,
            getRandUuidString(),
            null,
            LocalDate.now(),
            null,
            null,
            null,
            null,
            null,
            null
        );
        
        // when
        var updatedEmployee = service.updateEmployee(employee, updateRequest);
        
        // then
        assertNotNull(updatedEmployee);
        assertEquals(updatedEmployee.getFirstName(), updateRequest.getFirstName());
        assertEquals(updatedEmployee.getLastName(), updateRequest.getLastName());
        assertEquals(updatedEmployee.getJobTitle(), updateRequest.getJobTitle());
        assertEquals(updatedEmployee.getTitleOfCourtesy(), updateRequest.getTitleOfCourtesy());
    }
}
