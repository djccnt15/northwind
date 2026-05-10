package com.djccnt15.northwind.domain.team.service;

import com.djccnt15.northwind.db.entity.TeamEntity;
import com.djccnt15.northwind.db.repository.TeamRepo;
import com.djccnt15.northwind.domain.team.converter.TeamConverter;
import com.djccnt15.northwind.domain.team.model.TeamCreateReq;
import com.djccnt15.northwind.global.exception.exceptions.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.djccnt15.northwind.global.code.StatusCode.BAD_REQUEST;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamService {
    
    private final TeamRepo repository;
    private final TeamConverter converter;
    
    public void validateTeam(TeamCreateReq request) {
        repository.findByName(request.getName()).ifPresent(team -> {
            throw new ApiException(BAD_REQUEST, "Team name already exists: " + request.getName());
        });
    }
    
    public void validateTeam(Long id, TeamCreateReq request) {
        repository.findByName(request.getName()).ifPresent(e -> {
            if (!e.getId().equals(id)) {
                throw new ApiException(BAD_REQUEST, "Team name already exists: " + request.getName());
            }
        });
    }
    
    public TeamEntity createTeam(TeamCreateReq request) {
        var team = converter.toEntity(request);
        return repository.save(team);
    }
    
    public List<TeamEntity> getTeams(int page, int size, String kw) {
        var pageable = PageRequest.of(page, size, Sort.by("id"));
        return repository.findByNameLike(kw, pageable);
    }
    
    public Integer countTeams(String kw) {
        return repository.countByNameLike(kw);
    }
    
    public TeamEntity getTeam(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ApiException(BAD_REQUEST, "Team not found: " + id));
    }
    
     public void updateTeam(TeamEntity team, TeamCreateReq request) {
        team.setName(request.getName());
        repository.save(team);
    }
    
    public void deleteTeam(TeamEntity team) {
        repository.delete(team);
    }
}
