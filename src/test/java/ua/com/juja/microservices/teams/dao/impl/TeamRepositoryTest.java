package ua.com.juja.microservices.teams.dao.impl;

import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import ua.com.juja.microservices.integration.BaseIntegrationTest;
import ua.com.juja.microservices.teams.entity.Team;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
    public void saveTeamExecutedCorrectly() {
        final String uuidInOneTeam = "uuid-in-one-team";
        final String uuidInSeveralTeams = "uuid-in-several-teams";
        final Team expected =
                new Team("uuid-from",new HashSet<>(Arrays.asList(uuidInOneTeam, "uuid1", "uuid2", uuidInSeveralTeams)));

        Team actual = teamRepository.saveTeam(expected);

        assertNotNull(actual);
        assertThat(actual.getMembers(), is(expected.getMembers()));
    }

    @Test
    @UsingDataSet(locations = "/datasets/getAndDeactivateDataSet.json")
    public void getUserTeamsUserInOneTeamExecutedCorrectly() {
        Date actualDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        final String uuidInOneTeam = "uuid-in-one-team";
        final String uuidInSeveralTeams = "uuid-in-several-teams";
        final Team expected =
                new Team("uuid-from",new HashSet<>(Arrays.asList(uuidInOneTeam, "uuid1", "uuid2", uuidInSeveralTeams)));

        List<Team> actual = teamRepository.getUserActiveTeams(uuidInOneTeam, actualDate);

        assertEquals(1, actual.size());
        assertThat(actual.get(0).getMembers(), is(expected.getMembers()));
    }

    @Test
    @UsingDataSet(locations = "/datasets/getAndDeactivateDataSet.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void getUserTeamsIfUserInSeveralTeamsExecutedCorrectly() {
        Date actualDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        final String uuidInOneTeam = "uuid-in-one-team";
        final String uuidInSeveralTeams = "uuid-in-several-teams";
        final Team team1 =
                new Team("uuid-from",new LinkedHashSet<>(Arrays.asList(uuidInOneTeam, "uuid1", "uuid2", uuidInSeveralTeams)));
        final Team team2 =
                new Team("uuid-from",new LinkedHashSet<>(Arrays.asList(uuidInSeveralTeams, "uuid3", "uuid4", "uuid5")));
        final List<Team> expected = new ArrayList<>();
        expected.add(team1);
        expected.add(team2);

        List<Team> actual = teamRepository.getUserActiveTeams(uuidInSeveralTeams, actualDate);

        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < actual.size(); i++) {
            assertThat(actual.get(i).getMembers(), is(expected.get(i).getMembers()));
        }
    }

    @Test
    @UsingDataSet(locations = "/datasets/getAndDeactivateDataSet.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void getUserTeamsIfUserNotInTeamExecutedCorrectly() {
        Date actualDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        final String uuidNotInTeam = "uuid-not-in-team";

        List<Team> actual = teamRepository.getUserActiveTeams(uuidNotInTeam, actualDate);

        assertEquals(new ArrayList<>(), actual);
    }

    @Test
    @UsingDataSet(locations = "/datasets/getAndDeactivateDataSet.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void getUserTeamsIfUserInDeactivatedTeamsExecutedCorrectly() {
        Date actualDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        final String uuidInDeactivatedTeam = "uuid-in-deactivated-team";

        List<Team> actual = teamRepository.getUserActiveTeams(uuidInDeactivatedTeam, actualDate);

        assertEquals(new ArrayList<>(), actual);
    }

    @Test
    @UsingDataSet(locations = "/datasets/getAndDeactivateDataSet.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void deactivateTeamExecutedCorrectly() {
        Date actualDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        final String uuidInOneTeam = "uuid-in-one-team";
        List<Team> teamsBefore = teamRepository.getUserActiveTeams(uuidInOneTeam, actualDate);
        assertEquals(1, teamsBefore.size());
        teamsBefore.get(0).setDeactivateDate(actualDate);
        Date newDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());

        teamRepository.saveTeam(teamsBefore.get(0));
        List<Team> teamsAfter = teamRepository.getUserActiveTeams(uuidInOneTeam, newDate);

        assertEquals(0, teamsAfter.size());
    }

    @Test
    @UsingDataSet(locations = "/datasets/getAndDeactivateDataSet.json")
    public void checkUsersActiveTeamsSomeUserInSeveralTeamsExecutedCorrectly() {
        Date actualDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        final String uuidInOneTeam = "uuid-in-one-team";
        final String uuidInSeveralTeams = "uuid-in-several-teams";
        Set<String> members = new HashSet<>();
        members.add(uuidInOneTeam);
        members.add(uuidInSeveralTeams);
        final List<String> expected = new ArrayList<>();
        expected.add(uuidInOneTeam);
        expected.add(uuidInSeveralTeams);

        List<String> actual = teamRepository.checkUsersActiveTeams(members, actualDate);

        assertEquals(2, actual.size());
        assertThat(actual, is(expected));
    }

    @Test
    @UsingDataSet(locations = "/datasets/getAndDeactivateDataSet.json")
    public void checkUsersActiveTeamsNoOneInSeveralTeamsReturnsEmptyList() {
        Date actualDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        final String uuidNotOneTeam = "uuid-not-in-team";
        Set<String> members = new HashSet<>();
        members.add(uuidNotOneTeam);
        final List<String> expected = new ArrayList<>();

        List<String> actual = teamRepository.checkUsersActiveTeams(members, actualDate);

        assertEquals(0, actual.size());
        assertThat(actual, is(expected));
    }

    @Test
    @UsingDataSet(locations = "/datasets/getAllActiveTeamsDataSet.json")
    public void getAllActiveTeamsIfMongoTemplateReturnsNotNullTeamExecutedCorrectly() {
        Date actualDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        final Team team1 = new Team("uuid-from",new HashSet<>(Arrays.asList("uuid1", "uuid2", "uuid3", "uuid4")));
        final Team team2 = new Team("uuid-from",new HashSet<>(Arrays.asList("uuid5", "uuid6", "uuid7", "uuid8")));
        final List<Team> expected = Arrays.asList(team1, team2);

        List<Team> actual = teamRepository.getAllActiveTeams(actualDate);

        assertEquals(2, actual.size());
        for (int i = 0; i < actual.size(); i++) {
            assertThat(actual.get(i).getMembers(), is(expected.get(i).getMembers()));
        }
    }
}