package com.djccnt15.northwind.domain.admin.business;

import com.djccnt15.northwind.db.entity.TitleEntity;
import com.djccnt15.northwind.domain.title.converter.TitleConverter;
import com.djccnt15.northwind.domain.title.model.TitleCreateReq;
import com.djccnt15.northwind.domain.title.model.TitleRes;
import com.djccnt15.northwind.domain.title.service.TitleService;
import com.djccnt15.northwind.global.annotation.Business;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Business
@RequiredArgsConstructor
public class AdminTitleBusiness {
    
    private final TitleService service;
    private final TitleConverter converter;
    
    public TitleRes createTitle(TitleCreateReq request) {
        service.validateTitle(request);
        var entity = service.createTitle(request);
        return converter.toResponse(entity);
    }
    
    public List<String> getAllTitles() {
        var titles = service.getAllTitles();
        return titles.stream().map(TitleEntity::getTitle).toList();
    }
    
    public TitleRes updateTitle(Long id, TitleCreateReq request) {
        service.validateTitle(id, request);
        var entity = service.getTitle(id);
        service.updateTitle(entity, request);
        return converter.toResponse(entity);
    }
    
    public void deleteTitle(Long id) {
        var entity = service.getTitle(id);
        service.deleteTitle(entity);
    }
}
