package juja.microservices.teams.service;

import juja.microservices.teams.dao.TeamRepository;
import juja.microservices.teams.entity.Team;
import juja.microservices.teams.exceptions.TeamsException;
import juja.microservices.teams.exceptions.UserInSeveralTeamsException;
import juja.microservices.teams.exceptions.UserNotInTeamException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Ivan Shapovalov
 */
@Service
public class TeamService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private TeamRepository teamRepository;

    public Team dismissTeam(String uuid) {
        logger.debug("Send request to repository: get teams of user <{}> for current date", uuid);
        List<Team> teams = teamRepository.getUserTeams(uuid);
        logger.info("Received list of teams, size = {}", teams.size());
        logger.debug("Full list of teams <{}>", teams.toString());
        if (teams.size() == 1) {
            Team team = teams.get(0);
            team.setDismissDate(LocalDateTime.now());
            return teamRepository.saveTeam(team);
        } else if (teams.size() == 0) {
            logger.warn("User <{}> is not in the same team ", uuid);
            throw new UserNotInTeamException(String.format("User with uuid '%s' not in team now", uuid));
        } else {
            logger.warn("User <{}> is in several teams ", uuid);
            throw new UserInSeveralTeamsException(String.format("User with uuid '%s' is in several teams now", uuid));
        }
    }
}
