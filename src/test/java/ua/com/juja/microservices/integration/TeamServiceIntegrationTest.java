package ua.com.juja.microservices.integration;

import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import ua.com.juja.microservices.teams.dao.TeamRepository;
import ua.com.juja.microservices.teams.entity.Team;
import ua.com.juja.microservices.teams.entity.TeamRequest;
import ua.com.juja.microservices.teams.exceptions.UserAlreadyInTeamException;
import ua.com.juja.microservices.teams.service.TeamService;
import ua.com.juja.microservices.teams.exceptions.UserInSeveralTeamsException;
import ua.com.juja.microservices.teams.exceptions.UserNotInTeamException;
import net.javacrumbs.jsonunit.core.Option;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.junit.rules.ExpectedException;

import javax.inject.Inject;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
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

    private final Date actualDate = Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC));

    @Test
    @UsingDataSet(locations = "/datasets/addTeamIfUserNotInActiveTeam.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void test_addTeamIfUserNotInActiveTeamExecutedCorrectly() {
        TeamRequest teamRequest = new TeamRequest(new HashSet<>(Arrays.asList("new-user", "", "", "")));
        Team expected = new Team(teamRequest.getMembers());

        Team actual = teamService.addTeam(teamRequest);
        expected.setId(actual.getId());
        assertThatJson(actual).when(Option.IGNORING_ARRAY_ORDER).isEqualTo(expected);
    }

    @Test
    @UsingDataSet(locations = "/datasets/addTeamIfUsersInAnotherActiveTeam.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void test_addTeamIfUserInAnotherTeamsThrowsExeption() {
        String uuid = "user-in-team";
        TeamRequest teamRequest = new TeamRequest(new HashSet<>(Arrays.asList(uuid, "", "", "")));

        expectedException.expect(UserAlreadyInTeamException.class);
        expectedException.expectMessage(String.format("User(s) '%s' exists in a another teams", "[" + uuid + "]"));

        teamService.addTeam(teamRequest);
    }

    @Test
    @UsingDataSet(locations = "/datasets/getAndDeactivateDataSet.json")
    public void test_getTeamIfUserInTeamExecutedCorrectly() {
        final String uuid = "user-in-one-team";
        List<Team> teamsBefore = teamRepository.getUserActiveTeams(uuid, actualDate);
        assertEquals(1, teamsBefore.size());

        Team teamsAfter = teamService.getUserActiveTeam(uuid);
        assertEquals(teamsAfter, teamsBefore.get(0));
    }

    @Test
    @UsingDataSet(locations = "/datasets/getAndDeactivateDataSet.json")
    public void test_getTeamIfUserNotInTeamExecutedCorrectly() {
        final String uuid = "user-not-in-team";
        List<Team> teamsBefore = teamRepository.getUserActiveTeams(uuid, actualDate);
        assertEquals(0, teamsBefore.size());
        expectedException.expect(UserNotInTeamException.class);
        expectedException.expectMessage(String.format("User with uuid '%s' not in team now", uuid));
        teamService.getUserActiveTeam(uuid);
    }

    @Test
    @UsingDataSet(locations = "/datasets/getAndDeactivateDataSet.json")
    public void test_getTeamIfUserInSeveralTeamsExecutedCorrectly() {
        final String uuid = "user-in-several-teams";

        List<Team> teamsBefore = teamRepository.getUserActiveTeams(uuid, actualDate);
        assertEquals(2, teamsBefore.size());

        expectedException.expect(UserInSeveralTeamsException.class);
        expectedException.expectMessage(String.format("User with uuid '%s' is in several teams now", uuid));
        teamService.getUserActiveTeam(uuid);
    }

    @Test
    @UsingDataSet(locations = "/datasets/getAndDeactivateDataSet.json")
    public void test_deactivateTeamIfUserInTeamExecutedCorrectly() {
        final String uuid = "user-in-one-team";
        List<Team> teamsBefore = teamRepository.getUserActiveTeams(uuid, actualDate);
        assertEquals(1, teamsBefore.size());

        teamService.deactivateTeam(uuid);

        List<Team> teamsAfter = teamRepository.getUserActiveTeams(uuid, actualDate);
        assertEquals(0, teamsAfter.size());
    }
}