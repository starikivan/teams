package juja.microservices.teams.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.hibernate.validator.constraints.NotEmpty;

@Getter
public class UserUuidRequest {
    @NotEmpty
    private String uuid;

    @JsonCreator
    public UserUuidRequest(@JsonProperty("from") String uuid) {
        this.uuid = uuid;
    }
}