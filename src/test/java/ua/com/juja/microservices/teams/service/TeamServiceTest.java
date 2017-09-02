package ua.com.juja.microservices.teams.service;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ua.com.juja.microservices.teams.dao.TeamRepository;
import ua.com.juja.microservices.teams.entity.Team;
import ua.com.juja.microservices.teams.entity.TeamRequest;
import ua.com.juja.microservices.teams.exceptions.UserAlreadyInTeamException;
import ua.com.juja.microservices.teams.exceptions.UserInSeveralTeamsException;
import ua.com.juja.microservices.teams.exceptions.UserNotInTeamException;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Ivan Shapovalov
 * @author Andrii Sidun
 */
@RunWith(SpringRunner.class)
@WebMvcTest(TeamService.class)
public class TeamServiceTest {

    @Rule
    final public ExpectedException expectedException = ExpectedException.none();

    @InjectMocks
    @Inject
    private TeamService teamService;

    @MockBean
    private TeamRepository teamRepository;

    @Test
    public void activateTeamIfUserNotInAnotherTeamsExecutedCorrectly() {
        TeamRequest teamRequest = new TeamRequest(new HashSet<>(Arrays.asList("uuid1", "uuid2", "uuid3", "uuid4")));
        Team expected = new Team(teamRequest.getMembers());
        List<String> userInTeams = new ArrayList<>();
        when(teamRepository.checkUsersActiveTeams(eq(teamRequest.getMembers()),
                any(Date.class))).thenReturn(userInTeams);
        when(teamRepository.saveTeam(any(Team.class))).thenReturn(expected);

        Team actual = teamService.activateTeam(teamRequest);

        verify(teamRepository).checkUsersActiveTeams(eq(teamRequest.getMembers()), anyObject());
        ArgumentCaptor<Team> captor = ArgumentCaptor.forClass(Team.class);
        verify(teamRepository).saveTeam(captor.capture());
        assertEquals(expected.getMembers(), captor.getValue().getMembers());
        assertEquals(expected.getMembers(), actual.getMembers());
        verifyNoMoreInteractions(teamRepository);
    }

    @Test
    public void activateTeamIfRequestIsNullThrowsException() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Team Request must contain '4' members");

        teamService.activateTeam(null);
    }

    @Test
    public void activateTeamIfRequestContainNotFourMembersThrowsException() {
        TeamRequest teamRequest = new TeamRequest(new HashSet<>(Arrays.asList("uuid1", "uuid2", "uuid4")));

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Team Request must contain '4' members");

        teamService.activateTeam(teamRequest);
    }

    @Test
    public void activateTeamIfUserExistsInAnotherTeamThrowsException() {
        TeamRequest teamRequest = new TeamRequest(new HashSet<>(Arrays.asList("uuid1", "uuid2", "uuid3", "uuid4")));
        List<String> usersInTeams = new ArrayList<>(Arrays.asList("uuid1", "uuid4"));
        when(teamRepository.checkUsersActiveTeams(eq(teamRequest.getMembers()), any(Date.class)))
                .thenReturn(usersInTeams);
        expectedException.expect(UserAlreadyInTeamException.class);
        expectedException.expectMessage(String.format("User(s) '#%s#' exist(s) in another teams",
                usersInTeams.stream().collect(Collectors.joining(","))));

        teamService.activateTeam(teamRequest);

        verify(teamRepository).checkUsersActiveTeams(eq(teamRequest.getMembers()), any(Date.class));
        verifyNoMoreInteractions(teamRepository);
    }

    @Test
    public void deactivateTeamIfUserInSeveralTeamsThrowsException() {
        final String uuid = "uuid-in-several-teams";
        final Team team1 = new Team(new HashSet<>(Arrays.asList(uuid, "uuid1", "uuid2", "uuid3")));
        final Team team2 = new Team(new HashSet<>(Arrays.asList("uuid4", uuid, "uuid5", "uuid6")));
        List<Team> teams = new ArrayList<>();
        teams.add(team1);
        teams.add(team2);
        when(teamRepository.getUserActiveTeams(eq(uuid), any(Date.class))).thenReturn(teams);
        expectedException.expect(UserInSeveralTeamsException.class);
        expectedException.expectMessage(String.format("User with uuid '%s' is in several teams now", uuid));

        teamService.deactivateTeam(uuid);

        verify(teamRepository).getUserActiveTeams(eq(uuid), any(Date.class));
        verifyNoMoreInteractions(teamRepository);
    }

    @Test
    public void deactivateTeamIfUserNotInTeamThrowsException() {
        final String uuid = "uuid-not-in-team";
        List<Team> teams = new ArrayList<>();
        when(teamRepository.getUserActiveTeams(eq(uuid), any(Date.class))).thenReturn(teams);
        expectedException.expect(UserNotInTeamException.class);
        expectedException.expectMessage(String.format("User with uuid '%s' not in team now", uuid));

        teamService.deactivateTeam(uuid);

        verify(teamRepository).getUserActiveTeams(eq(uuid), any(Date.class));
        verifyNoMoreInteractions(teamRepository);
    }

    @Test
    public void deactivateTeamIfUserInTeamExecutedCorrectly() {
        final String uuid = "uuid-in-team";
        final Team team = new Team(new HashSet<>(Arrays.asList(uuid, "", "", "")));
        List<Team> teams = new ArrayList<>();
        teams.add(team);
        when(teamRepository.getUserActiveTeams(eq(uuid), any(Date.class))).thenReturn(teams);
        when(teamRepository.saveTeam(team)).thenReturn(team);

        teamService.deactivateTeam(uuid);

        verify(teamRepository).getUserActiveTeams(eq(uuid), any(Date.class));
        verify(teamRepository).saveTeam(team);
        verifyNoMoreInteractions(teamRepository);
    }

    @Test
    public void getAllTeamsExecutedCorrectly() {
        final Team team1 = new Team(new HashSet<>(Arrays.asList("uuid1", "uuid2", "uuid3", "uuid4")));
        final Team team2 = new Team(new HashSet<>(Arrays.asList("uuid5", "uuid6", "uuid7", "uuid8")));
        final List<Team> expected = Arrays.asList(team1, team2);

        when(teamRepository.getAllActiveTeams(any(Date.class))).thenReturn(expected);

        List<Team> actual = teamService.getAllActiveTeams();

        verify(teamRepository).getAllActiveTeams(any(Date.class));
        assertThat(actual, is(expected));
        verifyNoMoreInteractions(teamRepository);
    }
}