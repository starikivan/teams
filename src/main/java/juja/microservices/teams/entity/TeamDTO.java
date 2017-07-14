package juja.microservices.teams.entity;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import juja.microservices.teams.exceptions.TeamsException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Ivan Shapovalov
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Slf4j
public class TeamDTO {

    @JsonProperty ("members")
    private Set<String> members;

    @JsonProperty ("activateDate")
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private Date activatetDate;

    @JsonProperty("deactivateDate")
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private Date deactivateDate;

    public TeamDTO(Team team) {
        this.members = new HashSet<>(team.getMembers());
        this.activatetDate= team.getActivatetDate();
        this.deactivateDate= team.getDeactivateDate();
    }

    @Override
    public String toString() {
        String json;
        ObjectMapper mapper = new ObjectMapper();
        try {
            json = mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            log.warn("Convert TeamDTO failed. TeamDTO <{}>", this.getMembers());
            throw new TeamsException(String.format("Convert TeamDTO failed. TeamDTO members '%s'",members));
        }
        return json;
    }
}
