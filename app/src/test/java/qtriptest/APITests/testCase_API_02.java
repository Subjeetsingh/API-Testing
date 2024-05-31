package qtriptest.APITests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class testCase_API_02 {

    @Test(groups = "API Tests")
    public void getSearchedCitiesAndCheckFailedResponses() {
        RestAssured.baseURI = "https://content-qtripdynamic-qa-backend.azurewebsites.net/";
        RequestSpecification httpRequest = RestAssured.given().log().all();

        // Test for successful search
        Response httpResponse = httpRequest.contentType(ContentType.JSON).when().get("/api/v1/cities?q=beng");
        httpResponse.prettyPrint();

        // Verify status code
        assertEquals(httpResponse.getStatusCode(), 200);

        // Parse response body
        JSONArray responseArray = new JSONArray(httpResponse.getBody().asString());

        // Verify the length of the result array is 1
        assertEquals(responseArray.length(), 1);

        if (responseArray.length() > 0) {
            JSONObject city = responseArray.getJSONObject(0);

            // Verify the description contains "100+ Places"
            String description = city.getString("description");
            assertTrue(description.contains("100+ Places"), "100+ Places");
        }
    }
}
