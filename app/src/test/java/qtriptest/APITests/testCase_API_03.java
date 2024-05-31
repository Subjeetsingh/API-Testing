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

public class testCase_API_03 {

    @Test(groups = "API Tests", priority = 1)
    public void newRegisterUser() {
        RestAssured.baseURI = "https://content-qtripdynamic-qa-backend.azurewebsites.net";
        RequestSpecification httpRequest = RestAssured.given().log().all();

        UUID randomUUID = UUID.randomUUID();
        String userName = String.format("Subjeet_%s@gmail.com", randomUUID.toString());
        System.out.println("Generated username: " + userName);

        // Register new user
        JSONObject payloadObject = new JSONObject();
        payloadObject.put("email", userName);
        payloadObject.put("password", "Tester@123");
        payloadObject.put("confirmpassword", "Tester@123");

        Response httpResponse = httpRequest.contentType(ContentType.JSON).body(payloadObject.toString()).when().post("/api/v1/register");
        httpResponse.prettyPrint();

        assertEquals(httpResponse.getStatusCode(), 201);
        String responseBody = httpResponse.getBody().asPrettyString();
        assertTrue(responseBody.contains("success"));

        // Login with the new user
        payloadObject.remove("confirmpassword");
        httpResponse = httpRequest.contentType(ContentType.JSON).body(payloadObject.toString()).when().post("/api/v1/login");
        String responseBodyLogin = httpResponse.getBody().asString();
        System.out.println("Login Response: " + responseBodyLogin);

        assertEquals(httpResponse.getStatusCode(), 201);
        assertTrue(responseBodyLogin.contains("success"));

        String userToken = httpResponse.jsonPath().getString("data.token");
        System.out.println("User Token: " + userToken);

        String id = httpResponse.jsonPath().getString("data.id");
        System.out.println("User ID: " + id);

        // Make a new reservation
        payloadObject = new JSONObject();
        payloadObject.put("userId", id);
        payloadObject.put("name", "subjeet");
        payloadObject.put("date", "2024-06-30");
        payloadObject.put("person", "2");
        payloadObject.put("adventure", "2447910730");
        String headerToken = "Bearer " + userToken.trim();

        httpResponse = httpRequest.contentType(ContentType.JSON).header("Authorization", headerToken)
                .body(payloadObject.toString()).post("/api/v1/reservations/new");

        String responseBodyBooking = httpResponse.getBody().asString();
        System.out.println("Booking Response: " + responseBodyBooking);

        int statusCodeBooking = httpResponse.getStatusCode();
        if (statusCodeBooking != 200) {
            System.out.println("Error Response: " + responseBodyBooking);
        }
        assertEquals(statusCodeBooking, 200);
        assertTrue(responseBodyBooking.contains("success"));

        // Retrieve reservations
        String url = "/api/v1/reservations";
        System.out.println("Fetching reservations with URL: " + url);

        httpResponse = httpRequest.queryParam("id", id).when().get(url);
        String responseBodyReservations = httpResponse.getBody().asString();
        int statusCodeReservations = httpResponse.getStatusCode();

        System.out.println("Reservations Status Code: " + statusCodeReservations);
        System.out.println("Reservations Response: " + responseBodyReservations);

        if (statusCodeReservations != 200) {
            System.out.println("Error Response: " + responseBodyReservations);
        }

        assertEquals(statusCodeReservations, 200, "Expected status code 200 but found " + statusCodeReservations + " with response: " + responseBodyReservations);
        assertTrue(responseBodyReservations.contains("_id"), "Response does not contain '_id'");
    }
}
