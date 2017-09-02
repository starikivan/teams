package ua.com.juja.microservices.integration;

import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ua.com.juja.microservices.Utils;

import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
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
public class TeamControllerIntegrationTest extends BaseIntegrationTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();
    @Value("${teams.rest.api.version}")
    private String teamsRestApiVersion;
    @Value("${teams.baseURL}")
    private String teamsBaseUrl;
    @Value("${teams.endpoint.activateTeam}")
    private String teamsActivateTeamUrl;
    @Value("${teams.endpoint.deactivateTeam}")
    private String teamsDeactivateTeamUrl;
    @Value("${teams.endpoint.getAllTeams}")
    private String teamsGetAllTeamsUrl;

    private String teamsFullActivateTeamUrl;
    private String teamsFullDeactivateTeamUrl;
    private String teamsFullGetAllTeamsUrl;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        teamsFullActivateTeamUrl = "/" + teamsRestApiVersion + teamsBaseUrl + teamsActivateTeamUrl;
        teamsFullDeactivateTeamUrl = "/" + teamsRestApiVersion + teamsBaseUrl + teamsDeactivateTeamUrl + "/";
        teamsFullGetAllTeamsUrl = "/" + teamsRestApiVersion + teamsBaseUrl + teamsGetAllTeamsUrl + "/";
    }

    @Test
    @UsingDataSet(locations = "/datasets/activateTeamIfUserNotInActiveTeam.json")
    public void activateTeamIfUserNotInActiveTeamExecutedCorrectly() throws Exception {
        String jsonContentRequest = Utils.convertToString((resource
                ("acceptance/request/requestActivateTeamIfUserNotInActiveTeamExecutedCorrecly.json")));

        mockMvc.perform(post(teamsFullActivateTeamUrl)
                .contentType(APPLICATION_JSON_UTF8)
                .content(jsonContentRequest))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    @UsingDataSet(locations = "/datasets/getAndDeactivateDataSet.json")
    public void deactivateTeamIfUserInTeamExecutedCorrectly() throws Exception {
        final String uuid = "uuid-in-one-team";
        mockMvc.perform(put(teamsFullDeactivateTeamUrl + uuid))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    @UsingDataSet(locations = "/datasets/getAndDeactivateDataSet.json")
    public void deactivateTeamIfUserNotInTeamExecutedCorrectly() throws Exception {
        final String uuid = "uuid-not-in-team";
        mockMvc.perform(put(teamsFullDeactivateTeamUrl + uuid))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

    @Test
    @UsingDataSet(locations = "/datasets/getAndDeactivateDataSet.json")
    public void deactivateTeamIfUserInSeveralTeamsExecutedCorrectly() throws Exception {
        final String uuid = "uuid-in-several-teams";
        mockMvc.perform(put(teamsFullDeactivateTeamUrl + uuid))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

    @Test
    @UsingDataSet(locations = "/datasets/getAllActiveTeamsDataSet.json")
    public void getAllTeamsExecutedCorrectly() throws Exception {
        mockMvc.perform(get(teamsFullGetAllTeamsUrl)
                .contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }
}