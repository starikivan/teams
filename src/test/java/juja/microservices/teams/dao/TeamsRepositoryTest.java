package juja.microservices.teams.dao;

import juja.microservices.teams.entity.Team;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@WebMvcTest(TeamRepository.class)
public class TeamsRepositoryTest {

    @Inject
    @InjectMocks
    private TeamRepository teamRepository;

    @MockBean
    private MongoTemplate mongoTemplate;

    final String keeper = "f827811f-51e8-4fc4-a56d-aebcd2193bc4";
    final String notKeeper = "11111111-51e8-4fc4-a56d-aebcd2193bc4";
    final String uuidOne = "f827811f-51e8-4fc4-a56d-aebcd2193bc3";
    final String uuidOneInOtherTeam = "11111111-51e8-4fc4-a56d-aebcd2193bc3";
    final String uuidTwo = "f827811f-51e8-4fc4-a56d-aebcd2193bc2";
    final String uuidThree = "f827811f-51e8-4fc4-a56d-aebcd2193bc1";
    final String uuidFour = "f827811f-51e8-4fc4-a56d-aebcd2193bc0";

    @Test
    public void save(){
        //When
        doNothing().when(mongoTemplate).save(any(Team.class));
        //Then
        assertEquals(null, teamRepository.save(new Team(keeper, uuidOne, uuidTwo, uuidThree, uuidFour)));
    }

    @Test
    public void addTeamWhenCreatorIsNotKeeper(){

    }

    @Test
    public void addTeamWhenUserExistsInOtherTeam(){

    }

}