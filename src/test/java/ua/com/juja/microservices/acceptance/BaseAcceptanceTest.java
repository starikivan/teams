package ua.com.juja.microservices.acceptance;

import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;
import org.eclipse.jetty.http.HttpMethod;
import org.junit.Before;
import org.junit.Rule;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ua.com.juja.microservices.teams.Teams;

import static com.lordofthejars.nosqlunit.mongodb.MongoDbConfigurationBuilder.mongoDb;
import static io.restassured.RestAssured.given;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT, classes = {Teams.class})
@DirtiesContext
public class BaseAcceptanceTest {

    @LocalServerPort
    int localPort;
    private String mongoDbName = "teams";
    private String mongoDbHost = "127.0.0.1";
    private int mongoDbPort = 27017;
    @Rule
    public MongoDbRule mongoDbRule = new MongoDbRule(
            mongoDb()
                    .databaseName(mongoDbName)
                    .host(mongoDbHost)
                    .port(mongoDbPort)
                    .build()
    );

    @Before
    public void setup() {
        RestAssured.port = localPort;
        RestAssured.baseURI = "http://localhost";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    void printConsoleReport(String url, String expectedResponse, ResponseBody actualResponse) {

        System.out.println("\n\n URL  - " + url);

        System.out.println("\n Actual Response :\n");
        actualResponse.prettyPrint();

        System.out.println("\nExpected Response :");
        System.out.println("\n" + expectedResponse + "\n\n");
    }

    Response getResponse(String url, String jsonContentRequest, HttpMethod method) {
        Response response = getCommonResponse(url, jsonContentRequest, method);
        return response
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    Response getRealResponse(String url, String jsonContentRequest, HttpMethod method) {
        Response response = getCommonResponse(url, jsonContentRequest, method);
        return response
                .then()
                .extract()
                .response();
    }

    private Response getCommonResponse(String url, String jsonContentRequest, HttpMethod method) {
        RequestSpecification specification = given()
                .contentType("application/json")
                .body(jsonContentRequest)
                .when();
        Response response;
        if (HttpMethod.POST == method) {
            response = specification.post(url);
        } else if (HttpMethod.GET == method) {
            response = specification.get(url);
        } else if (HttpMethod.PUT == method) {
            response = specification.put(url);
        } else {
            throw new RuntimeException("Unsupported HttpMethod in getResponse()");
        }
        return response;
    }
}
