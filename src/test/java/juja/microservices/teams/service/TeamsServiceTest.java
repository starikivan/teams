package juja.microservices.teams.service;

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

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebMvcTest(TeamService.class)
public class TeamsServiceTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Inject
    private TeamService service;

    @MockBean
    private TeamRepository repository;

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
        Mockito.verifyNoMoreInteractions(repository);
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
        Mockito.verifyNoMoreInteractions(repository);
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
        Mockito.verifyNoMoreInteractions(repository);
    }

}