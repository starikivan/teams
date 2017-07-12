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
        List<Team> teams = teamRepository.getUserTeams(uuid);
        if (teams.size() == 1) {
            Team team = teams.get(0);
            team.setDismissDate(LocalDateTime.now());
            return teamRepository.saveTeam(team);
        } else if (teams.size() == 0) {
            throw new UserNotInTeamException(String.format("User with uuid '%s' not in team now", uuid));
        } else {
            throw new UserInSeveralTeamsException(String.format("User with uuid '%s' is in several teams now", uuid));
        }
    }
}
