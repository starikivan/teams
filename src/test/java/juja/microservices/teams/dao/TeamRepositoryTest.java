package juja.microservices.teams.dao;

import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import juja.microservices.integration.BaseIntegrationTest;
import juja.microservices.teams.entity.Team;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * @author Ivan Shapovalov
 * @author Andrii Sidun
 */
@RunWith(SpringRunner.class)
public class TeamRepositoryTest extends BaseIntegrationTest {

    @Inject
    private TeamRepository teamRepository;

    @Test
    public void test_saveTeamExecutedCorrectly(){
        final String userInOneTeam = "user-in-one-team";
        final String userInSeveralTeams = "user-in-several-teams";
        final Team team = new Team(new HashSet<>(Arrays.asList(userInOneTeam, "user1", "user2", userInSeveralTeams)));
        final List<Team> expected = new ArrayList<>();
        expected.add(team);

        Team actual = teamRepository.saveTeam(team);

        assertNotNull(actual);
        assertThat(actual.getMembers(), is(team.getMembers()));
    }

    @Test
    @UsingDataSet(locations = "/datasets/deactivateTeam_dataSet.json")
    public void test_getUserTeamsUserInOneTeamExecutedCorrectly() {
        final String userInOneTeam = "user-in-one-team";
        final String userInSeveralTeams = "user-in-several-teams";
        final Team team = new Team(new HashSet<>(Arrays.asList(userInOneTeam, "user1", "user2", userInSeveralTeams)));
        final List<Team> expected = new ArrayList<>();
        expected.add(team);

        List<Team> actual = teamRepository.getUserTeams(userInOneTeam);

        assertEquals(actual.size(),1);
        assertThat(actual.get(0).getMembers(), is(team.getMembers()));
    }

    @Test
    @UsingDataSet(locations = "/datasets/deactivateTeam_dataSet.json",loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void test_getUserTeamsIfUserInSeveralTeamsExecutedCorrectly() {
        final String userInOneTeam = "user-in-one-team";
        final String userInSeveralTeams = "user-in-several-teams";
        final Team team1 = new Team(new LinkedHashSet<>(Arrays.asList(userInOneTeam, "user1", "user2", userInSeveralTeams)));
        final Team team2 = new Team(new LinkedHashSet<>(Arrays.asList(userInSeveralTeams, "user3", "user4", "user5")));
        final List<Team> expected = new ArrayList<>();
        expected.add(team1);
        expected.add(team2);

        List<Team> actual = teamRepository.getUserTeams(userInSeveralTeams);

        assertEquals(actual.size(),expected.size());
        for (int i = 0; i < actual.size(); i++) {
            assertThat(actual.get(i).getMembers(), is(expected.get(i).getMembers()));
        }
    }

    @Test
    @UsingDataSet(locations = "/datasets/deactivateTeam_dataSet.json",loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void test_getUserTeamsIfUserNotInTeamExecutedCorrectly() {
        final String userNotInTeam = "user-not-in-team";
        final List<Team> expected = new ArrayList<>();

        List<Team> actual = teamRepository.getUserTeams(userNotInTeam);

        assertEquals(expected.toString(), actual.toString());
    }

    @Test
    @UsingDataSet(locations = "/datasets/deactivateTeam_dataSet.json",loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void test_getUserTeamsIfUserInDeactivatedTeamsExecutedCorrectly() {
        final String userInDeactivatedTeam = "user-in-deactivated-team";
        final List<Team> expected = new ArrayList<>();

        List<Team> actual = teamRepository.getUserTeams(userInDeactivatedTeam);

        assertEquals(expected.toString(),actual.toString());
    }

    @Test
    @UsingDataSet(locations = "/datasets/deactivateTeam_dataSet.json",loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void test_deactivateTeamExecutedCorrectly() {
        final String userInOneTeam = "user-in-one-team";
        List<Team> teamsBefore = teamRepository.getUserTeams(userInOneTeam);
        assertEquals(1,teamsBefore.size());
        teamsBefore.get(0).setDeactivateDate(LocalDateTime.now());

        teamRepository.saveTeam(teamsBefore.get(0));
        List<Team> teamsAfter = teamRepository.getUserTeams(userInOneTeam);

        assertEquals(0,teamsAfter.size());
    }
}