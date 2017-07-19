package juja.microservices.teams.controller;

import juja.microservices.teams.entity.Team;
import juja.microservices.teams.entity.TeamDTO;
import juja.microservices.teams.entity.TeamRequest;
import juja.microservices.teams.exceptions.UserInSeveralTeamsException;
import juja.microservices.teams.exceptions.UserNotInTeamException;
import juja.microservices.teams.service.TeamService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashSet;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
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

    @Value( "${rest.api.version}" )
    private String restApiVersion;

    @Inject
    private MockMvc mockMvc;

    @MockBean
    private TeamService teamService;

    @Value("classpath:/acceptance/request/addTeam.json")
    private Resource addTeamJsonFile;

    @Test
    public void addTeam() throws Exception {
        String jsonContentRequest = new String(Files.readAllBytes(Paths.get(addTeamJsonFile.getURI())), StandardCharsets.UTF_8);
        final TeamRequest teamRequest = new TeamRequest(new LinkedHashSet<>(Arrays.asList("asdqwe1",
                "f827811f-51e8-4fc4-a56d-aebcd2193bc3",
                "asdqwe3",
                "asdqwe4")));
        final Team team = new Team(teamRequest.getMembers());

        when(teamService.addTeam(any(TeamRequest.class))).thenReturn(team);
        String result = getGoodPostResult("/" + restApiVersion + "/teams", jsonContentRequest);

        verify(teamService).addTeam(any(TeamRequest.class));
        verifyNoMoreInteractions(teamService);
        assertEquals(new TeamDTO(team).toString(), result);
    }

    @Test
    public void test_deactivateTeamUserInTeamExecutedCorrectly()  throws Exception {

        final String uuid = "user-in-team";
        final Team team = new Team(new LinkedHashSet<>(Arrays.asList(uuid,"user1","user2","user-in-several-teams")));

        when(teamService.deactivateTeam(uuid)).thenReturn(team);
        String result= getGoodPutResult("/" + restApiVersion +"/teams/users/"+uuid);

        verify(teamService).deactivateTeam(uuid);
        verifyNoMoreInteractions(teamService);
        assertEquals(new TeamDTO(team).toString(), result);
    }

    @Test
    public void test_deactivateTeamUserNotInTeamBadResponce() throws Exception {

        final String uuid = "user-not-in-team";

        when(teamService.deactivateTeam(uuid)).thenThrow(new UserNotInTeamException(String.format("User with uuid '%s' not in team now",uuid)));

        verifyNoMoreInteractions(teamService);
        getBadPutResult("/" + restApiVersion +"/teams/users/"+uuid);
    }

    @Test
    public void test_deactivateTeamUserInSeveralTeamsBadResponce() throws Exception {
        final String uuid = "user-in-several-teams";

        when(teamService.deactivateTeam(uuid)).thenThrow(new UserInSeveralTeamsException(String.format("User with uuid '%s' is in several teams now", uuid)));

        verifyNoMoreInteractions(teamService);
        getBadPutResult("/" + restApiVersion +"/teams/users/"+uuid);
    }

    private String getGoodPutResult(String uri) throws Exception {
        return mockMvc.perform(put(uri))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }

    private void getBadPutResult(String uri) throws Exception {
        mockMvc.perform(put(uri))
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
}