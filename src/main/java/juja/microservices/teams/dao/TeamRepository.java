package juja.microservices.teams.dao;

import juja.microservices.teams.entity.Team;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository
public class TeamRepository {


    @Inject
    private MongoTemplate mongoTemplate;

    public String save(Team team){
        mongoTemplate.save(team);
        return team.getId();
    }

}
