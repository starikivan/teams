package juja.microservices.teams.dao;

import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import juja.microservices.integration.BaseIntegrationTest;
import juja.microservices.teams.entity.Team;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
public class TeamsRepositoryTest extends BaseIntegrationTest {

    @Inject
    private TeamRepository repository;

    @Test
    @UsingDataSet(locations = "/datasets/deactivateTeam_dataSet.json")
    public void test_getUserTeamsUserInOneTeamExecutedCorrectly() {
        final String userInOneTeam = "user-in-one-team";
        final String userInSeveralTeams = "user-in-several-teams";
        final Team team = new Team(userInOneTeam, "", "", userInSeveralTeams);
        final List<Team> expected = new ArrayList<>();
        expected.add(team);

        List<Team> actual = repository.getUserTeams(userInOneTeam);

        assertEquals(expected.toString(), actual.toString());
    }

    @Test
    @UsingDataSet(locations = "/datasets/deactivateTeam_dataSet.json")
    public void test_getUserTeamsIfUserInSeveralTeamsExecutedCorrectly() {
        final String userInOneTeam = "user-in-one-team";
        final String userInSeveralTeams = "user-in-several-teams";
        final Team team1 = new Team(userInOneTeam, "", "", userInSeveralTeams);
        final Team team2 = new Team(userInSeveralTeams, "", "", "");
        final List<Team> expected = new ArrayList<>();
        expected.add(team1);
        expected.add(team2);

        List<Team> actual = repository.getUserTeams(userInSeveralTeams);

        assertEquals(expected.toString(), actual.toString());
    }

    @Test
    @UsingDataSet(locations = "/datasets/deactivateTeam_dataSet.json")
    public void test_getUserTeamsIfUserNotInTeamExecutedCorrectly() {
        final String userNotInTeam = "user-not-in-team";
        final List<Team> expected = new ArrayList<>();

        List<Team> actual = repository.getUserTeams(userNotInTeam);

        assertEquals(expected.toString(), actual.toString());
    }

    @Test
    @UsingDataSet(locations = "/datasets/deactivateTeam_dataSet.json")
    public void test_getUserTeamsIfUserInDismissedTeamsExecutedCorrectly() {
        final String userInDismissedTeam = "user-in-dismissed-team";
        final List<Team> expected = new ArrayList<>();

        List<Team> actual = repository.getUserTeams(userInDismissedTeam);

        assertEquals(expected.toString(),actual.toString());
    }

    @Test
    @UsingDataSet(locations = "/datasets/deactivateTeam_dataSet.json")
    public void test_dismissTeamExecutedCorrectly() {
        final String userInOneTeam = "user-in-one-team";
        List<Team> teamsBefore = repository.getUserTeams(userInOneTeam);
        assertEquals(1,teamsBefore.size());
        teamsBefore.get(0).setDismissDate(LocalDateTime.now());
        repository.saveTeam(teamsBefore.get(0));
        List<Team> teamsAfter = repository.getUserTeams(userInOneTeam);
        assertEquals(0,teamsAfter.size());
    }

}