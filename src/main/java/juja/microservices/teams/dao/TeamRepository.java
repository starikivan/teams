package juja.microservices.teams.dao;

import juja.microservices.teams.entity.Team;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import javax.inject.Inject;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Ivan Shapovalov
 */
public class TeamRepository {

    @Inject
    private MongoTemplate mongoTemplate;

    public String dismiss(Team team) {
        Calendar cal = Calendar.getInstance();
        Update update = new Update();
        update.set("dismissDate", Date.from(cal.toInstant()));
        mongoTemplate.updateMulti(new Query(Criteria.where("id").is(team.getId())), update, Team.class);
        return team.getId();
    }

    public List<String> getUserTeamId(String uuid) {
        Date currentData = new Date();
        List<Team> teams = mongoTemplate.find(new Query(
                new Criteria()
                        .andOperator(Criteria.where("dismissDate").gte(currentData))
                        .orOperator(
                                Criteria.where("uuidOne").is(uuid),
                                Criteria.where("uuidTwo").is(uuid),
                                Criteria.where("uuidThree").is(uuid),
                                Criteria.where("uuidFour").is(uuid))
        ), Team.class);
        return teams.stream().map(team -> team.getId()).collect(Collectors.toList());
    }
}
