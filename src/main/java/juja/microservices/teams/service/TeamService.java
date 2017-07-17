package juja.microservices.teams.service;

import juja.microservices.teams.dao.TeamRepository;
import juja.microservices.teams.entity.Team;
import juja.microservices.teams.entity.TeamRequest;
import juja.microservices.teams.exceptions.UserExistsException;
import lombok.extern.slf4j.Slf4j;
import juja.microservices.teams.exceptions.UserInSeveralTeamsException;
import juja.microservices.teams.exceptions.UserNotInTeamException;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        Set<String> usersInTeams = usersInCurrentTeams(teamRequest);
        if (usersInTeams.size() > 0) {
            log.warn("User(s) '{}' exists in a another teams",usersInTeams);
            throw new UserExistsException(String.format("User(s) '%s' exists in a another teams", usersInTeams.toString()));
        }
        Team team = mappingRequestToTeam(teamRequest);
        log.debug("Started 'Save team '{}'", team);
        Team savedTeam=teamRepository.saveTeam(team);
        log.info("Finished 'Save team' '{}'", team.getId());
        log.debug("Finished 'Save team '{}'", team);
        return savedTeam;
    }

    private Team mappingRequestToTeam(TeamRequest teamRequest) {
        return new Team(teamRequest.getMembers());
    }

    private Set<String> usersInCurrentTeams(TeamRequest teamRequest) {
        log.debug("Started 'usersInCurrentTeams' with teamRequest '{}'",teamRequest);
        Set<String> usersInTeams = new HashSet<>();
        teamRequest.getMembers()
                .forEach(uuid -> {
                    List<Team> teams = teamRepository.getUserTeams(uuid);
                    if (teams.size() != 0) {
                        usersInTeams.add(uuid);
                    }
                });
        log.info("Finished 'usersInCurrentTeams' with teamRequest '{}'",teamRequest);
        return usersInTeams;
    }

    public Team deactivateTeam(String uuid) {
        log.debug("Started 'deactivateTeam' with uuid '{}'",uuid);
        List<Team> teams = getUserTeams(uuid);
        log.info("Finished 'usersInCurrentTeams' with uuid '{}. Teams size '",uuid,teams.size());
        log.debug("Finished 'usersInCurrentTeams' with uuid '{}'. Teams ",uuid,teams.toString());
        if (teams.size() == 1) {
            Team team = teams.get(0);
            team.setDeactivateDate(LocalDateTime.now());
            return teamRepository.saveTeam(team);
        } else if (teams.size() == 0) {
            log.warn("User <{}> is not in the team now", uuid);
            throw new UserNotInTeamException(String.format("User with uuid '%s' not in team now", uuid));
        } else {
            log.warn("User <{}> is in several teams ", uuid);
            throw new UserInSeveralTeamsException(String.format("User with uuid '%s' is in several teams now", uuid));
        }
    }

    private List<Team> getUserTeams(String uuid) {
        log.debug("Started 'getUserTeams' with uuid '{}'",uuid);
        List<Team> teams = teamRepository.getUserTeams(uuid);
        log.info("Finished 'getUserTeams' with uuid '{}' . Teams size '{}'",uuid,teams.size());
        log.debug("Finished 'getUserTeams' with uuid '{}' . Teams '{}'",uuid,teams);
        return teams;
    }
}
