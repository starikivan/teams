package ua.com.juja.microservices.teams.dao.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import ua.com.juja.microservices.teams.entity.Team;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;

/**
 * @author Ivan Shapovalov
 * @author Andrii.Sidun
 */
@Repository
@Slf4j
public class TeamRepository {

    @Value("${spring.data.mongodb.collection}")
    private String mongoCollectionName;
    @Inject
    private MongoTemplate mongoTemplate;

    public List<Team> getUserActiveTeams(String uuid, Date actualDate) {
        log.debug("Started 'Get user teams' '{}' from DB at date '{}'", uuid, actualDate);
        List<Team> teams = mongoTemplate.find(new Query(Criteria.where("deactivateDate").gt(actualDate)
                        .and("members").is(uuid).and("activateDate").lte(actualDate)),
                Team.class, mongoCollectionName);
        log.debug("Finished 'Get user '{}' teams' from DB at date '{}'. Teams <{}>", uuid, actualDate, teams);
        return teams;
    }

    public List<String> checkUsersActiveTeams(Set<String> members, Date actualDate) {
        log.debug("Started 'checkUsersActiveTeams' '{}' from DB at date '{}'", members.toArray(), actualDate);
        Aggregation agg = newAggregation(
                match(Criteria.where("deactivateDate").gt(actualDate).and("activateDate").lte(actualDate)
                        .and("members").in(members)),
                project("members"),
                unwind("members"),
                match(Criteria.where("members").in(members)),
                group("members").count().as("teams"),
                project("teams").and("uuid").previousOperation()
        );

        AggregationResults<Member> groupResults
                = mongoTemplate.aggregate(agg, mongoCollectionName, Member.class);
        List<Member> usersInActiveTeams = groupResults.getMappedResults();

        List<String> users = usersInActiveTeams.stream().map(Member::getUuid).collect(Collectors.toList());
        Collections.sort(users);
        log.debug("Finished 'checkUsersActiveTeams '{}' teams' from DB at date '{}'. Users in active teams <{}>",
                members.toArray(), actualDate, users.toArray());
        return users;

    }

    public List<Team> getAllActiveTeams(Date actualDate) {
        log.debug("Started 'Get all active teams' from DB at date '{}'", actualDate);
        List<Team> teams = mongoTemplate.find(new Query(Criteria.where("deactivateDate").gt(actualDate)
                .and("activateDate").lte(actualDate)), Team.class, mongoCollectionName);
        log.debug("Finished 'Get all active teams' from DB at date '{}'. Teams <{}>", actualDate, teams);
        return teams;

    }

    public Team saveTeam(Team team) {
        log.debug("Started 'Save team' '{}' into DB ", team.toString());
        mongoTemplate.save(team, mongoCollectionName);
        log.debug("Finished 'Save team' '{}' into DB ", team.toString());
        return team;
    }

    @Getter
    private class Member {
        public String uuid;
    }
}
