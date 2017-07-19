package ua.com.juja.microservices.acceptance;

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

import static ua.com.juja.microservices.teams.service.Utils.convertToString;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;

@RunWith(SpringRunner.class)
public class TeamsAcceptanceTest extends BaseAcceptanceTest {

    @Value("${rest.api.version}")
    private String restApiVersion;

    private String TEAMS_DEACTIVATE_TEAM_URL;
    private String TEAMS_GET_TEAM_URL;
    private String TEAMS_ADD_TEAM_URL;

    @Before
    public void setup() {
        TEAMS_DEACTIVATE_TEAM_URL = "/" + restApiVersion + "/teams/users/";
        TEAMS_GET_TEAM_URL = "/" + restApiVersion + "/teams/users/";
        TEAMS_ADD_TEAM_URL = "/" + restApiVersion + "/teams";
    }

    @UsingDataSet(locations = "/datasets/addTeamIfUserNotInActiveTeam.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    @Test
    public void test_addTeamIfUserNotInActiveTeamExecutedCorrectly() throws IOException {
        String url = TEAMS_ADD_TEAM_URL;
        String jsonContentRequest = convertToString(resource("acceptance/request/requestAddTeamIfUserNotInActiveTeamExecutedCorrecly.json"));
        Response actualResponse = getRealResponse(url, jsonContentRequest, HttpMethod.POST);

        String result = actualResponse.asString();

        String jsonContentExpectedResponse = convertToString(
                resource("acceptance/response/responseAddTeamIfUserNotInActiveTeamExecutedCorrectly.json"));

        printConsoleReport(url, jsonContentExpectedResponse, actualResponse.body());
        assertThatJson(result).when(Option.IGNORING_ARRAY_ORDER).when(Option.IGNORING_EXTRA_FIELDS)
                .isEqualTo(jsonContentExpectedResponse);
    }

    @UsingDataSet(locations = "/datasets/addTeamIfUsersInAnotherActiveTeam.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    @Test
    public void test_addTeamIfUserInAnotherActiveTeamExecutedCorrectly() throws IOException {

        String url = TEAMS_ADD_TEAM_URL;
        String jsonContentRequest = convertToString(resource("acceptance/request/requestAddTeamIfUsersInActiveTeamThrowsExceptions.json"));
        String jsonContentControlResponse = convertToString(
                resource("acceptance/response/responseAddTeamIfUserInActiveTeamThrowsException.json"));

        Response actualResponse = getRealResponse(url, jsonContentRequest, HttpMethod.POST);

        String result = actualResponse.asString();
        assertThatJson(result).when(Option.IGNORING_ARRAY_ORDER).isEqualTo(jsonContentControlResponse);
    }

    @UsingDataSet(locations = "/datasets/getAndDeactivateDataSet.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    @Test
    public void test_deactivateTeamIfUserInTeamExecutedCorrectly() throws IOException {

        String uuid = "user-in-one-team";
        String url = TEAMS_DEACTIVATE_TEAM_URL + uuid;
        Response actualResponse = getRealResponse(url, "", HttpMethod.PUT);

        String result = actualResponse.asString();
        String jsonContentExpectedResponse = String.format(convertToString(
                resource("acceptance/response/responseGetDeactivateTeamIfUserInTeamExecutedCorrectly.json")),
                "", "");
        printConsoleReport(url, jsonContentExpectedResponse, actualResponse.body());

        assertThatJson(result).when(Option.IGNORING_ARRAY_ORDER)
                .when(Option.IGNORING_EXTRA_FIELDS)
                .isEqualTo(jsonContentExpectedResponse);
    }

    @UsingDataSet(locations = "/datasets/getAndDeactivateDataSet.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    @Test
    public void test_deactivateTeamIfUserNotInTeamThrowsException() throws IOException {

        String uuid = "user-not-in-team";
        String jsonContentExpectedResponse = convertToString(
                resource("acceptance/response/responseGetDeactivateTeamIfUserNotInTeamThrowsExeption.json"));

        String url = TEAMS_DEACTIVATE_TEAM_URL + uuid;
        Response actualResponse = getRealResponse(url, "", HttpMethod.PUT);

        printConsoleReport(url, jsonContentExpectedResponse, actualResponse.body());
        String result = actualResponse.asString();
        assertThatJson(result).when(Option.IGNORING_ARRAY_ORDER).isEqualTo(jsonContentExpectedResponse);
    }

    @UsingDataSet(locations = "/datasets/getAndDeactivateDataSet.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    @Test
    public void test_deactivateTeamIfUserInSeveralTeamsThrowsException() throws IOException {

        String uuid = "user-in-several-teams";
        String jsonContentExpectedResponse = convertToString(
                resource("acceptance/response/responseGetDeactivateTeamIfUserInSeveralTeamsThrowsExceptions.json"));

        String url = TEAMS_DEACTIVATE_TEAM_URL + uuid;
        Response actualResponse = getRealResponse(url, "", HttpMethod.PUT);

        printConsoleReport(url, jsonContentExpectedResponse, actualResponse.body());
        String result = actualResponse.asString();
        assertThatJson(result).when(Option.IGNORING_ARRAY_ORDER).isEqualTo(jsonContentExpectedResponse);
    }

    @UsingDataSet(locations = "/datasets/getAndDeactivateDataSet.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    @Test
    public void test_getTeamIfUserInTeamExecutedCorrectly() throws IOException {

        String uuid = "user-in-one-team";
        String url = TEAMS_GET_TEAM_URL + uuid;
        Response actualResponse = getRealResponse(url, "", HttpMethod.GET);

        String result = actualResponse.asString();
        String jsonContentExpectedResponse = String.format(convertToString(
                resource("acceptance/response/responseGetDeactivateTeamIfUserInTeamExecutedCorrectly.json")),
                "", "");
        printConsoleReport(url, jsonContentExpectedResponse, actualResponse.body());

        assertThatJson(result).when(Option.IGNORING_ARRAY_ORDER)
                .when(Option.IGNORING_EXTRA_FIELDS)
                .isEqualTo(jsonContentExpectedResponse);
    }

    @UsingDataSet(locations = "/datasets/getAndDeactivateDataSet.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    @Test
    public void test_getTeamIfUserInSeveralTeamsThrowsException() throws IOException {

        String uuid = "user-in-several-teams";
        String jsonContentExpectedResponse = convertToString(
                resource("acceptance/response/responseGetDeactivateTeamIfUserInSeveralTeamsThrowsExceptions.json"));

        String url = TEAMS_DEACTIVATE_TEAM_URL + uuid;
        Response actualResponse = getRealResponse(url, "", HttpMethod.GET);

        printConsoleReport(url, jsonContentExpectedResponse, actualResponse.body());
        String result = actualResponse.asString();
        assertThatJson(result).when(Option.IGNORING_ARRAY_ORDER).isEqualTo(jsonContentExpectedResponse);
    }

    @UsingDataSet(locations = "/datasets/getAndDeactivateDataSet.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    @Test
    public void test_getTeamIfUserNotInTeamThrowsException() throws IOException {

        String uuid = "user-not-in-team";
        String jsonContentExpectedResponse = convertToString(
                resource("acceptance/response/responseGetDeactivateTeamIfUserNotInTeamThrowsExeption.json"));

        String url = TEAMS_DEACTIVATE_TEAM_URL + uuid;
        Response actualResponse = getRealResponse(url, "", HttpMethod.GET);

        printConsoleReport(url, jsonContentExpectedResponse, actualResponse.body());
        String result = actualResponse.asString();
        assertThatJson(result).when(Option.IGNORING_ARRAY_ORDER).isEqualTo(jsonContentExpectedResponse);
    }
}
