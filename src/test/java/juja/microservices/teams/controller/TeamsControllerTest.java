package juja.microservices.teams.controller;

import juja.microservices.teams.entity.Team;
import juja.microservices.teams.exceptions.UserInSeveralTeamsException;
import juja.microservices.teams.exceptions.UserNotInTeamException;
import juja.microservices.teams.service.TeamService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Andrii Sidun
 * @author Ivan Shapovalov
 */
@RunWith(SpringRunner.class)
@WebMvcTest(TeamController.class)
public class TeamsControllerTest {

    @Inject
    private MockMvc mockMvc;

    @MockBean
    private TeamService service;

    @Test
    public void test_dismissTeamUserInTeamExecutedCorrectly()  throws Exception {
        //Given
        final String uuid = "user-in-team";
        final Team team=new Team(uuid,"","","");
        //When
        when(service.dismissTeam(uuid)).thenReturn(team);
        String result=getGoodResult("/v1/teams/users/"+uuid);
        //Then
        verify(service).dismissTeam(uuid);
        Mockito.verifyNoMoreInteractions(service);
        assertEquals(team.toString(), result);
    }

    @Test
    public void test_dismissTeamUserNotInTeamBadResponce() throws Exception {
        //Given
        final String uuid = "user-not-in-team";
        //When
        when(service.dismissTeam(uuid)).thenThrow(new UserNotInTeamException(String.format("User with uuid '%s' not in team now",uuid)));
        //Then
        Mockito.verifyNoMoreInteractions(service);
        getBadResult("/v1/teams/users/"+uuid);
    }

    @Test
    public void test_dismissTeamUserInSeveralTeamsBadResponce() throws Exception {
        //Given
        final String uuid = "user-in-several-teams";
        //When
        when(service.dismissTeam(uuid)).thenThrow(new UserInSeveralTeamsException(String.format("User with uuid '%s' is in several teams now", uuid)));
        //Then
        Mockito.verifyNoMoreInteractions(service);
        getBadResult("/v1/teams/users/"+uuid);
    }

    private String getGoodResult(String uri) throws Exception {
        return mockMvc.perform(put(uri))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }

    private void getBadResult(String uri) throws Exception {
        mockMvc.perform(put(uri))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
    }

}