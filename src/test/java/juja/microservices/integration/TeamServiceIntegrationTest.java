package juja.microservices.integration;

import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import juja.microservices.teams.dao.TeamRepository;
import juja.microservices.teams.entity.Team;
import juja.microservices.teams.entity.TeamDTO;
import juja.microservices.teams.entity.TeamRequest;
import juja.microservices.teams.exceptions.UserExistsException;
import juja.microservices.teams.service.TeamService;
import juja.microservices.teams.exceptions.UserInSeveralTeamsException;
import juja.microservices.teams.exceptions.UserNotInTeamException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.junit.rules.ExpectedException;

import javax.inject.Inject;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Ivan Shapovalov
 * @author Andrii Sidun
 */
@RunWith(SpringRunner.class)
public class TeamServiceIntegrationTest extends BaseIntegrationTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Inject
    private TeamRepository teamRepository;

    @Inject
    private TeamService teamService;

    @Test
    @UsingDataSet(locations = "/datasets/addTeam_userNotInActiveTeam.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void test_addTeamIfUserNotInActiveTeamExecutedCorrectly(){
        TeamRequest teamRequest = new TeamRequest(new HashSet<>(Arrays.asList("new-user", "", "", "")));
        TeamDTO expected = new TeamDTO(new Team(teamRequest.getMembers()));

        Team actual= teamService.addTeam(teamRequest);

        assertEquals(expected.toString(), new TeamDTO(actual).toString());
    }

    @Test
    @UsingDataSet(locations = "/datasets/addTeam_userInAnotherTeam.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void test_addTeamIfUserInAnotherTeamsThrowsExeption(){
        String uuid="user-in-several-teams";
        TeamRequest teamRequest = new TeamRequest(new HashSet<>(Arrays.asList(uuid, "", "", "")));

        expectedException.expect(UserExistsException.class);
        expectedException.expectMessage(String.format("User(s) '%s' exists in a another teams", "["+uuid+"]"));

        teamService.addTeam(teamRequest);
    }

    @Test
    @UsingDataSet(locations = "/datasets/deactivateTeam_dataSet.json")
    public void test_deactivateTeamIfUserInTeamExecutedCorrectly() {
        final String uuid = "user-in-one-team";

        List<Team> teamsBefore = teamRepository.getUserTeams(uuid);
        assertEquals(1, teamsBefore.size());
        teamService.deactivateTeam(uuid);
        List<Team> teamsAfter = teamRepository.getUserTeams(uuid);
        assertEquals(0, teamsAfter.size());
    }

    @Test
    @UsingDataSet(locations = "/datasets/deactivateTeam_dataSet.json")
    public void test_deactivateTeamIfUserNotInTeamExecutedCorrectly() {
        final String uuid = "user-not-in-team";

        List<Team> teamsBefore = teamRepository.getUserTeams(uuid);
        assertEquals(0, teamsBefore.size());
        expectedException.expect(UserNotInTeamException.class);
        expectedException.expectMessage(String.format("User with uuid '%s' not in team now",uuid));
        teamService.deactivateTeam(uuid);
    }

    @Test
    @UsingDataSet(locations = "/datasets/deactivateTeam_dataSet.json")
    public void test_deactivateTeamIfUserInSeveralTeamsExecutedCorrectly() {
        final String uuid = "user-in-several-teams";

        List<Team> teamsBefore = teamRepository.getUserTeams(uuid);
        assertEquals(2, teamsBefore.size());

        expectedException.expect(UserInSeveralTeamsException.class);
        expectedException.expectMessage(String.format("User with uuid '%s' is in several teams now", uuid));
        teamService.deactivateTeam(uuid);
    }
}