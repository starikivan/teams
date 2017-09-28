package ua.com.juja.microservices.teams.entity.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.NotEmpty;
import ua.com.juja.microservices.teams.entity.TeamRequest;

/**
 * @author Ivan Shapovalov
 */
@ToString
@Slf4j
@Getter
public class DeactivateTeamRequest implements TeamRequest {
    @NotEmpty
    @JsonProperty("from")
    private final String from;
    @NotEmpty
    @JsonProperty("uuid")
    private final String uuid;

    public DeactivateTeamRequest(@JsonProperty("from") String from,@JsonProperty("uuid")  String uuid) {
        this.from = from;
        this.uuid = uuid;
    }
}
