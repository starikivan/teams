package ua.com.juja.microservices.teams.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.com.juja.microservices.teams.dao.TeamRepository;
import ua.com.juja.microservices.teams.entity.Team;
import ua.com.juja.microservices.teams.entity.TeamRequest;
import ua.com.juja.microservices.teams.exceptions.UserAlreadyInTeamException;
import ua.com.juja.microservices.teams.exceptions.UserInSeveralTeamsException;
import ua.com.juja.microservices.teams.exceptions.UserNotInTeamException;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Andrii.Sidun
 * @author Ivan Shapovalov
 */
@Service
@Slf4j
public class TeamService {

    private static final int TEAM_SIZE = 4;

    @Inject
    private TeamRepository teamRepository;

    public Team activateTeam(TeamRequest teamRequest) {
        if (teamRequest == null || teamRequest.getMembers().size() != TEAM_SIZE) {
            throw new IllegalArgumentException(String.format("Team Request must contain '%s' members", TEAM_SIZE));
        }
        log.debug("Started 'activateTeam' TeamRequest: {}", teamRequest);
        Date actualDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        List<String> usersInTeams = teamRepository.checkUsersActiveTeams(teamRequest.getMembers(), actualDate);
        if (usersInTeams.size() > 0) {
            log.warn("User(s) '{}' exist(s) in a another teams", usersInTeams);
            throw new UserAlreadyInTeamException(String.format("User(s) '#%s#' exist(s) in another teams",
                    usersInTeams.stream().collect(Collectors.joining(","))));
        }
        Team team = new Team(teamRequest.getMembers());
        log.debug("Started 'Save team '{}'", team);
        Team savedTeam = teamRepository.saveTeam(team);
        log.debug("Finished 'Save team '{}'", team);
        log.info("Finished 'Save team' '{}'", team.getId());
        return savedTeam;
    }

    public Team deactivateTeam(String uuid) {
        log.debug("Started 'deactivateTeam' with uuid '{}'", uuid);
        Team team = getUserActiveTeam(uuid);
        log.debug("Finished 'getUserActiveTeams' with uuid '{}'. Teams '{}'", uuid, team.toString());
        Date deactivateDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        team.setDeactivateDate(deactivateDate);
        log.debug("Started 'Save team in repository'. Team '{}'", team.toString());
        Team savedTeam = teamRepository.saveTeam(team);
        log.debug("Finished 'Save team in repository'. Team '{}'", team.toString());
        log.info("Team '{}' saved in repository ", team.getId());
        return savedTeam;
    }

    public Team getUserActiveTeam(String uuid) {
        Date actualDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        log.debug("Started 'getUserActiveTeam' with uuid '{}' on date '{}'", uuid, actualDate);
        List<Team> teams = teamRepository.getUserActiveTeams(uuid, actualDate);
        log.info("Finished 'getUserActiveTeam' with uuid '{}'. Teams size '{}'", uuid, teams.size());
        if (teams.size() == 1) {
            log.debug("Finished 'getUserActiveTeam' with uuid '{}' on date '{}' . Teams '{}'", uuid, actualDate, teams);
            return teams.get(0);
        } else if (teams.size() == 0) {
            log.warn("User <{}> is not in the team on date '{}'", uuid, actualDate);
            throw new UserNotInTeamException(String.format("User with uuid '%s' not in team now", uuid));
        } else {
            log.warn("User <{}> is in several teams on date '{}'", uuid, actualDate);
            throw new UserInSeveralTeamsException(String.format("User with uuid '%s' is in several teams now", uuid));
        }
    }

    public List<Team> getAllActiveTeams() {
        Date actualDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        log.debug("Started 'getAllActiveTeams' on date '{}'", actualDate);
        List<Team> teams = teamRepository.getAllActiveTeams(actualDate);
        log.debug("Finished 'getAllActiveTeams'. Teams '{}'", teams);
        log.info("Finished 'getAllActiveTeams'. Teams size '{}'", teams.size());
        return teams;
    }
}
