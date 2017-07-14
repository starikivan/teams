package juja.microservices.teams.service;

import juja.microservices.teams.dao.TeamRepository;
import juja.microservices.teams.entity.Team;
import juja.microservices.teams.entity.TeamRequest;
import juja.microservices.teams.exceptions.TeamUserExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * @author Andrii.Sidun
 * @author Ivan Shapovalov
 */
@Service
public class TeamService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private TeamRepository teamRepository;

    public String addTeam(TeamRequest teamRequest){
        logger.debug("Start TeamService.addTeam. Team: {}", teamRequest);
        if (isAnyBodyInOtherTeam(teamRequest)){
            logger.warn("Some user(s) exists in a team");
            throw new TeamUserExistsException("Some user(s) exists in a team");
        }
        Team team = mappingRequestToTeam(teamRequest);
        String newTeamId = teamRepository.add(team);
        logger.info("Added new Team with parameters '{}'", team);
        logger.debug("Finish TeamService.addTeam. newTeamId: {}", newTeamId);
        return newTeamId;
    }

    private Team mappingRequestToTeam(TeamRequest teamRequest) {
        return new Team(teamRequest.getFrom(), teamRequest.getUuidOne(), teamRequest.getUuidTwo(),
                teamRequest.getUuidThree(), teamRequest.getUuidFour());
    }

    private boolean isAnyBodyInOtherTeam(TeamRequest team){
        logger.debug("Start isAnyBodyInOtherTeam()");
        boolean result = false;
        if (teamRepository.isUserInOtherTeam(team.getUuidOne()) ||
                teamRepository.isUserInOtherTeam(team.getUuidTwo()) ||
                teamRepository.isUserInOtherTeam(team.getUuidThree()) ||
                teamRepository.isUserInOtherTeam(team.getUuidFour())) {
            result = true;
        }
        logger.info("Result execution isAnyBodyInOtherTeam is {}", result);
        logger.debug("Finish isAnyBodyInOtherTeam()");
        return result;
    }

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
