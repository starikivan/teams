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

import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;

@RunWith(SpringRunner.class)
public class TeamsAcceptanceTest extends BaseAcceptanceTest{

    @Value( "${rest.api.version}" )
    private String restApiVersion;

    //TODO - not works yet
    @UsingDataSet(locations = "/datasets/addTeam_userNotInActiveTeam.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    @Test
    public void test_addTeamUserNotInAnotherTeamExecutedCorrectly() throws IOException {

        //Given
        String jsonContentRequest = convertToString(resource("acceptance/request/addTeam.json"));
        String jsonContentControlResponse = convertToString(
                resource("acceptance/response/responseAddExistedUserException.json"));

        //When
        Response actualResponse = getRealResponse("/" + restApiVersion + "/teams/" + restApiVersion, jsonContentRequest, HttpMethod.POST);

        //Then
        String result = actualResponse.asString();
        assertThatJson(result).when(Option.IGNORING_ARRAY_ORDER).isNotEqualTo(jsonContentControlResponse);
    }

    //TODO - not works yet
    @UsingDataSet(locations = "/datasets/deactivateTeam_dataSet.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    @Test
    public void test_deactivateTeamUserInTeamExecutedCorrectly() throws IOException {

        String uuid="user-in-one-team";
        //Given
        String jsonContentControlResponse = convertToString(
                resource("acceptance/response/responseDeactivateTeamUserInTeam.json"));

        //When
        Response actualResponse = getRealResponse("/" + restApiVersion + "/teams/users/" + uuid,"", HttpMethod.PUT);

        //Then
        String result = actualResponse.asString();
        assertThatJson(result).when(Option.IGNORING_ARRAY_ORDER).isNotEqualTo(jsonContentControlResponse);
    }
}
