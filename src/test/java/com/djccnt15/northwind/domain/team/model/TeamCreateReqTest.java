package com.djccnt15.northwind.domain.team.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.djccnt15.northwind.domain.team.validation.TeamModelConst.NAME_LENGTH_MSG;
import static com.djccnt15.northwind.domain.team.validation.TeamModelConst.NAME_NOT_BLANK_MSG;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class TeamCreateReqTest {
    
    private static Validator validator;
    
    @BeforeAll
    static void setUp() {
        var factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    @Test
    void nameNotBlank() {
        // given
        var req = new TeamCreateReq("");
        
        // when
        var violations = validator.validate(req);
        var violationList = violations.stream()
            .map(ConstraintViolation::getMessage).toList();
        
        // then
        assertFalse(violations.isEmpty());
        assertTrue(violationList.contains(NAME_NOT_BLANK_MSG));
    }
    
    @Test
    void nameLength() {
        // given
        var req = new TeamCreateReq("A");
        
        // when
        var violations = validator.validate(req);
        
        // then
        assertTrue(violations.isEmpty());
        
        // given
        req.setName("A".repeat(51));
        
        // when
        violations = validator.validate(req);
        
        // then
        assertFalse(violations.isEmpty());
        assertEquals(NAME_LENGTH_MSG, violations.iterator().next().getMessage());
    }
}
