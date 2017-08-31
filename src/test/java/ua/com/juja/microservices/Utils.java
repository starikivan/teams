package ua.com.juja.microservices;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ua.com.juja.microservices.teams.entity.Team;
import ua.com.juja.microservices.teams.exceptions.TeamsException;

import java.io.IOException;
import java.io.Reader;

/**
 * @author Ivan Shapovalov
 */
@Slf4j
public class Utils {

    public static String convertToString(Reader reader) throws IOException {
        char[] arr = new char[8 * 1024];
        StringBuilder buffer = new StringBuilder();
        int numCharsRead;
        while ((numCharsRead = reader.read(arr, 0, arr.length)) != -1) {
            buffer.append(arr, 0, numCharsRead);
        }
        return buffer.toString();
    }

    public static String convertToJSON(Team team) {
        String json;
        ObjectMapper mapper = new ObjectMapper();
        try {
            json = mapper.writeValueAsString(team);
        } catch (JsonProcessingException e) {
            log.warn("Convert Team failed. Team <{}>", team.getMembers());
            throw new TeamsException(String.format("Convert Team failed. Team members '%s'", team.getMembers()));
        }
        return json;
    }
}
