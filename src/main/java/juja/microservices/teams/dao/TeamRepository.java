package juja.microservices.teams.dao;

import juja.microservices.teams.entity.Team;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Ivan Shapovalov
 * @author Andrii.Sidun
 */
@Repository
@Slf4j
public class TeamRepository {

    @Inject
    private MongoTemplate mongoTemplate;

    public List<Team> getUserTeams(String uuid) {
        log.debug("Started 'Get user teams' '{}' from DB at current date", uuid);
        Date currentDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<Team> teams = mongoTemplate.find(new Query(Criteria.where("deactivateDate").gt(currentDate)
                .and("members").is(uuid)
        ), Team.class);
        if (teams == null) {
            log.debug("Finished 'Get user '{}' teams from DB at current date. Teams is empty", uuid);
            return new ArrayList<>();
        } else {
            log.debug("Finished 'Get user '{}' teams' from DB at current date. Teams <{}>", uuid, teams);
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
