package juja.microservices.teams.dao;

import juja.microservices.teams.entity.Team;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.List;

@Repository
public class TeamRepository {


    @Inject
    private MongoTemplate mongoTemplate;

    public String add(Team team){
        mongoTemplate.save(team);
        return team.getId();
    }

    public boolean isUserInOtherTeam(String uuid){
        List<Team> result = mongoTemplate.find(new Query(
                new Criteria().orOperator(
                        Criteria.where("uuidOne").is(uuid), Criteria.where("uuidTwo").is(uuid),
                        Criteria.where("uuidThree").is(uuid), Criteria.where("uuidFour").is(uuid))), Team.class);
        return !result.isEmpty();
    }
}
