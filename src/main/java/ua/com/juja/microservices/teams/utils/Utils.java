package ua.com.juja.microservices.teams.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.util.Collections;
import ua.com.juja.microservices.teams.exceptions.ApiErrorMessage;

/**
 * @author Ivan Shapovalov
 */
public class Utils {
    public static ApiErrorMessage convertToApiError(HttpClientErrorException httpClientErrorException) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(httpClientErrorException.getResponseBodyAsString(), ApiErrorMessage.class);
        } catch (IOException e) {
            return new ApiErrorMessage(
                    500, "BotInternalError",
                    "I'm, sorry. I cannot parse api error message from remote service :(",
                    "Cannot parse api error message from remote service",
                    e.getMessage(),
                    Collections.singletonList(httpClientErrorException.getMessage())
            );
        }
    }
}
