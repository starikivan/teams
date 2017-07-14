package juja.microservices.teams.dao;

import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import juja.microservices.teams.entity.Team;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@WebMvcTest(TeamRepository.class)
public class TeamsRepositoryTest {

    @Inject
    @InjectMocks
    private TeamRepository teamRepository;

    @MockBean
    private MongoTemplate mongoTemplate;

    final String keeper = "f827811f-51e8-4fc4-a56d-aebcd2193bc4";
    final String notKeeper = "11111111-51e8-4fc4-a56d-aebcd2193bc4";
    final String uuidOne = "f827811f-51e8-4fc4-a56d-aebcd2193bc3";
    final String uuidOneInOtherTeam = "11111111-51e8-4fc4-a56d-aebcd2193bc3";
    final String uuidTwo = "f827811f-51e8-4fc4-a56d-aebcd2193bc2";
    final String uuidThree = "f827811f-51e8-4fc4-a56d-aebcd2193bc1";
    final String uuidFour = "f827811f-51e8-4fc4-a56d-aebcd2193bc0";
    final Team team = new Team(keeper, uuidOne, uuidTwo, uuidThree, uuidFour);

    @Test
    public void save(){
        //When
        doNothing().when(mongoTemplate).save(any(Team.class));
        //Then
        assertEquals(null, teamRepository.add(team));
    }

    @Test
    public void isUserInOtherTeam(){
        //Given
        List<Team> teamList = new ArrayList<Team>();
        teamList.add(team);
        //When
        when(mongoTemplate.find(anyObject(), eq(Team.class))).thenReturn(teamList);
        //Then
        assertTrue(teamRepository.isUserInOtherTeam(uuidOne));

    }

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