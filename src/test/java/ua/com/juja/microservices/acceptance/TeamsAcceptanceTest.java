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
import ua.com.juja.microservices.Utils;

import java.io.IOException;

import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;

@RunWith(SpringRunner.class)
public class TeamsAcceptanceTest extends BaseAcceptanceTest {

    @Value("${teams.rest.api.version}")
    private String teamsRestApiVersion;
    @Value("${teams.baseURL}")
    private String teamsBaseUrl;
    @Value("${teams.endpoint.activateTeam}")
    private String teamsActivateTeamUrl;
    @Value("${teams.endpoint.deactivateTeam}")
    private String teamsDeactivateTeamUrl;
    @Value("${teams.endpoint.getTeam}")
    private String teamsGetTeamUrl;
    @Value("${teams.endpoint.getAllTeams}")
    private String teamsGetAllTeamsUrl;

    private String teamsFullActivateTeamUrl;
    private String teamsFullDeactivateTeamUrl;
    private String teamsFullGetTeamUrl;
    private String teamsFullGetAllTeamsUrl;

    @Before
    public void localSetup() {
        teamsFullActivateTeamUrl = "/" + teamsRestApiVersion + teamsBaseUrl + teamsActivateTeamUrl;
        teamsFullDeactivateTeamUrl = "/" + teamsRestApiVersion + teamsBaseUrl + teamsDeactivateTeamUrl + "/";
        teamsFullGetTeamUrl = "/" + teamsRestApiVersion + teamsBaseUrl + teamsGetTeamUrl + "/";
        teamsFullGetAllTeamsUrl = "/" + teamsRestApiVersion + teamsBaseUrl + teamsGetAllTeamsUrl + "/";
    }

    @UsingDataSet(locations = "/datasets/activateTeamIfUserNotInActiveTeam.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    @Test
    public void activateTeamIfUserNotInActiveTeamExecutedCorrectly() throws IOException {
        String url = teamsFullActivateTeamUrl;
        String jsonContentRequest = Utils.convertToString(resource
                ("acceptance/request/requestActivateTeamIfUserNotInActiveTeamExecutedCorrecly.json"));
        Response actualResponse = getRealResponse(url, jsonContentRequest, HttpMethod.POST);

        String result = actualResponse.asString();

        String jsonContentExpectedResponse = Utils.convertToString(
                resource("acceptance/response/responseActivateTeamIfUserNotInActiveTeamExecutedCorrectly.json"));

        printConsoleReport(url, jsonContentExpectedResponse, actualResponse.body());
        assertThatJson(result)
                .when(Option.IGNORING_ARRAY_ORDER)
                .when(Option.IGNORING_EXTRA_FIELDS)
                .isEqualTo(jsonContentExpectedResponse);
    }

    @UsingDataSet(locations = "/datasets/activateTeamIfUsersInAnotherActiveTeam.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    @Test
    public void activateTeamIfUserInAnotherActiveTeamExecutedCorrectly() throws IOException {

        String url = teamsFullActivateTeamUrl;
        String jsonContentRequest = Utils
                .convertToString(resource("acceptance/request/requestActivateTeamIfUsersInActiveTeamThrowsExceptions.json"));
        String jsonContentControlResponse = Utils.convertToString(
                resource("acceptance/response/responseActivateTeamIfUserInActiveTeamThrowsException.json"));

        Response actualResponse = getRealResponse(url, jsonContentRequest, HttpMethod.POST);

        String result = actualResponse.asString();
        assertThatJson(result)
                .when(Option.IGNORING_ARRAY_ORDER)
                .isEqualTo(jsonContentControlResponse);
    }

    @UsingDataSet(locations = "/datasets/getAndDeactivateDataSet.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    @Test
    public void deactivateTeamIfUserInTeamExecutedCorrectly() throws IOException {
        String uuid = "uuid-in-one-team";
        String url = teamsFullDeactivateTeamUrl + uuid;
        Response actualResponse = getRealResponse(url, "", HttpMethod.PUT);

        String result = actualResponse.asString();
        String jsonContentExpectedResponse = String.format(Utils.convertToString(
                resource("acceptance/response/responseGetDeactivateTeamIfUserInTeamExecutedCorrectly.json")),
                "", "");
        printConsoleReport(url, jsonContentExpectedResponse, actualResponse.body());

        assertThatJson(result).when(Option.IGNORING_ARRAY_ORDER)
                .when(Option.IGNORING_EXTRA_FIELDS)
                .isEqualTo(jsonContentExpectedResponse);
    }

    @UsingDataSet(locations = "/datasets/getAndDeactivateDataSet.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    @Test
    public void deactivateTeamIfUserNotInTeamThrowsException() throws IOException {
        String uuid = "uuid-not-in-team";
        String jsonContentExpectedResponse = Utils.convertToString(
                resource("acceptance/response/responseGetDeactivateTeamIfUserNotInTeamThrowsExeption.json"));
        String url = teamsFullDeactivateTeamUrl + uuid;

        Response actualResponse = getRealResponse(url, "", HttpMethod.PUT);

        printConsoleReport(url, jsonContentExpectedResponse, actualResponse.body());
        String result = actualResponse.asString();
        assertThatJson(result)
                .when(Option.IGNORING_ARRAY_ORDER)
                .isEqualTo(jsonContentExpectedResponse);
    }

    @UsingDataSet(locations = "/datasets/getAndDeactivateDataSet.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    @Test
    public void deactivateTeamIfUserInSeveralTeamsThrowsException() throws IOException {
        String uuid = "uuid-in-several-teams";
        String jsonContentExpectedResponse = Utils.convertToString(
                resource("acceptance/response/responseGetDeactivateTeamIfUserInSeveralTeamsThrowsExceptions.json"));
        String url = teamsFullDeactivateTeamUrl + uuid;

        Response actualResponse = getRealResponse(url, "", HttpMethod.PUT);

        printConsoleReport(url, jsonContentExpectedResponse, actualResponse.body());
        String result = actualResponse.asString();
        assertThatJson(result)
                .when(Option.IGNORING_ARRAY_ORDER)
                .isEqualTo(jsonContentExpectedResponse);
    }

    @UsingDataSet(locations = "/datasets/getAndDeactivateDataSet.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    @Test
    public void getTeamIfUserInTeamExecutedCorrectly() throws IOException {
        String uuid = "uuid-in-one-team";
        String url = teamsFullGetTeamUrl + uuid;
        String jsonContentExpectedResponse = String.format(Utils.convertToString(
                resource("acceptance/response/responseGetDeactivateTeamIfUserInTeamExecutedCorrectly.json")),
                "", "");

        Response actualResponse = getRealResponse(url, "", HttpMethod.GET);

        String result = actualResponse.asString();
        printConsoleReport(url, jsonContentExpectedResponse, actualResponse.body());

        assertThatJson(result).when(Option.IGNORING_ARRAY_ORDER)
                .when(Option.IGNORING_EXTRA_FIELDS)
                .isEqualTo(jsonContentExpectedResponse);
    }

    @UsingDataSet(locations = "/datasets/getAndDeactivateDataSet.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    @Test
    public void getTeamIfUserInSeveralTeamsThrowsException() throws IOException {
        String uuid = "uuid-in-several-teams";
        String jsonContentExpectedResponse = Utils.convertToString(
                resource("acceptance/response/responseGetDeactivateTeamIfUserInSeveralTeamsThrowsExceptions.json"));
        String url = teamsFullGetTeamUrl + uuid;

        Response actualResponse = getRealResponse(url, "", HttpMethod.GET);

        printConsoleReport(url, jsonContentExpectedResponse, actualResponse.body());
        String result = actualResponse.asString();
        assertThatJson(result)
                .when(Option.IGNORING_ARRAY_ORDER)
                .isEqualTo(jsonContentExpectedResponse);
    }

    @UsingDataSet(locations = "/datasets/getAndDeactivateDataSet.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    @Test
    public void test_getTeamIfUserNotInTeamThrowsException() throws IOException {
        String uuid = "uuid-not-in-team";
        String jsonContentExpectedResponse = Utils.convertToString(
                resource("acceptance/response/responseGetDeactivateTeamIfUserNotInTeamThrowsExeption.json"));
        String url = teamsFullGetTeamUrl + uuid;

        Response actualResponse = getRealResponse(url, "", HttpMethod.GET);

        printConsoleReport(url, jsonContentExpectedResponse, actualResponse.body());
        String result = actualResponse.asString();
        assertThatJson(result)
                .when(Option.IGNORING_ARRAY_ORDER)
                .isEqualTo(jsonContentExpectedResponse);
    }

    @UsingDataSet(locations = "/datasets/getAllActiveTeamsDataSet.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    @Test
    public void test_getAllTeamExecutedCorrectly() throws IOException {
        String url = teamsFullGetAllTeamsUrl;
        Response actualResponse = getRealResponse(url, "", HttpMethod.GET);

        String result = actualResponse.asString();
        String jsonContentExpectedResponse = String.format(Utils.convertToString(
                resource("acceptance/response/responseGetAllTeamsExecutedCorrectly.json")),
                "", "");
        printConsoleReport(url, jsonContentExpectedResponse, actualResponse.body());
        assertThatJson(result).when(Option.IGNORING_ARRAY_ORDER)
                .when(Option.IGNORING_EXTRA_FIELDS)
                .isEqualTo(jsonContentExpectedResponse);
    }
}
