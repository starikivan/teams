package ua.com.juja.microservices.teams.service;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ua.com.juja.microservices.teams.dao.impl.TeamRepository;
import ua.com.juja.microservices.teams.entity.Team;
import ua.com.juja.microservices.teams.entity.impl.ActivateTeamRequest;
import ua.com.juja.microservices.teams.entity.impl.DeactivateTeamRequest;
import ua.com.juja.microservices.teams.exceptions.UserAlreadyInTeamException;
import ua.com.juja.microservices.teams.exceptions.UserInSeveralTeamsException;
import ua.com.juja.microservices.teams.exceptions.UserNotInTeamException;
import ua.com.juja.microservices.teams.exceptions.UserNotTeamsKeeperException;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.core.Is.is;
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

    @MockBean
    private KeeperService keeperService;

    @Value("${keepers.direction.teams}")
    private String teamsDirection;

    @Test
    public void activateTeamIfUserNotInAnotherTeamsExecutedCorrectly() {
        String from = "uuid-from";
        ActivateTeamRequest activateTeamRequest = new ActivateTeamRequest(from, new HashSet<>(
                Arrays.asList("uuid1", "uuid2", "uuid3", "uuid4")));
        Team expected = new Team(from, activateTeamRequest.getMembers());
        List<String> userInTeams = new ArrayList<>();
        when(keeperService.getDirections(from)).thenReturn(Collections.singletonList(teamsDirection));
        when(teamRepository.checkUsersActiveTeams(eq(activateTeamRequest.getMembers()),
                any(Date.class))).thenReturn(userInTeams);
        when(teamRepository.saveTeam(any(Team.class))).thenReturn(expected);

        Team actual = teamService.activateTeam(activateTeamRequest);

        verify(keeperService).getDirections(from);
        verify(teamRepository).checkUsersActiveTeams(eq(activateTeamRequest.getMembers()), anyObject());
        ArgumentCaptor<Team> captor = ArgumentCaptor.forClass(Team.class);
        verify(teamRepository).saveTeam(captor.capture());
        assertEquals(expected.getMembers(), captor.getValue().getMembers());
        assertEquals(expected.getMembers(), actual.getMembers());
        verifyNoMoreInteractions(teamRepository, keeperService);
    }

    @Test
    public void activateTeamIfRequestIsNullThrowsException() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Activate team Request must contain '4' members");

        teamService.activateTeam(null);
    }

    @Test
    public void activateTeamIfRequestContainNotFourMembersThrowsException() {
        String from = "uuid-from";
        ActivateTeamRequest activateTeamRequest = new ActivateTeamRequest(from,
                new HashSet<>(Arrays.asList("uuid1", "uuid2", "uuid4")));
        when(keeperService.getDirections(from)).thenReturn(Collections.singletonList("teams"));
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Activate team Request must contain '4' members");

        teamService.activateTeam(activateTeamRequest);
    }

    @Test
    public void activateTeamIfUserNotKeeperThrowsException() {
        String from = "uuid-from";
        ActivateTeamRequest activateTeamRequest = new ActivateTeamRequest(from,
                new HashSet<>(Arrays.asList("uuid1", "uuid2", "uuid3", "uuid4")));
        when(keeperService.getDirections(from)).thenReturn(null);

        expectedException.expect(UserNotTeamsKeeperException.class);
        expectedException.expectMessage("User '#uuid-from#' have not permissions for that command");

        teamService.activateTeam(activateTeamRequest);
    }

    @Test
    public void activateTeamIfUserNotKeeperOfTeamsDirectionThrowsException() {
        String from = "uuid-from";
        ActivateTeamRequest activateTeamRequest = new ActivateTeamRequest(from,
                new HashSet<>(Arrays.asList("uuid1", "uuid2", "uuid3", "uuid4")));
        when(keeperService.getDirections(from)).thenReturn(Arrays.asList("teams2", "keepers"));

        expectedException.expect(UserNotTeamsKeeperException.class);
        expectedException.expectMessage("User '#uuid-from#' have not permissions for that command");

        teamService.activateTeam(activateTeamRequest);
    }

    @Test
    public void activateTeamIfUserExistsInAnotherTeamThrowsException() {
        String from = "uuid-from";
        ActivateTeamRequest activateTeamRequest = new ActivateTeamRequest(from,
                new HashSet<>(Arrays.asList("uuid1", "uuid2", "uuid3", "uuid4")));
        List<String> usersInTeams = new ArrayList<>(Arrays.asList("uuid1", "uuid4"));
        when(keeperService.getDirections(from)).thenReturn(Collections.singletonList("teams"));
        when(teamRepository.checkUsersActiveTeams(eq(activateTeamRequest.getMembers()), any(Date.class)))
                .thenReturn(usersInTeams);
        expectedException.expect(UserAlreadyInTeamException.class);
        expectedException.expectMessage(String.format("User(s) '#%s#' exist(s) in another teams",
                usersInTeams.stream().collect(Collectors.joining(","))));

        teamService.activateTeam(activateTeamRequest);
    }

    @Test
    public void deactivateTeamIfRequestIsNullThrowsException() {
        String from = "uuid-from";
        final String uuid = "uuid-in-several-teams";
        DeactivateTeamRequest deactivateTeamRequest = new DeactivateTeamRequest(null, uuid);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Deactivate team Request must contain 'from' and 'uuid' fields");

        teamService.deactivateTeam(null);
    }

    @Test
    public void deactivateTeamIfFromIsNullThrowsException() {
        final String uuid = "uuid-in-several-teams";
        DeactivateTeamRequest deactivateTeamRequest = new DeactivateTeamRequest(null, uuid);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Deactivate team Request must contain 'from' and 'uuid' fields");

        teamService.deactivateTeam(null);
    }

    @Test
    public void deactivateTeamIfUuidIsNullThrowsException() {
        final String from = "uuid-from";
        DeactivateTeamRequest deactivateTeamRequest = new DeactivateTeamRequest(from, null);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Deactivate team Request must contain 'from' and 'uuid' fields");

        teamService.deactivateTeam(null);
    }

    @Test
    public void deactivateTeamIfUserNotKeeperThrowsException() {
        final String from = "uuid-from";
        final String uuid = "uuid-in-one-team";
        DeactivateTeamRequest deactivateTeamRequest = new DeactivateTeamRequest(from, uuid);
        when(keeperService.getDirections(from)).thenReturn(null);
        expectedException.expect(UserNotTeamsKeeperException.class);
        expectedException.expectMessage("User '#uuid-from#' have not permissions for that command");

        teamService.deactivateTeam(deactivateTeamRequest);
    }

    @Test
    public void deactivateTeamIfUserNotTeamsKeeperThrowsException() {
        final String from = "uuid-from";
        final String uuid = "uuid-in-one-team";
        DeactivateTeamRequest deactivateTeamRequest = new DeactivateTeamRequest(from, uuid);
        when(keeperService.getDirections(from)).thenReturn(Arrays.asList("teams2", "keepers"));

        expectedException.expect(UserNotTeamsKeeperException.class);
        expectedException.expectMessage("User '#uuid-from#' have not permissions for that command");

        teamService.deactivateTeam(deactivateTeamRequest);
    }

    @Test
    public void deactivateTeamIfUserInSeveralTeamsThrowsException() {
        String from = "uuid-from";
        final String uuid = "uuid-in-several-teams";
        DeactivateTeamRequest deactivateTeamRequest = new DeactivateTeamRequest(from, uuid);
        final Team team1 = new Team(from, new HashSet<>(Arrays.asList(uuid, "uuid1", "uuid2", "uuid3")));
        final Team team2 = new Team(from, new HashSet<>(Arrays.asList("uuid4", uuid, "uuid5", "uuid6")));
        List<Team> teams = new ArrayList<>();
        teams.add(team1);
        teams.add(team2);
        when(keeperService.getDirections(from)).thenReturn(Collections.singletonList(teamsDirection));
        when(teamRepository.getUserActiveTeams(eq(uuid), any(Date.class))).thenReturn(teams);
        expectedException.expect(UserInSeveralTeamsException.class);
        expectedException.expectMessage(String.format("User with uuid '%s' is in several teams now", uuid));

        teamService.deactivateTeam(deactivateTeamRequest);
    }

    @Test
    public void deactivateTeamIfUserNotInTeamThrowsException() {
        String from = "uuid-from";
        final String uuid = "uuid-not-in-team";
        DeactivateTeamRequest deactivateTeamRequest = new DeactivateTeamRequest(from, uuid);

        List<Team> teams = new ArrayList<>();
        when(keeperService.getDirections(from)).thenReturn(Collections.singletonList(teamsDirection));
        when(teamRepository.getUserActiveTeams(eq(uuid), any(Date.class))).thenReturn(teams);
        expectedException.expect(UserNotInTeamException.class);
        expectedException.expectMessage(String.format("User with uuid '%s' not in team now", uuid));

        teamService.deactivateTeam(deactivateTeamRequest);
    }

    @Test
    public void deactivateTeamIfUserInTeamExecutedCorrectly() {
        String from = "uuid-from";
        final String uuid = "uuid-in-team";
        DeactivateTeamRequest deactivateTeamRequest = new DeactivateTeamRequest(from, uuid);
        final Team team = new Team(from,new HashSet<>(Arrays.asList(uuid, "", "", "")));
        List<Team> teams = new ArrayList<>();
        teams.add(team);
        when(keeperService.getDirections(from)).thenReturn(Collections.singletonList(teamsDirection));
        when(teamRepository.getUserActiveTeams(eq(uuid), any(Date.class))).thenReturn(teams);
        when(teamRepository.saveTeam(team)).thenReturn(team);

        teamService.deactivateTeam(deactivateTeamRequest);

        verify(keeperService).getDirections(from);
        verify(teamRepository).getUserActiveTeams(eq(uuid), any(Date.class));
        verify(teamRepository).saveTeam(team);
        verifyNoMoreInteractions(teamRepository, keeperService);
    }

    @Test
    public void getAllTeamsExecutedCorrectly() {
        String from="uuid-from";
        final Team team1 = new Team(from,new HashSet<>(Arrays.asList("uuid1", "uuid2", "uuid3", "uuid4")));
        final Team team2 = new Team(from,new HashSet<>(Arrays.asList("uuid5", "uuid6", "uuid7", "uuid8")));
        final List<Team> expected = Arrays.asList(team1, team2);

        when(teamRepository.getAllActiveTeams(any(Date.class))).thenReturn(expected);

        List<Team> actual = teamService.getAllActiveTeams();

        verify(teamRepository).getAllActiveTeams(any(Date.class));
        assertThat(actual, is(expected));
        verifyNoMoreInteractions(teamRepository);
    }
}