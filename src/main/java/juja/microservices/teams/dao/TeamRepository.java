package juja.microservices.teams.dao;

import juja.microservices.teams.entity.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * @author Ivan Shapovalov
 * @author Andrii.Sidun
 */
@Repository
public class TeamRepository {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private MongoTemplate mongoTemplate;

    public List<Team> getUserTeams(String uuid) {
        logger.debug("Started get teams of user <{}> from DB for current date", uuid);
        Date currentDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<Team> teams = mongoTemplate.find(new Query(Criteria.where("deactivateDate").gt(currentDate)
                .and("members").is(uuid)
        ), Team.class);
        logger.debug("Finished get teams of user <{}> from DB for current date. Teams <{}>", uuid, teams.toString());
        return teams;
    }

    public Team saveTeam(Team team) {
        logger.debug("Started save team <{}> into DB ", team.toString());
        mongoTemplate.save(team);
        logger.debug("Finished save team <{}> into DB ", team.toString());
        return team;
    }
}
