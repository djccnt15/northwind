package com.djccnt15.northwind.domain.title.service;

import com.djccnt15.northwind.domain.title.model.TitleCreateReq;
import com.djccnt15.northwind.global.exception.exceptions.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static com.djccnt15.northwind.constants.TestConst.SYSTEM;
import static com.djccnt15.northwind.global.util.RandomUtil.getRandUuidString;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class TitleServiceTest {
    
    @Autowired private TitleService service;
    
    @Test
    void getTitle() {
        // when
        var title = service.getTitle(1L);
        
        // then
        assertNotNull(title);
        assertEquals(1L, title.getId());
        assertThrows(
            ApiException.class,
            () -> service.getTitle(-1L)
        );
    }
    
    @Test
    void validateTitle() {
        // given
        var req1 = new TitleCreateReq(SYSTEM);
        var req2 = new TitleCreateReq(getRandUuidString());
        
        // when & then
        assertThrows(
            ApiException.class,
            () -> service.validateTitle(req1)
        );
        assertDoesNotThrow(() -> service.validateTitle(req2));
        
        // given
        var ogReq = new TitleCreateReq(getRandUuidString());
        var ogTitle = service.createTitle(ogReq);
        var newReq = new TitleCreateReq(getRandUuidString());
        service.createTitle(newReq);
        
        // when & then
        assertThrows(
            ApiException.class,
            () -> service.validateTitle(ogTitle.getId(), newReq)
        );
        assertDoesNotThrow(() -> service.validateTitle(ogTitle.getId(), ogReq));
    }
    
    @Test
    void createTitle() {
        // given
        var req = new TitleCreateReq(getRandUuidString());
        
        // when
        var title = service.createTitle(req);
        
        // then
        assertNotNull(title);
        assertEquals(req.getTitle(), title.getTitle());
    }
    
    @Test
    void getAllTitles() {
        // when
        var titles = service.getTitles();
        
        // then
        assertNotNull(titles);
        assertFalse(titles.isEmpty());
    }
    
    @Test
    void updateTitle() {
        // given
        var ogReq = new TitleCreateReq(getRandUuidString());
        var ogTitle = service.createTitle(ogReq);
        var newReq = new TitleCreateReq(getRandUuidString());
        
        // when
        var updatedTitle = service.updateTitle(ogTitle, newReq);
        
        // then
        assertNotNull(updatedTitle);
        assertEquals(ogTitle.getId(), updatedTitle.getId());
        assertEquals(newReq.getTitle(), updatedTitle.getTitle());
    }
    
    @Test
    void deleteTitle() {
        // given
        var req = new TitleCreateReq(getRandUuidString());
        var title = service.createTitle(req);
        
        // when
        service.deleteTitle(title);
        
        // then
        assertThrows(
            ApiException.class,
            () -> service.getTitle(title.getId())
        );
    }
}
