package juja.microservices.integration;

import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import juja.microservices.teams.dao.TeamRepository;
import juja.microservices.teams.entity.Team;
import juja.microservices.teams.entity.TeamRequest;
import juja.microservices.teams.exceptions.TeamUserExistsException;
import juja.microservices.teams.service.TeamService;
import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import juja.microservices.teams.dao.TeamRepository;
import juja.microservices.teams.entity.Team;
import juja.microservices.teams.exceptions.UserInSeveralTeamsException;
import juja.microservices.teams.exceptions.UserNotInTeamException;
import juja.microservices.teams.service.TeamService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.junit.rules.ExpectedException;

import javax.inject.Inject;

import static org.junit.Assert.assertNotNull;



import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Andrii Sidun
 */
@RunWith(SpringRunner.class)
public class TeamServiceIntegrationTest extends BaseIntegrationTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();


    final String creator = "f827811f-51e8-4fc4-a56d-aebcd2193bc4";
    final String uuidOne = "f827811f-51e8-4fc4-a56d-aebcd2193bc3";
    final String uuidTwo = "f827811f-51e8-4fc4-a56d-aebcd2193bc2";
    final String uuidThree = "f827811f-51e8-4fc4-a56d-aebcd2193bc1";
    final String uuidFour = "f827811f-51e8-4fc4-a56d-aebcd2193bc0";
    TeamRequest teamRequest = new TeamRequest(creator, uuidOne, uuidTwo, uuidThree, uuidFour);

    @Inject
    private TeamRepository teamRepository;

    @Inject
    private TeamService teamService;

    @Test
    @UsingDataSet(locations = "/datasets/oneTeamInDB.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void addTeam(){
        //when
        String id = teamService.addTeam(teamRequest);
        //then
        assertNotNull(id);
    }

    @Test(expected = TeamUserExistsException.class)
    @UsingDataSet(locations = "/datasets/oneTeamInDBExists.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void addTeamWhenUserExists(){
        //when
        String id = teamService.addTeam(teamRequest);
    }

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