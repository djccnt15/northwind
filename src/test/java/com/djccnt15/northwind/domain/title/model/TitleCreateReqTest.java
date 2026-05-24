package com.djccnt15.northwind.domain.title.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.djccnt15.northwind.domain.title.validation.TitleModelConst.TITLE_NOT_BLANK_MSG;
import static com.djccnt15.northwind.domain.title.validation.TitleModelConst.TITLE_SIZE_MSG;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class TitleCreateReqTest {
    
    private static Validator validator;
    
    @BeforeAll
    static void setUp() {
        var factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    @Test
    void titleNotBlank() {
        // given
        var req = new TitleCreateReq("");
        
        // when
        var violations = validator.validate(req);
        var violationList = violations.stream()
            .map(ConstraintViolation::getMessage).toList();
        
        // then
        assertFalse(violations.isEmpty());
        assertTrue(violationList.contains(TITLE_NOT_BLANK_MSG));
    }
    
    @Test
    void titleSize() {
        // given
        var req = new TitleCreateReq("A");
        
        // when
        var violations = validator.validate(req);
        
        // then
        assertTrue(violations.isEmpty());
        
        // given
        req.setTitle("A".repeat(101));
        
        // when
        violations = validator.validate(req);
        
        // then
        assertFalse(violations.isEmpty());
        assertEquals(TITLE_SIZE_MSG, violations.iterator().next().getMessage());
    }
}
