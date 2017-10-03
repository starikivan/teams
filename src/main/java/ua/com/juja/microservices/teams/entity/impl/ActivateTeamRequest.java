package ua.com.juja.microservices.teams.entity.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.NotEmpty;
import ua.com.juja.microservices.teams.entity.TeamRequest;

import java.util.Collections;
import java.util.Set;

/**
 * @author Andrii.Sidun
 * @author Ivan Shapovalov
 */
@ToString
@Slf4j
@Getter
public class ActivateTeamRequest implements TeamRequest {

    @NotEmpty
    private String from;

    @NotEmpty
    private Set<String> members;

    @JsonCreator
    public ActivateTeamRequest(@JsonProperty("from") String from, @JsonProperty("members") Set<String> members) {
        this.from = from;
        this.members = members;
    }

    public Set<String> getMembers() {
        return Collections.unmodifiableSet(members);
    }
}
