package juja.microservices.acceptance;

import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import io.restassured.response.Response;
import net.javacrumbs.jsonunit.core.Option;
import org.eclipse.jetty.http.HttpMethod;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static java.time.ZoneOffset.UTC;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;

@RunWith(SpringRunner.class)
public class TeamsAcceptanceTest extends BaseAcceptanceTest {

    @Value("${rest.api.version}")
    private String restApiVersion;

    private String TEAMS_DEACTIVATE_TEAM_URL;
    private String TEAMS_ADD_TEAM_URL;

    @Before
    public void before() {
        TEAMS_DEACTIVATE_TEAM_URL = "/" + restApiVersion + "/teams/users/";
        TEAMS_ADD_TEAM_URL = "/" + restApiVersion + "/teams";
    }

    @UsingDataSet(locations = "/datasets/addTeam_userNotInActiveTeam.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    @Test
    public void test_addTeamIfUserNotInActiveTeamExecutedCorrectly() throws IOException {

        ZonedDateTime activateDate = ZonedDateTime.ofInstant(LocalDateTime.now().toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
        ZonedDateTime deactivateDate = ZonedDateTime.ofInstant(LocalDateTime.now().plusMonths(1).minusMinutes(1)
                .toInstant(ZoneOffset.UTC), ZoneOffset.UTC);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:00:00");
        String url = TEAMS_ADD_TEAM_URL;
        String jsonContentRequest = convertToString(resource("acceptance/request/requestAddTeamIfUserNotInActiveTeamExecutedCorrecly.json"));
        String jsonContentExpectedResponse = String.format(convertToString(
                resource("acceptance/response/responseAddTeamIfUserNotInActiveTeamExecutedCorrectly.json")),
                activateDate.format(formatter), deactivateDate.format(formatter));

        Response actualResponse = getRealResponse(url, jsonContentRequest, HttpMethod.POST);
        printConsoleReport(url, jsonContentExpectedResponse, actualResponse.body());

        String result = actualResponse.asString();
        assertThatJson(result).when(Option.IGNORING_ARRAY_ORDER).when(Option.IGNORING_EXTRA_FIELDS)
                .isEqualTo(jsonContentExpectedResponse);
    }

    @UsingDataSet(locations = "/datasets/addTeam_userInAnotherActiveTeam.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    @Test
    public void test_addTeamIfUserInAnotherActiveTeamExecutedCorrectly() throws IOException {

        String url = TEAMS_ADD_TEAM_URL;
        String jsonContentRequest = convertToString(resource("acceptance/request/requestAddTeamIfUserInActiveTeamThrowsExceptions.json"));
        String jsonContentControlResponse = convertToString(
                resource("acceptance/response/responseAddTeamIfUserInActiveTeamThrowsException.json"));

        Response actualResponse = getRealResponse(url, jsonContentRequest, HttpMethod.POST);

        String result = actualResponse.asString();
        assertThatJson(result).when(Option.IGNORING_ARRAY_ORDER).isEqualTo(jsonContentControlResponse);
    }

    @UsingDataSet(locations = "/datasets/deactivateTeam_dataSet.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    @Test
    public void test_deactivateTeamIfUserInTeamExecutedCorrectly() throws IOException {

        ZonedDateTime activateDate = ZonedDateTime.ofInstant(LocalDateTime.now().toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
        ZonedDateTime deactivateDate = ZonedDateTime.ofInstant(LocalDateTime.now().plusMonths(1).minusMinutes(1)
                .toInstant(ZoneOffset.UTC), ZoneOffset.UTC);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:00:00");
        String uuid = "user-in-one-team";
        String jsonContentExpectedResponse = String.format(convertToString(
                resource("acceptance/response/responseDeactivateTeamIfUserInTeamExecutedCorrectly.json")),
                activateDate.format(formatter), deactivateDate.format(formatter));


        String url = TEAMS_DEACTIVATE_TEAM_URL + uuid;
        Response actualResponse = getRealResponse(url, "", HttpMethod.PUT);

        printConsoleReport(url, jsonContentExpectedResponse, actualResponse.body());
        String result = actualResponse.asString();
        assertThatJson(result).when(Option.IGNORING_ARRAY_ORDER).when(Option.IGNORING_EXTRA_FIELDS).isEqualTo(jsonContentExpectedResponse);
    }

    @UsingDataSet(locations = "/datasets/deactivateTeam_dataSet.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    @Test
    public void test_deactivateTeamIfUserNotInTeamExecutedCorrectly() throws IOException {

        String uuid = "user-not-in-team";
        String jsonContentExpectedResponse = convertToString(
                resource("acceptance/response/responseDeactivateTeamIfUserNotInTeamThrowsExeption.json"));

        String url = TEAMS_DEACTIVATE_TEAM_URL + uuid;
        Response actualResponse = getRealResponse(url, "", HttpMethod.PUT);

        printConsoleReport(url, jsonContentExpectedResponse, actualResponse.body());
        String result = actualResponse.asString();
        assertThatJson(result).when(Option.IGNORING_ARRAY_ORDER).isEqualTo(jsonContentExpectedResponse);
    }

    @UsingDataSet(locations = "/datasets/deactivateTeam_dataSet.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    @Test
    public void test_deactivateTeamIfUserInSeveralTeamsExecutedCorrectly() throws IOException {

        String uuid = "user-in-several-teams";
        String jsonContentExpectedResponse = convertToString(
                resource("acceptance/response/responseDeactivateTeamIfUserInSeveralTeamsThrowsExceptions.json"));

        String url = TEAMS_DEACTIVATE_TEAM_URL + uuid;
        Response actualResponse = getRealResponse(url, "", HttpMethod.PUT);

        printConsoleReport(url, jsonContentExpectedResponse, actualResponse.body());
        String result = actualResponse.asString();
        assertThatJson(result).when(Option.IGNORING_ARRAY_ORDER).isEqualTo(jsonContentExpectedResponse);
    }
}
