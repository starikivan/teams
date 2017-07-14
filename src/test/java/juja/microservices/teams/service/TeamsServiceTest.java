package juja.microservices.teams.service;

import juja.microservices.teams.dao.TeamRepository;
import juja.microservices.teams.entity.Team;
import juja.microservices.teams.entity.TeamRequest;
import juja.microservices.teams.exceptions.TeamUserExistsException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import juja.microservices.teams.dao.TeamRepository;
import juja.microservices.teams.entity.Team;
import juja.microservices.teams.exceptions.UserInSeveralTeamsException;
import juja.microservices.teams.exceptions.UserNotInTeamException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrii.Sidun
 */

@RunWith(SpringRunner.class)
@WebMvcTest(TeamService.class)
public class TeamsServiceTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Inject
    @InjectMocks
    private TeamService teamService;

    @MockBean
    private TeamRepository teamRepository;

    final String creator = "f827811f-51e8-4fc4-a56d-aebcd2193bc4";
    final String notKeeper = "11111111-51e8-4fc4-a56d-aebcd2193bc4";
    final String uuidOne = "f827811f-51e8-4fc4-a56d-aebcd2193bc3";
    final String uuidOneInOtherTeam = "11111111-51e8-4fc4-a56d-aebcd2193bc3";
    final String uuidTwo = "f827811f-51e8-4fc4-a56d-aebcd2193bc2";
    final String uuidThree = "f827811f-51e8-4fc4-a56d-aebcd2193bc1";
    final String uuidFour = "f827811f-51e8-4fc4-a56d-aebcd2193bc0";
    final TeamRequest teamRequest = new TeamRequest(creator, uuidOne, uuidTwo, uuidThree, uuidFour);

    @Test
    public void addTeam(){
        //Given
        String expected = "SomeId";
        when(teamRepository.add(any(Team.class))).thenReturn("SomeId");
        //When
        String result = teamService.addTeam(teamRequest);
        //Then
        assertEquals(expected, result);
    }

    @Test(expected = TeamUserExistsException.class)
    public void addTeamWhenUserExistsInOtherTeam(){
        //When
        when(teamRepository.isUserInOtherTeam(anyString())).thenReturn(true);
        teamService.addTeam(teamRequest);
    }

    @Test
    public void test_dismissTeamIfUserInSeveralTeamsThrowsException()  {
        final String uuid = "user-in-several-teams";
        final Team team1=new Team(uuid,"","","");
        final Team team2=new Team("",uuid,"","");
        List<Team> teams=new ArrayList<>();
        teams.add(team1);
        teams.add(team2);

        when(repository.getUserTeams(uuid)).thenReturn(teams);

        expectedException.expect(UserInSeveralTeamsException.class);
        expectedException.expectMessage(String.format("User with uuid '%s' is in several teams now", uuid));

        service.dismissTeam(uuid);
        verify(repository).getUserTeams(uuid);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void test_dismissTeamIfUserNotInTeamThrowsException() {
        final String uuid = "user-not-in-team";
        List<Team> teams=new ArrayList<>();
        when(repository.getUserTeams(uuid)).thenReturn(teams);

        expectedException.expect(UserNotInTeamException.class);
        expectedException.expectMessage(String.format("User with uuid '%s' not in team now",uuid));

        service.dismissTeam(uuid);
        verify(repository).getUserTeams(uuid);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void test_dismissTeamIfUserInTeamExecutedCorrectly() {
        final String uuid = "user-in-team";
        final Team team=new Team(uuid,"","","");
        List<Team> teams=new ArrayList<>();
        teams.add(team);
        when(repository.getUserTeams(uuid)).thenReturn(teams);
        when(repository.saveTeam(team)).thenReturn(team);

        service.dismissTeam(uuid);
        verify(repository).getUserTeams(uuid);
        verify(repository).saveTeam(team);
        verifyNoMoreInteractions(repository);
    }

}