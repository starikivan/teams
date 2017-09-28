package ua.com.juja.microservices.teams.dao.impl;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import ua.com.juja.microservices.teams.dao.KeeperRepository;
import ua.com.juja.microservices.teams.exceptions.KeeperExchangeException;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * @author Ivan Shapovalov
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RestKeeperRepositoryTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Inject
    private KeeperRepository keeperRepository;
    @Inject
    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;

    @Value("${keepers.endpoint.getDirections}")
    private String keepersGetDirectionsUrl;

    @Value("${keepers.direction.teams}")
    private String teamsDirection;

    @Before
    public void setup() {
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
    }

    @Test
    public void getDirectionsExecutedCorrectly() {
        String uuid = "uuid";
        mockServer.expect(requestTo(keepersGetDirectionsUrl + "/" + uuid))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(
                        "[\"First direction\",\"Second direction\"]",
                        MediaType.APPLICATION_JSON));
        List<String> expected = Arrays.asList("First direction", "Second direction");

        List<String> actual = keeperRepository.getDirections(uuid);

        assertThat(actual, equalTo(expected));
    }

    @Test
    public void getDirectionsKeepersServiceReturnErrorThrowsException() {
        String uuid = "uuid";
        mockServer.expect(requestTo(keepersGetDirectionsUrl + "/" + uuid))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withBadRequest().body("bad request"));

        expectedException.expect(KeeperExchangeException.class);
        expectedException.expectMessage(containsString("Keepers service returns error"));

        keeperRepository.getDirections(uuid);
    }
}