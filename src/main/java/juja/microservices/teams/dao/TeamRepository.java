package juja.microservices.teams.dao;

import juja.microservices.teams.entity.Team;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

import java.util.*;

/**
 * @author Ivan Shapovalov
 * @author Andrii.Sidun
 */
@Repository
@Slf4j
public class TeamRepository {

    @Inject
    private MongoTemplate mongoTemplate;

    public List<Team> getUserActiveTeams(String uuid, Date actualDate) {
        log.debug("Started 'Get user teams' '{}' from DB at date '{}'", uuid, actualDate);
        List<Team> teams = mongoTemplate.find(new Query(Criteria.where("deactivateDate").gt(actualDate)
                .and("members").is(uuid).and("activateDate").lte(actualDate)
        ), Team.class);

        if (teams == null) {
            log.debug("Finished 'Get user '{}' teams from DB at date '{}'. Teams is empty", uuid, actualDate);
            return new ArrayList<>();
        } else {
            log.debug("Finished 'Get user '{}' teams' from DB at date '{}'. Teams <{}>", uuid, actualDate, teams);
            return teams;
        }
    }

    public Team saveTeam(Team team) {
        log.debug("Started 'Save team' '{}' into DB ", team.toString());
        mongoTemplate.save(team);
        log.debug("Finished 'Save team' '{}' into DB ", team.toString());
        return team;
    }
}
