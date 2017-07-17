package juja.microservices.teams.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import juja.microservices.teams.exceptions.TeamsException;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Set;

/**
 * @author Andrii.Sidun
 * @author Ivan Shapovalov
 */
@Data
@ToString
@Slf4j
public class Team {

    @Id
    private String id;
    @JsonProperty("members")
    private Set<String> members;

    @JsonProperty("activateDate")
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:00:00")
    private Date activateDate;

    @JsonProperty("deactivateDate")
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:00:00")
    private Date deactivateDate;

    @JsonCreator
    public Team(@JsonProperty("members") Set<String> members) {
        this.members = members;
        this.activateDate = Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC));
        this.deactivateDate = Date.from(LocalDateTime.now().plusMonths(1).minusSeconds(1)
               .toInstant(ZoneOffset.UTC));
    }

    public void setDeactivateDate(LocalDateTime ldt) {
        this.deactivateDate = Date.from(ldt.toInstant(ZoneOffset.UTC));
    }

    public String toJSON() {
        String json;
        ObjectMapper mapper = new ObjectMapper();
        try {
            json = mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            log.warn("Convert Team failed. Team <{}>", this.getMembers());
            throw new TeamsException(String.format("Convert Team failed. Team members '%s'",members));
        }
        return json;
    }
}
