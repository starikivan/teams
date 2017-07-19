package ua.com.juja.microservices.teams.service;

import ua.com.juja.microservices.teams.dao.TeamRepository;
import ua.com.juja.microservices.teams.entity.Team;
import ua.com.juja.microservices.teams.entity.TeamRequest;
import ua.com.juja.microservices.teams.exceptions.UserAlreadyInTeamException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import ua.com.juja.microservices.teams.exceptions.UserInSeveralTeamsException;
import ua.com.juja.microservices.teams.exceptions.UserNotInTeamException;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import java.util.*;

/**
 * @author Ivan Shapovalov
 * @author Andrii Sidun
 */
@RunWith(SpringRunner.class)
@WebMvcTest(TeamService.class)
public class TeamServiceTest {

    @Rule
    final public ExpectedException expectedException = ExpectedException.none();

    @Inject
    @InjectMocks
    private TeamService teamService;

    @MockBean
    private TeamRepository teamRepository;

    @Test
    public void test_addTeamIfUserNotInAnotherTeamsExecutedCorrectly() {
        //Given
        TeamRequest teamRequest = new TeamRequest(new HashSet<>(Arrays.asList("user1", "user2", "user3", "user4")));
        Team expected = new Team(teamRequest.getMembers());
        List<String> userInTeams = new ArrayList<>();
        when(teamRepository.checkUsersActiveTeams(anySetOf(String.class), anyObject())).thenReturn(userInTeams);
        when(teamRepository.saveTeam(any(Team.class))).thenReturn(expected);

        Team actual = teamService.addTeam(teamRequest);

        verify(teamRepository).checkUsersActiveTeams(anySetOf(String.class), anyObject());
        verify(teamRepository).saveTeam(any(Team.class));
        verifyNoMoreInteractions(teamRepository);
        assertEquals(expected.getMembers(), actual.getMembers());
    }

    @Test(expected = UserAlreadyInTeamException.class)
    public void test_addTeamIfUserExistsInAnotherTeamThrowsException() {
        TeamRequest teamRequest = new TeamRequest(new HashSet<>(Arrays.asList("user1", "user2", "user3", "user4")));

        List<String> userInTeams = new ArrayList<>(Collections.singletonList("user1"));
        when(teamRepository.checkUsersActiveTeams(anySetOf(String.class), anyObject())).thenReturn(userInTeams);
        teamService.addTeam(teamRequest);
    }

    @Test
    public void test_deactivateTeamIfUserInSeveralTeamsThrowsException() {
        final String uuid = "user-in-several-teams";
        final Team team1 = new Team(new HashSet<>(Arrays.asList(uuid, "user1", "user2", "user3")));
        final Team team2 = new Team(new HashSet<>(Arrays.asList("user4", uuid, "user5", "user6")));
        List<Team> teams = new ArrayList<>();
        teams.add(team1);
        teams.add(team2);

        when(teamRepository.getUserActiveTeams(anyString(), anyObject())).thenReturn(teams);

        expectedException.expect(UserInSeveralTeamsException.class);
        expectedException.expectMessage(String.format("User with uuid '%s' is in several teams now", uuid));

        teamService.deactivateTeam(uuid);
        verify(teamRepository).getUserActiveTeams(anyString(), anyObject());
        verifyNoMoreInteractions(teamRepository);
    }

    @Test
    public void test_deactivateTeamIfUserNotInTeamThrowsException() {
        final String uuid = "user-not-in-team";
        List<Team> teams = new ArrayList<>();
        when(teamRepository.getUserActiveTeams(anyString(), anyObject())).thenReturn(teams);

        expectedException.expect(UserNotInTeamException.class);
        expectedException.expectMessage(String.format("User with uuid '%s' not in team now", uuid));

        teamService.deactivateTeam(uuid);
        verify(teamRepository).getUserActiveTeams(anyString(), anyObject());
        verifyNoMoreInteractions(teamRepository);
    }

    @Test
    public void test_deactivateTeamIfUserInTeamExecutedCorrectly() {
        final String uuid = "user-in-team";
        final Team team = new Team(new HashSet<>(Arrays.asList(uuid, "", "", "")));
        List<Team> teams = new ArrayList<>();
        teams.add(team);

        when(teamRepository.getUserActiveTeams(anyString(), anyObject())).thenReturn(teams);
        when(teamRepository.saveTeam(team)).thenReturn(team);
        teamService.deactivateTeam(uuid);

        verify(teamRepository).getUserActiveTeams(anyString(), anyObject());
        verify(teamRepository).saveTeam(team);
        verifyNoMoreInteractions(teamRepository);
    }
}