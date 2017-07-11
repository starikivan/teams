package juja.microservices.teams.service;

import juja.microservices.teams.dao.TeamRepository;
import juja.microservices.teams.entity.Team;
import juja.microservices.teams.entity.TeamRequest;
import juja.microservices.teams.exceptions.TeamUserExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * @author Ivan Shapovalov
 */

@Service
public class TeamService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private TeamRepository teamRepository;

    public String dismissTeam(String uuid){
        logger.debug("Start TeamService.dismiss. Team: {}", teamRequest);
        if (isAnyBodyInOtherTeam(teamRequest)){
            logger.warn("Some user(s) exists in a team");
            throw new TeamUserExistsException("Some user(s) exists in a team");
        }
        Team team = mappingRequestToTeam(teamRequest);
        String newTeamId = teamRepository.delete(team);
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
}
