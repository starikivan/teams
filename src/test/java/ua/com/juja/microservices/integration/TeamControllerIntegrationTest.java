package ua.com.juja.microservices.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import ua.com.juja.microservices.Utils;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Ivan Shapovalov
 * @author Andrii Sidun
 */
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class TeamControllerIntegrationTest extends BaseIntegrationTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();
    @Value("${teams.endpoint.activateTeam}")
    private String teamsActivateTeamUrl;
    @Value("${teams.endpoint.deactivateTeam}")
    private String teamsDeactivateTeamUrl;
    @Value("${teams.endpoint.getAllTeams}")
    private String teamsGetAllTeamsUrl;

    @Value("${keepers.endpoint.getDirections}")
    private String keepersGetDirectionsUrl;

    @Value("${keepers.direction.teams}")
    private String teamsDirection;

    @Inject
    private RestTemplate restTemplate;

    @Inject
    private MockMvc mockMvc;
    private MockRestServiceServer mockServer;

    @Before
    public void setup() {
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @UsingDataSet(locations = "/datasets/activateTeamIfUserNotInActiveTeam.json")
    public void activateTeamIfUserNotInActiveTeamExecutedCorrectly() throws Exception {
        String jsonContentRequest = Utils.convertToString((resource
                ("acceptance/request/requestActivateTeamIfUserNotInActiveTeamExecutedCorrecly.json")));
        mockSuccessKeepersServiceReturnsDirections(keepersGetDirectionsUrl + "/uuid-from",
                Collections.singletonList(teamsDirection));
        mockMvc.perform(post(teamsActivateTeamUrl)
                .contentType(APPLICATION_JSON_UTF8)
                .content(jsonContentRequest))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    @UsingDataSet(locations = "/datasets/activateTeamIfUserNotInActiveTeam.json")
    public void activateTeamIfUserInActiveTeamExecutedCorrectly() throws Exception {
        String jsonContentRequest = Utils.convertToString((resource
                ("acceptance/request/requestActivateTeamIfUsersInActiveTeamThrowsExceptions.json")));
        mockSuccessKeepersServiceReturnsDirections(keepersGetDirectionsUrl + "/uuid-from",
                Collections.singletonList(teamsDirection));
        mockMvc.perform(post(teamsActivateTeamUrl)
                .contentType(APPLICATION_JSON_UTF8)
                .content(jsonContentRequest))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    @UsingDataSet(locations = "/datasets/getAndDeactivateDataSet.json")
    public void deactivateTeamIfUserInTeamExecutedCorrectly() throws Exception {
        String jsonContentRequest = Utils.convertToString((resource
                ("acceptance/request/requestDeactivateTeamIfUserInTeamExecutedCorrectly.json")));
        mockSuccessKeepersServiceReturnsDirections(keepersGetDirectionsUrl + "/uuid-from",
                Collections.singletonList(teamsDirection));
        mockMvc.perform(put(teamsDeactivateTeamUrl)
                .contentType(APPLICATION_JSON_UTF8)
                .content(jsonContentRequest))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    @UsingDataSet(locations = "/datasets/getAndDeactivateDataSet.json")
    public void deactivateTeamIfUserNotInTeamExecutedCorrectly() throws Exception {
        String jsonContentRequest = Utils.convertToString((resource
                ("acceptance/request/requestDeactivateTeamIfUserNotInTeamThrowsException.json")));
        mockSuccessKeepersServiceReturnsDirections(keepersGetDirectionsUrl + "/uuid-from",
                Collections.singletonList(teamsDirection));

        mockMvc.perform(put(teamsDeactivateTeamUrl)
                .contentType(APPLICATION_JSON_UTF8)
                .content(jsonContentRequest))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

    @Test
    @UsingDataSet(locations = "/datasets/getAndDeactivateDataSet.json")
    public void deactivateTeamIfUserInSeveralTeamsExecutedCorrectly() throws Exception {
        String jsonContentRequest = Utils.convertToString((resource
                ("acceptance/request/requestDeactivateTeamIfUserInSeveralTeamsException.json")));
        mockSuccessKeepersServiceReturnsDirections(keepersGetDirectionsUrl + "/uuid-from",
                Collections.singletonList(teamsDirection));

        mockMvc.perform(put(teamsDeactivateTeamUrl)
                .contentType(APPLICATION_JSON_UTF8)
                .content(jsonContentRequest))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

    @Test
    @UsingDataSet(locations = "/datasets/getAllActiveTeamsDataSet.json")
    public void getAllTeamsExecutedCorrectly() throws Exception {
        mockMvc.perform(get(teamsGetAllTeamsUrl)
                .contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    private void mockSuccessKeepersServiceReturnsDirections(String expectedURI,
                                                            List<String> directions) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mockServer.expect(requestTo(expectedURI))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(mapper.writeValueAsString(directions), MediaType.APPLICATION_JSON_UTF8));
    }
}