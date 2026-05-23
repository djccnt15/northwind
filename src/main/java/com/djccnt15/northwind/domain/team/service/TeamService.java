package com.djccnt15.northwind.domain.team.service;

import com.djccnt15.northwind.db.entity.AppUserEntity;
import com.djccnt15.northwind.db.entity.TeamEntity;
import com.djccnt15.northwind.db.repository.TeamRepo;
import com.djccnt15.northwind.domain.team.converter.TeamConverter;
import com.djccnt15.northwind.domain.team.model.TeamCreateReq;
import com.djccnt15.northwind.global.exception.exceptions.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        if (repository.existsByName(request.getName())) {
            throw new ApiException(BAD_REQUEST, "Team name already exists");
        }
    }
    
    public void validateTeam(Long id, TeamCreateReq request) {
        if (repository.existsByNameAndIdNot(request.getName(), id)) {
            throw new ApiException(BAD_REQUEST, "Team name already exists");
        }
    }
    
    public TeamEntity createTeam(TeamCreateReq request) {
        var team = converter.toEntity(request);
        return repository.save(team);
    }
    
    public Page<TeamEntity> getTeams(String kw, Pageable pageable) {
        return repository.findByNameLike(kw, pageable);
    }
    
    public List<TeamEntity> getTeams() {
        return repository.findAll();
    }
    
    public TeamEntity getTeam(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ApiException(BAD_REQUEST, "Team not found"));
    }
    
    public TeamEntity getTeam(String name) {
        return repository.findFirstByName(name)
            .orElseThrow(() -> new ApiException(BAD_REQUEST, "Team not found"));
    }
    
     public void updateTeam(TeamEntity team, TeamCreateReq request) {
        team.setName(request.getName());
        repository.save(team);
    }
    
    public void deleteTeam(TeamEntity team) {
        repository.delete(team);
    }
    
    public void addMember(TeamEntity team, AppUserEntity user) {
        team.addMember(user);
        repository.save(team);
    }
    
     public void removeMember(TeamEntity team, AppUserEntity user) {
         team.removeMember(user);
         repository.save(team);
    }
}
