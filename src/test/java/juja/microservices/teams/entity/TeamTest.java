package juja.microservices.teams.entity;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * @author Andrii.Sidun
 */

public class TeamTest {

    @Test
    public void newTest() {
        //Given
        final String creator = "f827811f-51e8-4fc4-a56d-aebcd2193bc4";
        final String uuidOne = "f827811f-51e8-4fc4-a56d-aebcd2193bc3";
        final String uuidTwo = "f827811f-51e8-4fc4-a56d-aebcd2193bc2";
        final String uuidThree = "f827811f-51e8-4fc4-a56d-aebcd2193bc1";
        final String uuidFour = "f827811f-51e8-4fc4-a56d-aebcd2193bc0";
        final Date startDate = new Date();
        Date dismissDate = getDatePlusOneMonth(startDate);

        Team team = new Team(creator, uuidOne, uuidTwo, uuidThree, uuidFour);
        //Then
        assertEquals(null, team.getId());
        assertEquals(creator, team.getFrom());
        assertEquals(uuidOne, team.getUuidOne());
        assertEquals(uuidTwo, team.getUuidTwo());
        assertEquals(uuidThree, team.getUuidThree());
        assertEquals(uuidFour, team.getUuidFour());
        assertEquals(startDate.toString(), team.getStartDate().toString());
        assertEquals(dismissDate.toString(), team.getDismissDate().toString());
    }

    private Date getDatePlusOneMonth(Date startDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.add(Calendar.MONTH, 1);
        return Date.from(cal.toInstant());
    }

}