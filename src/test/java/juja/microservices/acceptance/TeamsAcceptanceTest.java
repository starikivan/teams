package juja.microservices.acceptance;

import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import io.restassured.response.Response;
import net.javacrumbs.jsonunit.core.Option;
import org.eclipse.jetty.http.HttpMethod;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;

@RunWith(SpringRunner.class)
public class TeamsAcceptanceTest extends BaseAcceptanceTest{

    @Value( "${rest.api.version}" )
    private String restApiVersion;

    @UsingDataSet(locations = "/datasets/addTeam_userNotInActiveTeam.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    @Test
    public void test_addTeamIfUserNotInActiveTeamExecutedCorrectly() throws IOException {

        ZonedDateTime activateDate= LocalDate.now().
                atStartOfDay(ZoneId.systemDefault());
        ZonedDateTime deactivateDate= LocalDate.now().plusMonths(1).plusDays(-1).
                atStartOfDay(ZoneId.systemDefault());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String jsonContentRequest = convertToString(resource("acceptance/request/requestAddTeamIfUserNotInActiveTeamExecutedCorrecly.json"));
        String jsonContentControlResponse = String.format(convertToString(
                resource("acceptance/response/responseAddTeamIfUserNotInActiveTeamExecutedCorrectly.json")),
                activateDate.format(formatter),deactivateDate.format(formatter));

        Response actualResponse = getRealResponse("/" + restApiVersion + "/teams/" , jsonContentRequest, HttpMethod.POST);

        String result = actualResponse.asString();
        assertThatJson(result).when(Option.IGNORING_ARRAY_ORDER)
                .isEqualTo(jsonContentControlResponse);
    }

    @UsingDataSet(locations = "/datasets/addTeam_userInAnotherActiveTeam.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    @Test
    public void test_addTeamIfUserInAnotherActiveTeamExecutedCorrectly() throws IOException {

        String jsonContentRequest = convertToString(resource("acceptance/request/requestAddTeamIfUserInActiveTeamThrowsExceptions.json"));
        String jsonContentControlResponse = convertToString(
                resource("acceptance/response/responseAddTeamIfUserInActiveTeamThrowsException.json"));

        Response actualResponse = getRealResponse("/" + restApiVersion + "/teams/" + restApiVersion, jsonContentRequest, HttpMethod.POST);

        String result = actualResponse.asString();
        assertThatJson(result).when(Option.IGNORING_ARRAY_ORDER).isEqualTo(jsonContentControlResponse);
    }

    @UsingDataSet(locations = "/datasets/deactivateTeam_dataSet.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    @Test
    public void test_deactivateTeamIfUserInTeamExecutedCorrectly() throws IOException {

        String uuid="user-in-one-team";
        String jsonContentControlResponse = convertToString(
                resource("acceptance/response/responseDeactivateTeamIfUserInTeamExecutedCorrectly.json"));

        Response actualResponse = getRealResponse("/" + restApiVersion + "/teams/users/" + uuid,"", HttpMethod.PUT);

        String result = actualResponse.asString();
        assertThatJson(result).when(Option.IGNORING_ARRAY_ORDER).isEqualTo(jsonContentControlResponse);
    }

    @UsingDataSet(locations = "/datasets/deactivateTeam_dataSet.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    @Test
    public void test_deactivateTeamIfUserNotInTeamExecutedCorrectly() throws IOException {

        String uuid="user-not-in-team";
        String jsonContentControlResponse = convertToString(
                resource("acceptance/response/responseDeactivateTeamIfUserNotInTeamThrowsExeption.json"));

        Response actualResponse = getRealResponse("/" + restApiVersion + "/teams/users/" + uuid,"", HttpMethod.PUT);

        String result = actualResponse.asString();
        assertThatJson(result).when(Option.IGNORING_ARRAY_ORDER).isEqualTo(jsonContentControlResponse);
    }

    @UsingDataSet(locations = "/datasets/deactivateTeam_dataSet.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    @Test
    public void test_deactivateTeamIfUserInSeveralTeamsExecutedCorrectly() throws IOException {

        String uuid="user-in-several-teams";
        String jsonContentControlResponse = convertToString(
                resource("acceptance/response/responseDeactivateTeamIfUserInSeveralTeamsThrowsExceptions.json"));

        Response actualResponse = getRealResponse("/" + restApiVersion + "/teams/users/" + uuid,"", HttpMethod.PUT);

        String result = actualResponse.asString();
        assertThatJson(result).when(Option.IGNORING_ARRAY_ORDER).isEqualTo(jsonContentControlResponse);
    }
}
