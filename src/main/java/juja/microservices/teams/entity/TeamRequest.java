package juja.microservices.teams.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author Andrii.Sidun
 */

@Getter
@ToString
public class TeamRequest {

    @NotEmpty
    private String uuidOne;
    @NotEmpty
    private String uuidTwo;
    @NotEmpty
    private String uuidThree;
    @NotEmpty
    private String uuidFour;

    @JsonCreator
    public TeamRequest(@JsonProperty("uuidOne") String uuidOne,
                       @JsonProperty("uuidTwo") String uuidTwo,
                       @JsonProperty("uuidThree") String uuidThree,
                       @JsonProperty("uuidFour") String uuidFour) {
        this.uuidOne = uuidOne;
        this.uuidTwo = uuidTwo;
        this.uuidThree = uuidThree;
        this.uuidFour = uuidFour;
    }
}
