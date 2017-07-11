package juja.microservices.acceptance;

import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import com.mongodb.util.JSON;
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

    @Test
    @UsingDataSet(locations = "/datasets/oneTeamInDB.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void addTeam() throws IOException {

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

    @Test
    @UsingDataSet(locations = "/datasets/oneTeamInDBExists.json", loadStrategy = LoadStrategyEnum.DELETE_ALL)
    public void addTeamWhenUserExists() throws IOException {

        //Given
        String jsonContentRequest = convertToString(resource("acceptance/request/addTeam.json"));
        String jsonContentControlResponse = convertToString(
                resource("acceptance/response/responseAddExistedUserException.json"));

        //When
        Response actualResponse = getRealResponse("/" + restApiVersion + "/teams", jsonContentRequest, HttpMethod.POST);

        //Then
        String result = actualResponse.asString();
        assertThatJson(result).when(Option.IGNORING_ARRAY_ORDER).isEqualTo(jsonContentControlResponse);
    }

}
