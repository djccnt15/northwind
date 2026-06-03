package com.djccnt15.northwind.domain.title.service;

import com.djccnt15.northwind.db.entity.TitleEntity;
import com.djccnt15.northwind.db.repository.TitleRepo;
import com.djccnt15.northwind.domain.title.converter.TitleConverter;
import com.djccnt15.northwind.domain.title.model.TitleCreateReq;
import com.djccnt15.northwind.global.exception.exceptions.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.djccnt15.northwind.global.code.StatusCode.BAD_REQUEST;
import static com.djccnt15.northwind.global.code.StatusCode.NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class TitleService {
    
    private final TitleRepo repository;
    private final TitleConverter converter;
    
    public TitleEntity getTitle(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ApiException(NOT_FOUND, "Title not found"));
    }
    
    public TitleEntity getTitle(String title) {
        return repository.findFirstByTitle(title)
            .orElseThrow(() -> new ApiException(NOT_FOUND, "Title not found"));
    }
    
    public void validateTitle(TitleCreateReq request) {
        if (repository.existsByTitle(request.getTitle())) {
            throw new ApiException(BAD_REQUEST, "Title already exists");
        }
    }
    
    public void validateTitle(Long id, TitleCreateReq request) {
        if (repository.existsByTitleAndIdNot(request.getTitle(), id)) {
            throw new ApiException(BAD_REQUEST, "Title already exists");
        }
    }
    
    public TitleEntity createTitle(TitleCreateReq request) {
        var entity = converter.toEntity(request);
        repository.save(entity);
        return entity;
    }
    
    public Page<TitleEntity> getTitles(String kw, Pageable pageable) {
        return repository.findByTitleLike(kw, pageable);
    }
    
    public List<TitleEntity> getTitles() {
        return repository.findAll(Sort.by("title"));
    }
    
    public TitleEntity updateTitle(TitleEntity entity, TitleCreateReq request) {
        entity.setTitle(request.getTitle());
        repository.save(entity);
        return entity;
    }
    
    public void deleteTitle(TitleEntity entity) {
        repository.delete(entity);
    }
}
