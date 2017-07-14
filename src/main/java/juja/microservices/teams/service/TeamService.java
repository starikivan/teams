package juja.microservices.teams.service;

import juja.microservices.teams.dao.TeamRepository;
import juja.microservices.teams.entity.Team;
import juja.microservices.teams.entity.TeamRequest;
import juja.microservices.teams.exceptions.UserExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import juja.microservices.teams.exceptions.UserInSeveralTeamsException;
import juja.microservices.teams.exceptions.UserNotInTeamException;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Andrii.Sidun
 * @author Ivan Shapovalov
 */
@Service
public class TeamService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private TeamRepository teamRepository;

    public Team addTeam(TeamRequest teamRequest) {
        logger.debug("Start TeamService.addTeam. Team: {}", teamRequest);
        Set<String> usersInTeams = usersInCurrentTeams(teamRequest);
        if (usersInTeams.size() > 0) {
            logger.warn("User(s) '{}' exists in a team",usersInTeams);
            throw new UserExistsException(String.format("User(s) '%s' exists in a current team", usersInTeams.toString()));
        }
        Team team = mappingRequestToTeam(teamRequest);
        teamRepository.saveTeam(team);
        logger.info("Added new Team with parameters '{}'", team);
        logger.debug("Finish TeamService.addTeam. newTeamId: {}", team.getId());
        return team;
    }

    private Team mappingRequestToTeam(TeamRequest teamRequest) {
        return new Team(teamRequest.getMembers());
    }

    private Set<String> usersInCurrentTeams(TeamRequest team) {
        logger.debug("Start usersInCurrentTeams()");
        Set<String> usersInTeams = new HashSet();
        team.getMembers().stream()
                .forEach(uuid -> {
                    List<Team> teams = teamRepository.getUserTeams(uuid);
                    if (teams.size() != 0) {
                        usersInTeams.add(uuid);
                    }
                });
        logger.info("Result execution usersInCurrentTeams is {}", usersInTeams.size());
        logger.debug("Finish usersInCurrentTeams()");
        return usersInTeams;
    }

    public Team deactivateTeam(String uuid) {
        List<Team> teams = getUserTeams(uuid);
        if (teams.size() == 1) {
            Team team = teams.get(0);
            team.setDeactivateDate(LocalDate.now());
            return teamRepository.saveTeam(team);
        } else if (teams.size() == 0) {
            logger.warn("User <{}> is not in the same team ", uuid);
            throw new UserNotInTeamException(String.format("User with uuid '%s' not in team now", uuid));
        } else {
            logger.warn("User <{}> is in several teams ", uuid);
            throw new UserInSeveralTeamsException(String.format("User with uuid '%s' is in several teams now", uuid));
        }
    }

    public List<Team> getUserTeams(String uuid) {
        logger.debug("Send request to repository: get teams of user <{}> for current date", uuid);
        List<Team> teams = teamRepository.getUserTeams(uuid);
        logger.info("Received list of teams, size = {}", teams.size());
        logger.debug("Full list of teams <{}>", teams.toString());
        return teams;
    }
}
