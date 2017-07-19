package ua.com.juja.microservices.teams.dao;

import ua.com.juja.microservices.teams.entity.Team;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

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

    public List<String> checkUsersActiveTeams(Set<String> members, Date actualDate) {
        log.debug("Started 'checkUsersActiveTeams' '{}' from DB at date '{}'", members.toArray(), actualDate);
        Aggregation agg = newAggregation(
                match(Criteria.where("deactivateDate").gt(actualDate)
                        .and("members").in(members)),
                project("members"),
                unwind("members"),
                match(Criteria.where("members").in(members)),
                group("members").count().as("teams"),
                project("teams").and("name").previousOperation(),
                sort(Sort.Direction.DESC, "teams")
        );

        AggregationResults<User> groupResults
                = mongoTemplate.aggregate(agg, Team.class, User.class);
        List<User> usersInActiveTeams = groupResults.getMappedResults();

        if (usersInActiveTeams == null) {
            log.debug("Finished 'checkUsersActiveTeams '{}' from DB at date '{}'. Teams is empty", members.toArray(), actualDate);
            return new ArrayList<>();
        } else {
            List<String> users = usersInActiveTeams.stream().map(User::getName).collect(Collectors.toList());
            Collections.sort(users);
            log.debug("Finished 'checkUsersActiveTeams '{}' teams' from DB at date '{}'. Users in active teams <{}>",
                    members.toArray(), actualDate, users.toArray());
            return users;
        }
    }

    public Team saveTeam(Team team) {
        log.debug("Started 'Save team' '{}' into DB ", team.toString());
        mongoTemplate.save(team);
        log.debug("Finished 'Save team' '{}' into DB ", team.toString());
        return team;
    }

    @Getter
    private class User {
        public String name;
    }
}
