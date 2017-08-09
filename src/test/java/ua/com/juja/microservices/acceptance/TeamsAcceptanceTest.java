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

    private String teamsFullActivateTeamUrl;
    private String teamsFullDeactivateTeamUrl;
    private String teamsFullGetTeamUrl;

    @Before
    public void localSetup() {
        teamsFullActivateTeamUrl = "/" + teamsRestApiVersion+teamsBaseUrl+teamsActivateTeamUrl;
        teamsFullDeactivateTeamUrl = "/" + teamsRestApiVersion + teamsBaseUrl + teamsDeactivateTeamUrl + "/";
        teamsFullGetTeamUrl = "/" + teamsRestApiVersion + teamsBaseUrl + teamsGetTeamUrl + "/";
    }

    @UsingDataSet(locations = "/datasets/activateTeamIfUserNotInActiveTeam.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    @Test
    public void test_activateTeamIfUserNotInActiveTeamExecutedCorrectly() throws IOException {
        String url = teamsFullActivateTeamUrl;
        String jsonContentRequest = Utils.convertToString(resource
                ("acceptance/request/requestActivateTeamIfUserNotInActiveTeamExecutedCorrecly.json"));
        Response actualResponse = getRealResponse(url, jsonContentRequest, HttpMethod.POST);

        String result = actualResponse.asString();

        String jsonContentExpectedResponse = Utils.convertToString(
                resource("acceptance/response/responseActivateTeamIfUserNotInActiveTeamExecutedCorrectly.json"));

        printConsoleReport(url, jsonContentExpectedResponse, actualResponse.body());
        assertThatJson(result).when(Option.IGNORING_ARRAY_ORDER).when(Option.IGNORING_EXTRA_FIELDS)
                .isEqualTo(jsonContentExpectedResponse);
    }

    @UsingDataSet(locations = "/datasets/activateTeamIfUsersInAnotherActiveTeam.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    @Test
    public void test_activateTeamIfUserInAnotherActiveTeamExecutedCorrectly() throws IOException {

        String url = teamsFullActivateTeamUrl;
        String jsonContentRequest = Utils
                .convertToString(resource("acceptance/request/requestActivateTeamIfUsersInActiveTeamThrowsExceptions.json"));
        String jsonContentControlResponse = Utils.convertToString(
                resource("acceptance/response/responseActivateTeamIfUserInActiveTeamThrowsException.json"));

        Response actualResponse = getRealResponse(url, jsonContentRequest, HttpMethod.POST);

        String result = actualResponse.asString();
        assertThatJson(result).when(Option.IGNORING_ARRAY_ORDER).isEqualTo(jsonContentControlResponse);
    }

    @UsingDataSet(locations = "/datasets/getAndDeactivateDataSet.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    @Test
    public void test_deactivateTeamIfUserInTeamExecutedCorrectly() throws IOException {

        String uuid = "user-in-one-team";
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
    public void test_deactivateTeamIfUserNotInTeamThrowsException() throws IOException {

        String uuid = "user-not-in-team";
        String jsonContentExpectedResponse = Utils.convertToString(
                resource("acceptance/response/responseGetDeactivateTeamIfUserNotInTeamThrowsExeption.json"));

        String url = teamsFullDeactivateTeamUrl + uuid;
        Response actualResponse = getRealResponse(url, "", HttpMethod.PUT);

        printConsoleReport(url, jsonContentExpectedResponse, actualResponse.body());
        String result = actualResponse.asString();
        assertThatJson(result).when(Option.IGNORING_ARRAY_ORDER).isEqualTo(jsonContentExpectedResponse);
    }

    @UsingDataSet(locations = "/datasets/getAndDeactivateDataSet.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    @Test
    public void test_deactivateTeamIfUserInSeveralTeamsThrowsException() throws IOException {

        String uuid = "user-in-several-teams";
        String jsonContentExpectedResponse = Utils.convertToString(
                resource("acceptance/response/responseGetDeactivateTeamIfUserInSeveralTeamsThrowsExceptions.json"));

        String url = teamsFullDeactivateTeamUrl + uuid;
        Response actualResponse = getRealResponse(url, "", HttpMethod.PUT);

        printConsoleReport(url, jsonContentExpectedResponse, actualResponse.body());
        String result = actualResponse.asString();
        assertThatJson(result).when(Option.IGNORING_ARRAY_ORDER).isEqualTo(jsonContentExpectedResponse);
    }

    @UsingDataSet(locations = "/datasets/getAndDeactivateDataSet.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    @Test
    public void test_getTeamIfUserInTeamExecutedCorrectly() throws IOException {

        String uuid = "user-in-one-team";
        String url = teamsFullGetTeamUrl + uuid;
        Response actualResponse = getRealResponse(url, "", HttpMethod.GET);

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
    public void test_getTeamIfUserInSeveralTeamsThrowsException() throws IOException {

        String uuid = "user-in-several-teams";
        String jsonContentExpectedResponse = Utils.convertToString(
                resource("acceptance/response/responseGetDeactivateTeamIfUserInSeveralTeamsThrowsExceptions.json"));

        String url = teamsFullGetTeamUrl + uuid;
        Response actualResponse = getRealResponse(url, "", HttpMethod.GET);

        printConsoleReport(url, jsonContentExpectedResponse, actualResponse.body());
        String result = actualResponse.asString();
        assertThatJson(result).when(Option.IGNORING_ARRAY_ORDER).isEqualTo(jsonContentExpectedResponse);
    }

    @UsingDataSet(locations = "/datasets/getAndDeactivateDataSet.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    @Test
    public void test_getTeamIfUserNotInTeamThrowsException() throws IOException {

        String uuid = "user-not-in-team";
        String jsonContentExpectedResponse = Utils.convertToString(
                resource("acceptance/response/responseGetDeactivateTeamIfUserNotInTeamThrowsExeption.json"));

        String url = teamsFullGetTeamUrl + uuid;
        Response actualResponse = getRealResponse(url, "", HttpMethod.GET);

        printConsoleReport(url, jsonContentExpectedResponse, actualResponse.body());
        String result = actualResponse.asString();
        assertThatJson(result).when(Option.IGNORING_ARRAY_ORDER).isEqualTo(jsonContentExpectedResponse);
    }
}
