package ua.com.juja.microservices.integration;

import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import ua.com.juja.microservices.teams.service.Utils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Ivan Shapovalov
 * @author Andrii Sidun
 */
@RunWith(SpringRunner.class)
public class TeamControllerIntegrationTest extends BaseIntegrationTest {

    @Value("${rest.api.version}")
    private String restApiVersion;

    private String TEAMS_DEACTIVATE_TEAM_URL;
    private String TEAMS_ADD_TEAM_URL;

    private MockMvc mockMvc;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        TEAMS_DEACTIVATE_TEAM_URL = "/" + restApiVersion + "/teams/users/";
        TEAMS_ADD_TEAM_URL = "/" + restApiVersion + "/teams";
    }

    @Test
    @UsingDataSet(locations = "/datasets/addTeamIfUserNotInActiveTeam.json")
    public void test_addTeamIfUserNotInActiveTeamExecutedCorrectly() throws Exception {
        String jsonContentRequest = Utils.convertToString((resource
                ("acceptance/request/requestAddTeamIfUserNotInActiveTeamExecutedCorrecly.json")));

        mockMvc.perform(post(TEAMS_ADD_TEAM_URL)
                .contentType(APPLICATION_JSON_UTF8)
                .content(jsonContentRequest))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    @UsingDataSet(locations = "/datasets/getAndDeactivateDataSet.json")
    public void test_deactivateTeamIfUserInTeamExecutedCorrectly() throws Exception {
        final String uuid = "user-in-one-team";
        mockMvc.perform(put(TEAMS_DEACTIVATE_TEAM_URL + uuid))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    @UsingDataSet(locations = "/datasets/getAndDeactivateDataSet.json")
    public void test_deactivateTeamIfUserNotInTeamExecutedCorrectly() throws Exception {
        final String uuid = "user-not-in-team";
        mockMvc.perform(put(TEAMS_DEACTIVATE_TEAM_URL + uuid))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

    @Test
    @UsingDataSet(locations = "/datasets/getAndDeactivateDataSet.json")
    public void test_deactivateTeamIfUserInSeveralTeamsExecutedCorrectly() throws Exception {
        final String uuid = "user-in-several-teams";
        mockMvc.perform(put(TEAMS_DEACTIVATE_TEAM_URL + uuid))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }
}