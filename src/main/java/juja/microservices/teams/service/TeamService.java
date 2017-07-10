package juja.microservices.teams.service;

import juja.microservices.teams.dao.TeamRepository;
import juja.microservices.teams.entity.Team;
import juja.microservices.teams.exceptions.TeamUserExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * @author Andrii.Sidun
 */

@Service
public class TeamService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private TeamRepository teamRepository;

    public String addTeam(Team team){
        logger.debug("Start TeamService.addTeam. Team: {}", team);
        if (isAnyBodyInOtherTeam(team)){
            logger.warn("Some user(s) exists in a team");
            throw new TeamUserExistsException("Some user(s) exists in a team");
        }
        String newTeamId = teamRepository.add(team);
        logger.info("Added new Team with parameters '{}'", team);
        logger.debug("Finish TeamService.addTeam. newTeamId: {}", newTeamId);
        return newTeamId;
    }

    private boolean isAnyBodyInOtherTeam(Team team){
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
