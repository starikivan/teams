package juja.microservices.teams.controller;

import juja.microservices.teams.entity.TeamRequest;
import juja.microservices.teams.service.TeamService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Andrii Sidun
 */


@RunWith(SpringRunner.class)
@WebMvcTest(TeamController.class)
public class TeamsControllerTest {

    @Inject
    private MockMvc mockMvc;

    @MockBean
    private TeamService teamService;

    @Inject
    @InjectMocks
    private TeamController teamController;

    @Value( "${rest.api.version}" )
    private String restApiVersion;

    @Value("classpath:/acceptance/request/addTeam.json")
    private Resource addTeamJsonFile;

    @Test
    public void addTeam() throws Exception {
        //Given
        String jsonContentRequest = new String(Files.readAllBytes(Paths.get(addTeamJsonFile.getURI())), StandardCharsets.UTF_8);
        final String someId = "SomeId";
        final String expectedId = "[\"SomeId\"]";
        //When
        when(teamService.addTeam(any(TeamRequest.class))).thenReturn(someId);
        String result = getResult("/" + restApiVersion + "/teams", jsonContentRequest);
        //Then
        assertEquals(expectedId, result);
    }

    private String getResult(String uri, String jsonContentRequest) throws Exception {
        return mockMvc.perform(post(uri)
                .contentType(APPLICATION_JSON_UTF8)
                .content(jsonContentRequest))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }
}