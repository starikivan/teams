package juja.microservices.teams.service;

import juja.microservices.teams.dao.TeamRepository;
import juja.microservices.teams.entity.Team;
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
        //TODO: Need to check if team.creator is keeper. Only keeper can add the new team
        String newTeamId = teamRepository.save(team);
        logger.info("Added new Team with parameters '{}'", team);
        logger.debug("Finish TeamService.addTeam. newTeamId: {}", newTeamId);
        return newTeamId;
    }
}
