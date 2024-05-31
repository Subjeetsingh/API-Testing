package qtriptest.APITests;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.RestAssured;
import org.json.JSONObject;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.UUID;

public class testCase_API_01 {

    RequestSpecification httpRequest;
    Response httpResponse;
    JSONObject payloadObject;
    JSONObject responseObject;

    @Test(groups = "API Tests")
    public void register_New_User_Test() {
        SoftAssert soft = new SoftAssert();
        String userName = String.format("Subjeet_%s@gmail.com", UUID.randomUUID().toString());

        try {
            RestAssured.baseURI = "https://content-qtripdynamic-qa-backend.azurewebsites.net/";
            httpRequest = RestAssured.given().log().all();
            payloadObject = new JSONObject();  // Initialize payloadObject here
            
            System.out.println("Initializing payloadObject with email: " + userName);
            payloadObject.put("email", userName);  // This should not throw NullPointerException
            payloadObject.put("password", "Subjeet@123");
            payloadObject.put("confirmpassword", "Subjeet@123");

            httpResponse = httpRequest.contentType(ContentType.JSON).body(payloadObject.toString())
                    .when().post("/api/v1/register");

            httpResponse.prettyPrint();

            String responseString = httpResponse.asString();
            if (responseString != null && !responseString.isEmpty()) {
                responseObject = new JSONObject(responseString);
            } else {
                responseObject = new JSONObject();
            }

            System.out.println("Response Object: " + responseObject.toString());

            soft.assertEquals(httpResponse.getStatusCode(), 201, "Status code mismatch");
            soft.assertTrue(responseObject.has("success"), "Response does not contain 'success' key");
            soft.assertEquals(responseObject.optString("success"), "true", "Success value mismatch");
            soft.assertAll();
        } catch (Exception e) {
            e.printStackTrace();
            soft.fail("Exception occurred: " + e.getMessage());
        }
    }

    // @Test(groups = "API Tests", dependsOnMethods = "register_New_User_Test")
    // public void register_With_ExistingUser() {
    //     SoftAssert soft = new SoftAssert();
    //     try {
    //         RestAssured.baseURI = "https://content-qtripdynamic-qa-backend.azurewebsites.net/";
    //         httpRequest = RestAssured.given().log().all();
    //         payloadObject = new JSONObject();  // Re-initialize payloadObject before using it again
            
    //         payloadObject.put("email", "Subjeet@gmail.com");
    //         payloadObject.put("password", "Subjeet@123");
    //         payloadObject.put("confirmpassword", "Subjeet@123");

    //         httpResponse = httpRequest.contentType(ContentType.JSON).body(payloadObject.toString())
    //                 .when().post("/api/v1/register");

    //         httpResponse.prettyPrint();

    //         // Add debug statement to check httpResponse
    //         System.out.println("HTTP Response: " + httpResponse);

    //         String responseString = httpResponse.asString();
    //         if (responseString != null && !responseString.isEmpty()) {
    //             responseObject = new JSONObject(responseString);
    //         } else {
    //             responseObject = new JSONObject();
    //         }

    //         System.out.println("Response Object: " + responseObject.toString());

    //         soft.assertEquals(httpResponse.getStatusCode(), 400, "Status code mismatch");
    //         soft.assertTrue(responseObject.has("success"), "Response does not contain 'success' key");
    //         soft.assertEquals(responseObject.optString("success"), "false", "Success value mismatch");
    //         soft.assertEquals(responseObject.optString("message"), "Email already exists", "Message mismatch");
    //         soft.assertAll();
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         soft.fail("Exception occurred: " + e.getMessage());
    //     }
    // }
}
