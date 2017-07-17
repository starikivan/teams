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

import java.time.LocalDate;
import java.time.ZoneId;
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
    private Set<String> members;

    @JsonProperty("activateDate")
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private Date activateDate;

    @JsonProperty("deactivateDate")
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private Date deactivateDate;

    @JsonCreator
    public Team(@JsonProperty("members") Set<String> members) {
        this.members = members;
        this.activateDate = Date.from(LocalDate.now().
                atStartOfDay(ZoneId.systemDefault()).toInstant());
        this.deactivateDate = Date.from(LocalDate.now().plusMonths(1).plusDays(-1)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public void setDeactivateDate(LocalDate deactivateDate) {
        this.deactivateDate = Date.from(deactivateDate.
                atStartOfDay(ZoneId.systemDefault()).toInstant());
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
