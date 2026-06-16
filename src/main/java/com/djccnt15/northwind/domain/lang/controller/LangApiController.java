package com.djccnt15.northwind.domain.lang.controller;

import com.djccnt15.northwind.domain.lang.converter.LangConverter;
import com.djccnt15.northwind.domain.lang.model.LangRes;
import com.djccnt15.northwind.domain.lang.service.LangService;
import com.djccnt15.northwind.global.api.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.djccnt15.northwind.global.constants.RouteConst.API_V1;

@Slf4j
@RestController
@RequestMapping(API_V1 + "/lang")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class LangApiController {

    private final LangService langService;
    private final LangConverter langConverter;

    @GetMapping
    public ResponseEntity<Api<List<LangRes>>> getLangs() {
        var response = langService.getLangs().stream()
            .map(langConverter::toResponse).toList();
        return ResponseEntity.ok(Api.OK(response));
    }
}
