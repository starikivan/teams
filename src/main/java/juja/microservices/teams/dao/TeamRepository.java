package juja.microservices.teams.dao;

import juja.microservices.teams.entity.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

/**
 * @author Ivan Shapovalov
 */
@Repository
public class TeamRepository {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private MongoTemplate mongoTemplate;

    public List<Team> getUserTeams(String uuid) {
        logger.debug("Started get teams of user <{}> from DB for current date", uuid);
        Date currentData = new Date();
        List<Team> teams=mongoTemplate.find(new Query(
                new Criteria()
                        .andOperator(Criteria.where("dismissDate").gte(currentData))
                        .orOperator(
                                Criteria.where("uuidOne").is(uuid), Criteria.where("uuidTwo").is(uuid),
                                Criteria.where("uuidThree").is(uuid), Criteria.where("uuidFour").is(uuid))
        ), Team.class);
        logger.debug("Finished get teams of user <{}> from DB for current date. Teams <{}>", uuid,teams.toString());
        return teams;
    }

    public Team saveTeam(Team team) {
        logger.debug("Started save team <{}> into DB ", team.toString());
        mongoTemplate.save(team);
        logger.debug("Finished save team <{}> into DB ", team.toString());
        return team;
    }

}
