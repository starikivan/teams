package juja.microservices.teams.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.annotation.Id;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

@Data
@Getter
public class Team {

    @Id
    private String id;
    private String from;
    private String uuidOne;
    private String uuidTwo;
    private String uuidThree;
    private String uuidFour;
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private Date startDate;
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private Date dismissDate;

    @JsonCreator
    public Team(@JsonProperty("from") String from,
                @JsonProperty("uuidOne") String uuidOne,
                @JsonProperty("uuidTwo") String uuidTwo,
                @JsonProperty("uuidThree") String uuidThree,
                @JsonProperty("uuidFour") String uuidFour,
                @JsonProperty("startDate") Date startDate) {
        this.from = from;
        this.uuidOne = uuidOne;
        this.uuidTwo = uuidTwo;
        this.uuidThree = uuidThree;
        this.uuidFour = uuidFour;
        this.startDate = startDate;
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.add(Calendar.MONTH, 1);
        dismissDate = Date.from(cal.toInstant());
    }
}
