package ua.com.juja.microservices.teams.controller;

import net.javacrumbs.jsonunit.core.Option;
import org.eclipse.jetty.http.HttpMethod;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ua.com.juja.microservices.Utils;
import ua.com.juja.microservices.teams.entity.Team;
import ua.com.juja.microservices.teams.entity.TeamRequest;
import ua.com.juja.microservices.teams.exceptions.UserAlreadyInTeamException;
import ua.com.juja.microservices.teams.exceptions.UserInSeveralTeamsException;
import ua.com.juja.microservices.teams.exceptions.UserNotInTeamException;
import ua.com.juja.microservices.teams.service.TeamService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.junit.Assert.assertEquals;
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

    @Value("${rest.api.version}")
    private String restApiVersion;

    @Inject
    private MockMvc mockMvc;

    @MockBean
    private TeamService teamService;

    private String TEAMS_DEACTIVATE_TEAM_URL;
    private String TEAMS_ACTIVATE_TEAM_URL;

    @Before
    public void setup() {
        TEAMS_DEACTIVATE_TEAM_URL = "/" + restApiVersion + "/teams/users/";
        TEAMS_ACTIVATE_TEAM_URL = "/" + restApiVersion + "/teams";
    }

    @Test
    public void test_activateTeamIfSomeUsersInActiveTeams() throws Exception {

        String jsonContentRequest = Utils.convertToString(resource
                ("acceptance/request/requestActivateTeamIfUsersInActiveTeamThrowsExceptions.json"));

        String jsonContentExpectedResponse = Utils.convertToString(
                resource("acceptance/response/responseActivateTeamIfUserInActiveTeamThrowsException.json"));
        List<String> usersInTeams = new ArrayList<>(Arrays.asList("user-in-team", "user-in-team2"));

        when(teamService.activateTeam(any(TeamRequest.class)))
                .thenThrow(new UserAlreadyInTeamException(
                        String.format("User(s) '#%s#' exist(s) in another teams", usersInTeams.toString())));

        verifyNoMoreInteractions(teamService);
        String actualResponse = getBadPostResult(TEAMS_ACTIVATE_TEAM_URL, jsonContentRequest);

        assertThatJson(actualResponse).when(Option.IGNORING_ARRAY_ORDER).isEqualTo(jsonContentExpectedResponse);
    }

    @Test
    public void test_activateTeamIfAllUsersNotInActiveTeams() throws Exception {

        String jsonContentRequest = Utils.convertToString(resource(
                "acceptance/request/requestActivateTeamIfUserNotInActiveTeamExecutedCorrecly.json"));
        final TeamRequest teamRequest = new TeamRequest(new LinkedHashSet<>(Arrays.asList("400",
                "100",
                "200",
                "300")));
        final Team team = new Team(teamRequest.getMembers());

        when(teamService.activateTeam(any(TeamRequest.class))).thenReturn(team);
        String result = getGoodPostResult(TEAMS_ACTIVATE_TEAM_URL, jsonContentRequest);

        verify(teamService).activateTeam(any(TeamRequest.class));
        verifyNoMoreInteractions(teamService);
        assertEquals(Utils.convertToJSON(team), result);
    }

    @Test
    public void test_deactivateTeamIfUserInTeamExecutedCorrectly() throws Exception {

        final String uuid = "user-in-team";
        final Team team =
                new Team(new LinkedHashSet<>(Arrays.asList(uuid, "user1", "user2", "user-in-several-teams")));

        when(teamService.deactivateTeam(uuid)).thenReturn(team);
        String result = getGoodResult(TEAMS_DEACTIVATE_TEAM_URL + uuid, HttpMethod.PUT);

        verify(teamService).deactivateTeam(uuid);
        verifyNoMoreInteractions(teamService);
        assertEquals(Utils.convertToJSON(team), result);
    }

    @Test
    public void test_getTeamByUuidIfUserInTeamExecutedCorrectly() throws Exception {

        final String uuid = "user-in-team";
        final Team team =
                new Team(new LinkedHashSet<>(Arrays.asList(uuid, "user1", "user2", "user-in-several-teams")));

        when(teamService.getUserActiveTeam(uuid)).thenReturn(team);
        String result = getGoodResult(TEAMS_DEACTIVATE_TEAM_URL + uuid, HttpMethod.GET);

        verify(teamService).getUserActiveTeam(uuid);
        verifyNoMoreInteractions(teamService);
        assertEquals(Utils.convertToJSON(team), result);
    }

    @Test
    public void test_getTeamByUuidIfUserNotInTeamBadResponce() throws Exception {

        String jsonContentExpectedResponse = Utils.convertToString(
                resource("acceptance/response/responseGetDeactivateTeamIfUserNotInTeamThrowsExeption.json"));
        final String uuid = "user-not-in-team";

        when(teamService.getUserActiveTeam(uuid)).thenThrow(new UserNotInTeamException(String.format("User with uuid " +
                "'%s' not in team now", uuid)));

        verifyNoMoreInteractions(teamService);
        String actualResponse = getBadResult(TEAMS_DEACTIVATE_TEAM_URL + uuid, HttpMethod.GET);
        assertThatJson(actualResponse).when(Option.IGNORING_ARRAY_ORDER).isEqualTo(jsonContentExpectedResponse);
    }

    @Test
    public void test_deactivateTeamIfUserNotInTeamBadResponce() throws Exception {

        String jsonContentExpectedResponse = Utils.convertToString(
                resource("acceptance/response/responseGetDeactivateTeamIfUserNotInTeamThrowsExeption.json"));
        final String uuid = "user-not-in-team";

        when(teamService.deactivateTeam(uuid)).thenThrow(new UserNotInTeamException(String.format("User with uuid " +
                "'%s' not in team now", uuid)));

        verifyNoMoreInteractions(teamService);
        String actualResponse = getBadResult(TEAMS_DEACTIVATE_TEAM_URL + uuid, HttpMethod.PUT);
        assertThatJson(actualResponse).when(Option.IGNORING_ARRAY_ORDER).isEqualTo(jsonContentExpectedResponse);
    }

    @Test
    public void test_deactivateTeamIfUserInSeveralTeamsBadResponce() throws Exception {
        String jsonContentExpectedResponse = Utils.convertToString(
                resource("acceptance/response/responseGetDeactivateTeamIfUserInSeveralTeamsThrowsExceptions.json"));
        final String uuid = "user-in-several-teams";

        when(teamService.deactivateTeam(uuid))
                .thenThrow(new UserInSeveralTeamsException(String.format("User with uuid '%s' is in several teams now", uuid)));

        verifyNoMoreInteractions(teamService);
        String actualResponse = getBadResult(TEAMS_DEACTIVATE_TEAM_URL + uuid, HttpMethod.PUT);
        assertThatJson(actualResponse).when(Option.IGNORING_ARRAY_ORDER).isEqualTo(jsonContentExpectedResponse);

    }

    private String getGoodResult(String uri, HttpMethod method) throws Exception {
        MockHttpServletRequestBuilder builder;
        if (HttpMethod.GET == method) {
            builder = get(uri);
        } else if (HttpMethod.PUT == method) {
            builder = put(uri);
        } else {
            throw new RuntimeException("Unsupported HttpMethod in getResponse()");
        }

        return mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }

    private String getBadResult(String uri, HttpMethod method) throws Exception {
        MockHttpServletRequestBuilder builder;
        if (HttpMethod.GET == method) {
            builder = get(uri);
        } else if (HttpMethod.PUT == method) {
            builder = put(uri);
        } else {
            throw new RuntimeException("Unsupported HttpMethod in getResponse()");
        }
        return mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
    }

    private String getGoodPostResult(String uri, String jsonContentRequest) throws Exception {
        return mockMvc.perform(post(uri)
                .contentType(APPLICATION_JSON_UTF8)
                .content(jsonContentRequest))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }

    private String getBadPostResult(String uri, String jsonContentRequest) throws Exception {
        return mockMvc.perform(post(uri)
                .contentType(APPLICATION_JSON_UTF8)
                .content(jsonContentRequest))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
    }
}