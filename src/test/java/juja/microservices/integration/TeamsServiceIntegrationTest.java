package juja.microservices.integration;

import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import juja.microservices.teams.dao.TeamRepository;
import juja.microservices.teams.entity.Team;
import juja.microservices.teams.service.TeamService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

import static org.junit.Assert.*;

/**
 * Created by Andrii Sidun
 */
@RunWith(SpringRunner.class)
public class TeamsServiceIntegrationTest extends BaseIntegrationTest {

    final String creator = "f827811f-51e8-4fc4-a56d-aebcd2193bc4";
    final String uuidOne = "f827811f-51e8-4fc4-a56d-aebcd2193bc3";
    final String uuidTwo = "f827811f-51e8-4fc4-a56d-aebcd2193bc2";
    final String uuidThree = "f827811f-51e8-4fc4-a56d-aebcd2193bc1";
    final String uuidFour = "f827811f-51e8-4fc4-a56d-aebcd2193bc0";

    @Inject
    private TeamRepository teamRepository;

    @Inject
    private TeamService teamService;

    @Test
    @UsingDataSet(locations = "/datasets/oneTeamInDB.json")
    public void addTeam(){
        //given
        Team team = new Team(creator, uuidOne, uuidTwo, uuidThree, uuidFour);
        //when
        String id = teamRepository.add(team);
        //then
        assertNotNull(id);
    }

}
