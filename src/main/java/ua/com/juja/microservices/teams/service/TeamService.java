package ua.com.juja.microservices.teams.service;

import ua.com.juja.microservices.teams.dao.TeamRepository;
import ua.com.juja.microservices.teams.entity.Team;
import ua.com.juja.microservices.teams.entity.TeamRequest;
import ua.com.juja.microservices.teams.exceptions.UserAlreadyInTeamException;
import lombok.extern.slf4j.Slf4j;
import ua.com.juja.microservices.teams.exceptions.UserInSeveralTeamsException;
import ua.com.juja.microservices.teams.exceptions.UserNotInTeamException;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * @author Andrii.Sidun
 * @author Ivan Shapovalov
 */
@Service
@Slf4j
public class TeamService {
    @Inject
    private TeamRepository teamRepository;

    public Team addTeam(TeamRequest teamRequest) {
        log.debug("Started 'addTeam' TeamRequest: {}", teamRequest);
        Date actualDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        List<String> usersInTeams = teamRepository.checkUsersActiveTeams(teamRequest.getMembers(), actualDate);
        if (usersInTeams.size() > 0) {
            log.warn("User(s) '{}' exists in a another teams", usersInTeams);
            throw new UserAlreadyInTeamException(String.format("User(s) '%s' exists in a another teams", usersInTeams.toString()));
        }
        Team team = mappingRequestToTeam(teamRequest);
        log.debug("Started 'Save team '{}'", team);
        Team savedTeam = teamRepository.saveTeam(team);
        log.info("Finished 'Save team' '{}'", team.getId());
        log.debug("Finished 'Save team '{}'", team);
        return savedTeam;
    }

    private Team mappingRequestToTeam(TeamRequest teamRequest) {
        return new Team(teamRequest.getMembers());
    }

    public Team deactivateTeam(String uuid) {
        log.debug("Started 'deactivateTeam' with uuid '{}'", uuid);
        Team team = getUserActiveTeam(uuid);
        log.debug("Finished 'usersInCurrentTeams' with uuid '{}'. Teams ", uuid, team.toString());
        team.setDeactivateDate(Date.from(Instant.now()));
        log.info("Finished 'setDectivate date' in team '{}' ", team.toString());
        log.debug("Started 'Save team in repository'. Team '{}'", team.toString());
        Team savedTeam = teamRepository.saveTeam(team);
        log.info("Team '{}' saved in repository ", team.getId());
        log.debug("Finished 'Save team in repository'. Team '{}'", team.toString());
        return savedTeam;

    }

    public Team getUserActiveTeam(String uuid) {
        log.debug("Started 'getUserActiveTeam' with uuid '{}'", uuid);
        Date actualDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        List<Team> teams = teamRepository.getUserActiveTeams(uuid, actualDate);
        log.info("Finished 'getUserActiveTeam' with uuid '{}' . Teams size '{}'", uuid, teams.size());
        if (teams.size() == 1) {
            log.debug("Finished 'getUserActiveTeam' with uuid '{}' . Teams '{}'", uuid, teams);
            return teams.get(0);
        } else if (teams.size() == 0) {
            log.warn("User <{}> is not in the team now", uuid);
            throw new UserNotInTeamException(String.format("User with uuid '%s' not in team now", uuid));
        } else {
            log.warn("User <{}> is in several teams ", uuid);
            throw new UserInSeveralTeamsException(String.format("User with uuid '%s' is in several teams now", uuid));
        }
    }
}
