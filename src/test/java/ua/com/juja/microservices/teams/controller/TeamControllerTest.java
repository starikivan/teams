package ua.com.juja.microservices.teams.controller;

import net.javacrumbs.jsonunit.core.Option;
import org.eclipse.jetty.http.HttpMethod;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ua.com.juja.microservices.Utils;
import ua.com.juja.microservices.teams.entity.Team;
import ua.com.juja.microservices.teams.entity.impl.ActivateTeamRequest;
import ua.com.juja.microservices.teams.entity.impl.DeactivateTeamRequest;
import ua.com.juja.microservices.teams.exceptions.UserAlreadyInTeamException;
import ua.com.juja.microservices.teams.exceptions.UserInSeveralTeamsException;
import ua.com.juja.microservices.teams.exceptions.UserNotInTeamException;
import ua.com.juja.microservices.teams.service.TeamService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Andrii Sidun
 * @author Ivan Shapovalov
 */
@RunWith(SpringRunner.class)
@WebMvcTest(TeamController.class)
public class TeamControllerTest {
    @Value("${teams.endpoint.activateTeam}")
    private String teamsActivateTeamUrl;
    @Value("${teams.endpoint.deactivateTeam}")
    private String teamsDeactivateTeamUrl;
    @Value("${teams.endpoint.getTeam}")
    private String teamsGetTeamUrl;
    @Value("${teams.endpoint.getAllTeams}")
    private String teamsGetAllTeamsUrl;

    @Inject
    private MockMvc mockMvc;

    @MockBean
    private TeamService teamService;

    @Test
    public void activateTeamIfSomeUsersInActiveTeams() throws Exception {
        String jsonContentRequest = Utils.convertToString(resource
                ("acceptance/request/requestActivateTeamIfUsersInActiveTeamThrowsExceptions.json"));
        String jsonContentExpectedResponse = Utils.convertToString(
                resource("acceptance/response/responseActivateTeamIfUserInActiveTeamThrowsException.json"));
        List<String> usersInTeams = new ArrayList<>(Arrays.asList("uuid-in-team", "uuid-in-team2"));
        when(teamService.activateTeam(any(ActivateTeamRequest.class)))
                .thenThrow(new UserAlreadyInTeamException(
                        String.format("User(s) '#%s#' exist(s) in another teams",
                                usersInTeams.stream().collect(Collectors.joining(",")))));

        String actualResponse = getBadJsonResult(teamsActivateTeamUrl, HttpMethod.POST, jsonContentRequest);

        assertThatJson(actualResponse).when(Option.IGNORING_ARRAY_ORDER).isEqualTo(jsonContentExpectedResponse);
        verify(teamService).activateTeam(any(ActivateTeamRequest.class));
        verifyNoMoreInteractions(teamService);
    }

    @Test
    public void activateTeamIfAllUsersNotInActiveTeams() throws Exception {
        String jsonContentRequest = Utils.convertToString(resource(
                "acceptance/request/requestActivateTeamIfUserNotInActiveTeamExecutedCorrecly.json"));
        final List<String> expectedUuids = Arrays.asList("uuid400", "uuid100", "uuid200", "uuid300");
        final Team team = new Team("uuid-from", new LinkedHashSet<>(expectedUuids));
        when(teamService.activateTeam(any(ActivateTeamRequest.class))).thenReturn(team);

        String result = getGoodJsonResult(teamsActivateTeamUrl, HttpMethod.POST, jsonContentRequest);

        ArgumentCaptor<ActivateTeamRequest> captor = ArgumentCaptor.forClass(ActivateTeamRequest.class);
        verify(teamService).activateTeam(captor.capture());
        Set<String> actualUuids = captor.getValue().getMembers();
        assertThat("List equality without order",
                actualUuids, containsInAnyOrder(expectedUuids.toArray(new String[expectedUuids.size()])));
        verifyNoMoreInteractions(teamService);
        assertEquals(Utils.convertToJSON(team), result);
    }

    @Test
    public void deactivateTeamIfUserInTeamExecutedCorrectly() throws Exception {
        String jsonContentRequest = Utils.convertToString(resource(
                "acceptance/request/requestDeactivateTeamIfUserInTeamExecutedCorrectly.json"));
        final Team team =
                new Team("uuid-from", new LinkedHashSet<>(Arrays.asList("uuid400", "uuid100", "uuid200", "uuid-in-one-team")));
        when(teamService.deactivateTeam(any(DeactivateTeamRequest.class))).thenReturn(team);

        String result = getGoodJsonResult(teamsDeactivateTeamUrl, HttpMethod.PUT, jsonContentRequest);

        ArgumentCaptor<DeactivateTeamRequest> captor = ArgumentCaptor.forClass(DeactivateTeamRequest.class);
        verify(teamService).deactivateTeam(captor.capture());
        assertTrue(captor.getValue().getFrom().equals("uuid-from"));
        assertTrue(captor.getValue().getUuid().equals("uuid-in-one-team"));
        verifyNoMoreInteractions(teamService);
        assertEquals(Utils.convertToJSON(team), result);
    }

    @Test
    public void getTeamByUuidIfUserInTeamExecutedCorrectly() throws Exception {
        final String uuid = "uuid-in-team";
        final Team team =
                new Team("uuid-from", new LinkedHashSet<>(Arrays.asList(uuid, "uuid1", "uuid2", "uuid-in-several-teams")));
        when(teamService.getUserActiveTeam(uuid)).thenReturn(team);

        String result = getGoodResult(teamsGetTeamUrl + "/" + uuid);

        verify(teamService).getUserActiveTeam(uuid);
        verifyNoMoreInteractions(teamService);
        assertEquals(Utils.convertToJSON(team), result);
    }

    @Test
    public void getTeamByUuidIfUserNotInTeamBadResponce() throws Exception {
        String jsonContentExpectedResponse = Utils.convertToString(
                resource("acceptance/response/responseGetDeactivateTeamIfUserNotInTeamThrowsExeption.json"));
        final String uuid = "uuid-not-in-team";
        when(teamService.getUserActiveTeam(uuid)).thenThrow(new UserNotInTeamException(String.format("User with uuid " +
                "'%s' not in team now", uuid)));

        String actualResponse = getBadResult(teamsGetTeamUrl + "/" + uuid);

        assertThatJson(actualResponse).when(Option.IGNORING_ARRAY_ORDER).isEqualTo(jsonContentExpectedResponse);
        verify(teamService).getUserActiveTeam(uuid);
        verifyNoMoreInteractions(teamService);
    }

    @Test
    public void deactivateTeamIfUserNotInTeamBadResponce() throws Exception {
        String jsonContentRequest = Utils.convertToString(resource(
                "acceptance/request/requestDeactivateTeamIfUserNotInTeamThrowsException.json"));
        String jsonContentExpectedResponse = Utils.convertToString(
                resource("acceptance/response/responseGetDeactivateTeamIfUserNotInTeamThrowsExeption.json"));
        final String uuid = "uuid-not-in-team";
        when(teamService.deactivateTeam(any(DeactivateTeamRequest.class)))
                .thenThrow(new UserNotInTeamException(String.format("User with uuid '%s' not in team now", uuid)));

        String actualResponse = getBadJsonResult(teamsDeactivateTeamUrl, HttpMethod.PUT, jsonContentRequest);

        assertThatJson(actualResponse).when(Option.IGNORING_ARRAY_ORDER).isEqualTo(jsonContentExpectedResponse);
        ArgumentCaptor<DeactivateTeamRequest> captor = ArgumentCaptor.forClass(DeactivateTeamRequest.class);
        verify(teamService).deactivateTeam(captor.capture());
        assertTrue(captor.getValue().getFrom().equals("uuid-from"));
        assertTrue(captor.getValue().getUuid().equals("uuid-not-in-team"));
        verifyNoMoreInteractions(teamService);
    }

    @Test
    public void deactivateTeamIfUserInSeveralTeamsBadResponce() throws Exception {
        String jsonContentRequest = Utils.convertToString(resource(
                "acceptance/request/requestDeactivateTeamIfUserInSeveralTeamsException.json"));

        String jsonContentExpectedResponse = Utils.convertToString(
                resource("acceptance/response/responseGetDeactivateTeamIfUserInSeveralTeamsThrowsExceptions.json"));
        final String uuid = "uuid-in-several-teams";
        when(teamService.deactivateTeam(any(DeactivateTeamRequest.class)))
                .thenThrow(new UserInSeveralTeamsException(String.format("User with uuid '%s' is in several teams " +
                        "now", uuid)));

        String actualResponse = getBadJsonResult(teamsDeactivateTeamUrl, HttpMethod.PUT, jsonContentRequest);

        assertThatJson(actualResponse).when(Option.IGNORING_ARRAY_ORDER).isEqualTo(jsonContentExpectedResponse);
        ArgumentCaptor<DeactivateTeamRequest> captor = ArgumentCaptor.forClass(DeactivateTeamRequest.class);
        verify(teamService).deactivateTeam(captor.capture());
        assertTrue(captor.getValue().getFrom().equals("uuid-from"));
        assertTrue(captor.getValue().getUuid().equals("uuid-in-several-teams"));
        verifyNoMoreInteractions(teamService);
    }

    @Test
    public void getAllActiveTeamsGoodResponse() throws Exception {
        final Team team1 = new Team("uuid-from", new HashSet<>(Arrays.asList("uuid1", "uuid2", "uuid3", "uuid4")));
        final Team team2 = new Team("uuid-from", new HashSet<>(Arrays.asList("uuid5", "uuid6", "uuid7", "uuid8")));
        final List<Team> teams = Arrays.asList(team1, team2);
        String expected = "[" + teams.stream().map(Utils::convertToJSON)
                .collect(Collectors.joining(",")) + "]";
        when(teamService.getAllActiveTeams()).thenReturn(teams);

        String result = getGoodResult(teamsGetAllTeamsUrl);

        verify(teamService).getAllActiveTeams();
        verifyNoMoreInteractions(teamService);
        assertEquals(expected, result);
    }

    private String getGoodResult(String uri) throws Exception {
        MockHttpServletRequestBuilder builder;
        if (HttpMethod.GET == HttpMethod.GET) {
            builder = get(uri);
        } else if (HttpMethod.PUT == HttpMethod.GET) {
            builder = put(uri);
        } else {
            throw new RuntimeException("Unsupported HttpMethod in getResponse()");
        }
        return mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }

    private String getBadResult(String uri) throws Exception {
        MockHttpServletRequestBuilder builder;
        if (HttpMethod.GET == HttpMethod.GET) {
            builder = get(uri);
        } else if (HttpMethod.PUT == HttpMethod.GET) {
            builder = put(uri);
        } else {
            throw new RuntimeException("Unsupported HttpMethod in getResponse()");
        }
        return mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
    }

    private String getGoodJsonResult(String uri, HttpMethod method, String jsonContentRequest) throws Exception {
        MockHttpServletRequestBuilder builder;
        if (HttpMethod.POST == method) {
            builder = post(uri);
        } else if (HttpMethod.PUT == method) {
            builder = put(uri);
        } else {
            throw new RuntimeException("Unsupported HttpMethod in getResponse()");
        }
        return mockMvc.perform(builder
                .contentType(APPLICATION_JSON_UTF8)
                .content(jsonContentRequest))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }

    private String getBadJsonResult(String uri, HttpMethod method, String jsonContentRequest) throws Exception {
        MockHttpServletRequestBuilder builder;
        if (HttpMethod.POST == method) {
            builder = post(uri);
        } else if (HttpMethod.PUT == method) {
            builder = put(uri);
        } else {
            throw new RuntimeException("Unsupported HttpMethod in getResponse()");
        }
        return mockMvc.perform(builder
                .contentType(APPLICATION_JSON_UTF8)
                .content(jsonContentRequest))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
    }
}