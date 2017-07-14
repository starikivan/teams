package juja.microservices.teams.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Collections;
import java.util.Set;

/**
 * @author Andrii.Sidun
 * @author Ivan Shapovalov
 */
@Getter
@ToString
public class TeamRequest {

    @NotEmpty
    private Set<String> uuids;

    @JsonCreator
    public TeamRequest(@JsonProperty("uuids") Set<String> uuids) {
        this.uuids = uuids;
    }
}
