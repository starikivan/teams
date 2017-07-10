package juja.microservices.teams.entity;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * @author Andrii.Sidun
 */

public class TeamRequestTest {

    @Test
    public void newTeamRequest() {
        //Given
        final String creator = "f827811f-51e8-4fc4-a56d-aebcd2193bc4";
        final String uuidOne = "f827811f-51e8-4fc4-a56d-aebcd2193bc3";
        final String uuidTwo = "f827811f-51e8-4fc4-a56d-aebcd2193bc2";
        final String uuidThree = "f827811f-51e8-4fc4-a56d-aebcd2193bc1";
        final String uuidFour = "f827811f-51e8-4fc4-a56d-aebcd2193bc0";

        TeamRequest team = new TeamRequest(creator, uuidOne, uuidTwo, uuidThree, uuidFour);
        //Then
        assertEquals(null, team.getId());
        assertEquals(creator, team.getFrom());
        assertEquals(uuidOne, team.getUuidOne());
        assertEquals(uuidTwo, team.getUuidTwo());
        assertEquals(uuidThree, team.getUuidThree());
        assertEquals(uuidFour, team.getUuidFour());
    }

}