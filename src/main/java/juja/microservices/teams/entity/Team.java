package juja.microservices.teams.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;

/**
 * @author Andrii.Sidun
 * @author Ivan Shapovalov
 */
@Getter
@Setter
@Data
public class Team {

    @Id
    private String id;
    private Set<String> uuids;

    @JsonProperty("activatetDate")
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private Date activatetDate;

    @JsonProperty("deactivateDate")
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private Date deactivateDate;

    @JsonCreator
    public Team(@JsonProperty("uuids") Set<String> uuids) {
        this.uuids = uuids;
        this.activatetDate = Date.from(LocalDate.now().
                atStartOfDay(ZoneId.systemDefault()).toInstant());
        this.deactivateDate = Date.from(LocalDate.now().plusMonths(1)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public void setDeactivateDate(LocalDate deactivateDate) {
        this.deactivateDate = Date.from(deactivateDate.
                atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    @Override
    public String toString() {
        return "{" +
                "\"uuids\":" + uuids +
                ", activatetDate=" + activatetDate +
                ", deactivateDate=" + deactivateDate +
                '}';
    }
}
