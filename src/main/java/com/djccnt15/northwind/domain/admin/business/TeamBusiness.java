package com.djccnt15.northwind.domain.admin.business;

import com.djccnt15.northwind.domain.admin.converter.TeamConverter;
import com.djccnt15.northwind.domain.admin.model.TeamCreateReq;
import com.djccnt15.northwind.domain.admin.model.TeamRes;
import com.djccnt15.northwind.domain.admin.service.TeamService;
import com.djccnt15.northwind.domain.model.ListCountRes;
import com.djccnt15.northwind.global.annotation.Business;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Business
@RequiredArgsConstructor
public class TeamBusiness {
    
    private final TeamService service;
    private final TeamConverter converter;
    
    public TeamRes createTeam(TeamCreateReq request) {
        service.validateTeam(request);
        var entity = service.createTeam(request);
        return converter.toResponse(entity);
    }
    
    public ListCountRes<TeamRes> getTeams(int page, int size, String keyword) {
        var kw = "%%%s%%".formatted(keyword.trim());
        var teamList = service.getTeams(page, size, kw).stream()
            .map(converter::toResponse).toList();
        var totalCounts = service.countTeams(kw);
        return ListCountRes.<TeamRes>builder()
            .list(teamList)
            .totalCounts(totalCounts)
            .build();
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
