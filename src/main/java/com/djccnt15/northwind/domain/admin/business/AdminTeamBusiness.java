package com.djccnt15.northwind.domain.admin.business;

import com.djccnt15.northwind.domain.team.converter.TeamConverter;
import com.djccnt15.northwind.domain.team.model.TeamCreateReq;
import com.djccnt15.northwind.domain.team.model.TeamRes;
import com.djccnt15.northwind.domain.team.service.TeamService;
import com.djccnt15.northwind.global.annotation.Business;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Slf4j
@Business
@RequiredArgsConstructor
public class AdminTeamBusiness {
    
    private final TeamService service;
    private final TeamConverter converter;
    
    public TeamRes createTeam(TeamCreateReq request) {
        service.validateTeam(request);
        var entity = service.createTeam(request);
        return converter.toResponse(entity);
    }
    
    public Page<TeamRes> getTeams(int page, int size, String keyword) {
        var kw = "%%%s%%".formatted(keyword.trim());
        var pageable = PageRequest.of(page, size, Sort.by("id"));
        var teams = service.getTeams(kw, pageable);
        return teams.map(converter::toResponse);
    }
    
    public TeamRes updateTeam(Long id, TeamCreateReq request) {
        service.validateTeam(id, request);
        var entity = service.getTeam(id);
        service.updateTeam(entity, request);
        return converter.toResponse(entity);
    }
    
    public void deleteTeam(Long id) {
        var entity = service.getTeam(id);
        service.deleteTeam(entity);
    }
}
