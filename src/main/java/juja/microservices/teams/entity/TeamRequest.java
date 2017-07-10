package juja.microservices.teams.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

/**
 * @author Andrii.Sidun
 */

@Getter
@ToString
public class TeamRequest {

    @Id
    private String id;
    private String from;
    private String uuidOne;
    private String uuidTwo;
    private String uuidThree;
    private String uuidFour;

    @JsonCreator
    public TeamRequest(@JsonProperty("from") String from,
                @JsonProperty("uuidOne") String uuidOne,
                @JsonProperty("uuidTwo") String uuidTwo,
                @JsonProperty("uuidThree") String uuidThree,
                @JsonProperty("uuidFour") String uuidFour) {
        this.from = from;
        this.uuidOne = uuidOne;
        this.uuidTwo = uuidTwo;
        this.uuidThree = uuidThree;
        this.uuidFour = uuidFour;
    }
}
