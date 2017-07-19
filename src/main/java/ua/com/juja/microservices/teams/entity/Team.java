package ua.com.juja.microservices.teams.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;

import ua.com.juja.microservices.teams.exceptions.TeamsException;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;

import java.time.*;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private Date activateDate;

    @JsonProperty("deactivateDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private Date deactivateDate;

    @JsonCreator
    public Team(@JsonProperty("members") Set<String> members) {
        this.members = members;
        this.activateDate = Date.from(LocalDateTime.of(LocalDate.now(), LocalTime.MIN).atZone(ZoneId.systemDefault()).toInstant());
        this.deactivateDate = Date.from(LocalDateTime.of(LocalDate.now().plusMonths(1).minusDays(1), LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
    }

    public String toJSON() {
        String json;
        ObjectMapper mapper = new ObjectMapper();
        try {
            json = mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            log.warn("Convert Team failed. Team <{}>", this.getMembers());
            throw new TeamsException(String.format("Convert Team failed. Team members '%s'", members));
        }
        return json;
    }
}
