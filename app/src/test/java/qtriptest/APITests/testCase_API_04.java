package qtriptest.APITests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class testCase_API_04 {

    @Test(groups = "API Tests")
    public void registerUserAndCheckDuplicate() {
        RestAssured.baseURI = "https://content-qtripdynamic-qa-backend.azurewebsites.net";
        RequestSpecification httpRequest = RestAssured.given().log().all();

        String username = String.format("subjeet_%s@yopmail.com", UUID.randomUUID().toString());

        // Register new user
        JSONObject payloadObject = new JSONObject();
        payloadObject.put("email", username);
        payloadObject.put("password", "Tester@123");
        payloadObject.put("confirmpassword", "Tester@123");

        Response httpResponse = httpRequest.contentType(ContentType.JSON).body(payloadObject.toString()).when().post("/api/v1/register");
        httpResponse.prettyPrint();

        assertEquals(httpResponse.getStatusCode(), 201);

        String responseBody = httpResponse.getBody().asPrettyString();
        assertTrue(responseBody.contains("success"));

        // Try to register the same user again
        httpResponse = httpRequest.contentType(ContentType.JSON).body(payloadObject.toString()).when().post("/api/v1/register");
        httpResponse.prettyPrint();

        assertEquals(httpResponse.getStatusCode(), 400);

        responseBody = httpResponse.getBody().asPrettyString();
        System.out.println(responseBody);

        JSONObject responseObject = new JSONObject(responseBody);
        String message = responseObject.optString("message");
        assertEquals(message, "Email already exists", "Email already exists");

        assertTrue(responseBody.contains("Email already exist"));
    }
}
