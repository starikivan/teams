package juja.microservices.teams.dao;

import juja.microservices.teams.entity.Team;
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

    @Inject
    private MongoTemplate mongoTemplate;

    public List<Team> getUserTeams(String uuid) {
        Date currentData = new Date();
        return mongoTemplate.find(new Query(
                new Criteria()
                        .andOperator(Criteria.where("dismissDate").gte(currentData))
                        .orOperator(
                                Criteria.where("uuidOne").is(uuid), Criteria.where("uuidTwo").is(uuid),
                                Criteria.where("uuidThree").is(uuid), Criteria.where("uuidFour").is(uuid))
        ), Team.class);
    }

    public Team saveTeam(Team team) {
        mongoTemplate.save(team);
        return team;
    }

}
