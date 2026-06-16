package com.djccnt15.northwind.domain.lang.service;

import com.djccnt15.northwind.db.entity.SupportedLangEntity;
import com.djccnt15.northwind.db.repository.SupportedLangRepo;
import com.djccnt15.northwind.global.exception.exceptions.ApiException;
import com.djccnt15.northwind.global.message.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.djccnt15.northwind.global.code.StatusCode.NOT_FOUND;
import static com.djccnt15.northwind.domain.lang.validation.LangErrorConst.NOT_FOUND_ERR_MSG;

@Slf4j
@Service
@RequiredArgsConstructor
public class LangService {

    public static final String DEFAULT_LANG = "en";

    private final SupportedLangRepo repository;
    private final MessageUtil messageUtil;

    public List<SupportedLangEntity> getLangs() {
        return repository.findAll(Sort.by("lang"));
    }

    public SupportedLangEntity getLang(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ApiException(NOT_FOUND, messageUtil.getMessage(NOT_FOUND_ERR_MSG)));
    }

    public SupportedLangEntity getDefaultLang() {
        return repository.findFirstByLang(DEFAULT_LANG)
            .orElseThrow(() -> new ApiException(NOT_FOUND, messageUtil.getMessage(NOT_FOUND_ERR_MSG)));
    }

    public SupportedLangEntity getLangOrDefault(String langCode) {
        if (langCode == null || langCode.isBlank()) {
            return getDefaultLang();
        }
        return repository.findFirstByLang(langCode.trim())
            .orElseGet(this::getDefaultLang);
    }
}
