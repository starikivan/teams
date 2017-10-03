package ua.com.juja.microservices.teams.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ua.com.juja.microservices.teams.dao.KeeperRepository;
import ua.com.juja.microservices.teams.exceptions.ApiErrorMessage;
import ua.com.juja.microservices.teams.exceptions.KeeperExchangeException;
import ua.com.juja.microservices.teams.utils.Utils;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

/**
 * @author Ivan Shapovalov
 */
@Repository
@Profile({"production", "default"})
public class RestKeeperRepository implements KeeperRepository {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private RestTemplate restTemplate;

    @Value("${keepers.endpoint.getDirections}")
    private String keepersGetDirectionsUrl;

    @Override
    public List<String> getDirections(String uuid) {
        List<String> directions;
        String url = keepersGetDirectionsUrl + "/" + uuid;
        logger.debug("Send request to keepers repository '{}'", url);
        try {
            ResponseEntity<String[]> response = this.restTemplate.getForEntity(url, String[].class);
            directions = Arrays.asList(response.getBody());
        } catch (HttpClientErrorException ex) {
            ApiErrorMessage error = Utils.convertToApiError(ex);
            logger.warn("Keepers service returned error: [{}]", ex.getMessage());
            throw new KeeperExchangeException(error, "Keepers service returns error: " + ex.getMessage());
        }
        logger.debug("Received list of keeper's '{}' directions: {}", uuid, directions);
        return directions;
    }
}