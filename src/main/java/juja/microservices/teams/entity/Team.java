package juja.microservices.teams.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Andrii.Sidun
 * @author Ivan Shapovalov
 */
@Getter
@Setter
public class Team {

    @Id
    private String id;
    private String uuidOne;
    private String uuidTwo;
    private String uuidThree;
    private String uuidFour;

    @JsonProperty("startDate")
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private Date startDate;

    @JsonProperty("dismissDate")
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private Date dismissDate;

    @JsonCreator
    public Team(@JsonProperty("uuidOne") String uuidOne,
                @JsonProperty("uuidTwo") String uuidTwo,
                @JsonProperty("uuidThree") String uuidThree,
                @JsonProperty("uuidFour") String uuidFour) {
        this.uuidOne = uuidOne;
        this.uuidTwo = uuidTwo;
        this.uuidThree = uuidThree;
        this.uuidFour = uuidFour;
        this.startDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.add(Calendar.MONTH, 1);
        dismissDate = Date.from(cal.toInstant());
    }

    public void setDismissDate(LocalDateTime dismissDate) {
        this.dismissDate = Date.from(dismissDate.atZone(ZoneId.systemDefault()).toInstant());
    }

    @Override
    public String toString() {
       String lineSeparator = System.lineSeparator();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        String startDate = dateFormat.format(getStartDate().getTime());
//        String dismissDate = dateFormat.format(getDismissDate().getTime());

        return "Team:".concat(lineSeparator)
                .concat("uuidOne = ").concat(uuidOne).concat(lineSeparator)
                .concat("uuidTwo = ").concat(uuidTwo).concat(lineSeparator)
                .concat("uuidThree = ").concat(uuidThree).concat(lineSeparator)
                .concat("uuidFour = ").concat(uuidFour).concat(lineSeparator);
    }

}
