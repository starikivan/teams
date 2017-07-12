package juja.microservices.integration;

import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import juja.microservices.teams.dao.TeamRepository;
import juja.microservices.teams.entity.Team;
import juja.microservices.teams.exceptions.UserInSeveralTeamsException;
import juja.microservices.teams.exceptions.UserNotInTeamException;
import juja.microservices.teams.service.TeamService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
public class TeamServiceIntegrationTest extends BaseIntegrationTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Inject
    private TeamRepository repository;

    @Inject
    private TeamService service;

    @Test
    @UsingDataSet(locations = "/datasets/deactivateTeam_dataSet.json")
    public void test_dismissTeamIfUserInTeamExecutedCorrectly() {
        final String uuid = "user-in-one-team";

        List<Team> teamsBefore = repository.getUserTeams(uuid);
        assertEquals(1, teamsBefore.size());
        service.dismissTeam(uuid);
        List<Team> teamsAfter = repository.getUserTeams(uuid);
        assertEquals(0, teamsAfter.size());
    }

    @Test
    @UsingDataSet(locations = "/datasets/deactivateTeam_dataSet.json")
    public void test_dismissTeamIfUserNotInTeamExecutedCorrectly() {
        final String uuid = "user-not-in-team";

        List<Team> teamsBefore = repository.getUserTeams(uuid);
        assertEquals(0, teamsBefore.size());
        expectedException.expect(UserNotInTeamException.class);
        expectedException.expectMessage(String.format("User with uuid '%s' not in team now",uuid));
        service.dismissTeam(uuid);
    }

    @Test
    @UsingDataSet(locations = "/datasets/deactivateTeam_dataSet.json")
    public void test_dismissTeamIfUserInSeveralTeamsExecutedCorrectly() {
        final String uuid = "user-in-several-teams";

        List<Team> teamsBefore = repository.getUserTeams(uuid);
        assertEquals(2, teamsBefore.size());
        expectedException.expect(UserInSeveralTeamsException.class);
        expectedException.expectMessage(String.format("User with uuid '%s' is in several teams now", uuid));
        service.dismissTeam(uuid);
    }

}