package com.payconiq.api.poc;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.jayway.restassured.path.json.JsonPath;

import io.restassured.RestAssured;


public class GITCURDOperationTest {

	public static final String accessToken = "d9233d94ba85cc56670fce224c0d52c08ea65392";
	public static final String baseURL = "https://api.github.com/gists";

	io.restassured.path.json.JsonPath pathEvaluator = null;
	Object obj = null;
	com.jayway.restassured.response.Response gistIDRes = null;

	String gistIDVal = null;
	String resGistID = null;

	/**
	 * This method is used to set up the Base URI which will be used throughout the
	 * test suite. This BeforeCalss annotation will run before the first test method
	 * in the current class is invoked.
	 */
	@BeforeClass
	public void setUp() {
		RestAssured.baseURI = "https://api.github.com/gists";
	}

	/**
	 * This is a health check test case which check GIT health. It will return HTTP
	 * response code based on the health of the application.
	 * 
	 * @throws Exception
	 */
	@Test
	public void gitHealthCheck() throws Exception {
		given().when().get(baseURL).then().statusCode(200);
	}

	/**
	 * This positive test case creates a public GIST with the name Welcome.txt.
	 * Returns HTTP response code of 201 after the successful creation of GIST.
	 * Also, It tests whether the GIST is created with proper details or not
	 * Incase of error it will throw an HTTP error response code.
	 * 
	 * @throws Exception
	 */

	/*This will provide the provison to enable/disable test case based on requirement, 
	 * Also, Priority defines in which order/heirerachy test cases needs to be executed.*/

	@Test(enabled = true, priority = 1)
	public void createGists() throws Exception {
		String JSONString = "{\r\n  \"description\": \"First GIST File\",\r\n  \"public\": true,\r\n  \"files\": {\r\n    \"welcome.txt\": {\r\n      \"content\": \"Welcome to the world of GIST\"\r\n    }\r\n  }\r\n}";
		gistIDRes = given().body(JSONString).when().post(baseURL + "?access_token=" + accessToken).then()
				.body("owner.id", equalTo(40266552))
				.body("files.'welcome.txt'.filename", equalToIgnoringCase("welcome.txt"))
				.body("files.'welcome.txt'.language", equalToIgnoringCase("Text"))
				.body("files.'welcome.txt'.content", equalToIgnoringCase("Welcome to the world of GIST"))
				.body("description", equalToIgnoringCase("First GIST File"))
				.body(containsString("lodari"))
				.body("public", equalTo(true))
				.contentType("application/json").assertThat().statusCode(201).extract().response();

		String rBody = gistIDRes.asString();
		JsonPath jp = new JsonPath(rBody);
		resGistID = jp.getString("id");
	}

	/**
	 * This positive test case validates the Retrieve operation of CURD. This
	 * retrieves all the Gists available for the OAUTH Access token. Test case will
	 * fail incase if the retrieve operation is failed.
	 * 
	 * @throws Exception
	 */
	@Test(enabled = true, priority = 2)
	public void retrieveGists() throws Exception {
		given().when().get(baseURL + "?access_token=" + accessToken).then()
		.body(containsString("lodari"))
		.contentType("application/json").body(containsString("lodari")).statusCode(200);
	}

	/**
	 * This positive test case validates the Retrieve operation of CURD. This
	 * retrieves specific GIST based on the GIST_ID and validates the retrieved GIST response body. Test case will fail incase if
	 * the retrieve operation is failed.
	 * 
	 * @throws Exception
	 */
	@Test(enabled = true, priority = 3)
	public void retrieveSpecificGists() throws Exception {
		given().when().get(baseURL + "/" + resGistID + "?access_token=" + accessToken).then()
		.body(containsString("lodari")).contentType("application/json")
		.body("owner.id", equalTo(40266552))
		.body("id", equalToIgnoringCase(resGistID))
		.body("public", equalTo(true))
		.body("files.'welcome.txt'.filename", equalToIgnoringCase("welcome.txt"))
		.body("files.'welcome.txt'.language", equalToIgnoringCase("Text"))
		.body("description", equalToIgnoringCase("First GIST File"))
		.statusCode(200);
	}

	/**
	 * This positive test case validates the UPDATE operation of CURD. This will
	 * update the GIST description, File name, File content. GIST ID needs to be
	 * passed on which we are going to perform update/Edit operation. In the event
	 * of successful generation it returns HTTP response code of 200. In case of
	 * failure it will return HTTP error response code.
	 * 
	 * @throws Exception
	 */
	@Test(enabled = true, priority = 4)
	public void updateGists() throws Exception {
		String JSONString = "{\r\n  \"description\": \"Welcome to GIST World\",\r\n  \"files\": {\r\n    \"welcomeupdated.txt\": {\r\n      \"content\": \"Updated File\"\r\n    },\r\n    \"welcome.txt\": {\r\n      \"filename\": \"welcomeupdated.txt\",\r\n      \"content\": \"modified contents\"\r\n    },\r\n    \"welcomeupdated.txt\": {\r\n      \"content\": \"a new file\"\r\n    },\r\n    \"welcome.txt\": null\r\n  }\r\n}";
		given().body(JSONString).when().patch(baseURL + "/" + resGistID + "?access_token=" + accessToken).then()
		.body("owner.id", equalTo(40266552))
		.body("files.'welcomeupdated.txt'.filename", equalToIgnoringCase("welcomeupdated.txt"))
		.body("files.'welcomeupdated.txt'.language", equalToIgnoringCase("Text"))
		.body("description", equalToIgnoringCase("Welcome to GIST World"))
		.body(containsString("lodari"))
		.body("public", equalTo(true))
		.statusCode(200);
	}

	/**
	 * This positive test case validates the DELETE operation of CURD. This deletes
	 * the Gists for which GIST_ID is passed in the HTTP request. to execute this
	 * operation we need to pass both OAUTH access token & GIST_ID. If the GIST is
	 * deleted successfully than it returns HTTP response code 204. In case of
	 * failure it will return HTTP error response code.
	 * 
	 * @throws Exception
	 */
	@Test(enabled = true, priority = 5)
	public void deleteGists() throws Exception {
		given().when().delete(baseURL + "/" + resGistID + "?access_token=" + accessToken).then().statusCode(204);
	}

	/* ----------Negative Test Scenarios -----------------*/

	/**
	 * This retrieveDeletedGists method will try to retrieve the GIST which is already deleted by
	 * DELETE Operation in the previous step.
	 * @throws Exception
	 */
	@Test(enabled = false, priority = 6)
	public void retrieveDeletedGists() throws Exception {
		given().when().get(baseURL + "/" + resGistID + "?access_token=" + accessToken).then()
		.body("message", equalToIgnoringCase("Not Found"))
		.statusCode(404);
	}


	/**
	 * This retriGistsWithInvalidToken method will try to retrieve the GIST by using the invalid OAuth token
	 * @throws Exception
	 */
	@Test(enabled = true, priority = 7)
	public void retriWithInvalidToken() throws Exception {
		given().when().get(baseURL + "?access_token=d9233d94ba85cc56670fce224c0d52cea65392").then()
		.statusCode(401);
	}

	/**
	 * This retriWithInvalidGISTID method will try to retrieve the GIST by using the invalid OAuth token & Invalid GIST_ID
	 * @throws Exception
	 */
	@Test(enabled = true, priority = 8)
	public void retriWithInvalidGISTID() throws Exception {
		given().when().get(baseURL + "/" + resGistID + "?access_token=" + accessToken).then()
		.statusCode(404);
	}

	/**
	 * This retriWithInvalidTokenID method will try to retrieve the GIST by using the invalid OAuth token & Invalid GIST_ID
	 * @throws Exception
	 */
	@Test(enabled = true, priority = 9)
	public void retriWithInvalidTokenID() throws Exception {
		given().when().get(baseURL + "/" + resGistID + "?access_token=d9233d94ba85cc56670fce224c0d52cea65392").then()
		.statusCode(401);
	}


	/**
	 * This createGistInvalidBody method will try to create the GIST by using the invalid body parameters/syntax
	 * @throws Exception
	 */
	@Test(enabled = true, priority = 10)
	public void createGistInvalidBody() throws Exception {
		String JSONStringNeg = "{\r\n  \"description\": \"First GIST File\",\r\n  ,\r\n  \"files\": {\r\n    \"welcome.txt\": {\r\n      \"content\": \"Welcome to the world of GIST\"\r\n    }\r  }\r\n}";
		given().body(JSONStringNeg).when().post(baseURL + "?access_token=" + accessToken).then()
		.assertThat().statusCode(400);
	}
	
	/**
	 * This createGistInvalidToken method will try to create the GIST by using the invalid body parameters/syntax
	 * @throws Exception
	 */
	@Test(enabled = true, priority = 11)
	public void createGistInvalidToken() throws Exception {
		String bodyParameters= "{\r\n  \"description\": \"Welcome to GIST World\",\r\n  \"files\": {\r\n    \"welcomeupdated.txt\": {\r\n      \"content\": \"Updated File\"\r\n    },\r\n    \"welcome.txt\": {\r\n      \"filename\": \"welcomeupdated.txt\",\r\n      \"content\": \"modified contents\"\r\n    },\r\n    \"welcomeupdated.txt\": {\r\n      \"content\": \"a new file\"\r\n    },\r\n    \"welcome.txt\": null\r\n  }\r\n}";
		given().body(bodyParameters).when().post(baseURL + "?access_token=d9233d94ba85cc56670fce224c0d52c08ea65392").then()
		.assertThat().statusCode(422);
	}


	/**
	 * This method updateGistsNegativetry try to update the GIST which doesn't exist b using valid OAuth access token.
	 * @throws Exception
	 */
	@Test(enabled = true, priority = 11)
	public void updateGistsNegative() throws Exception {
		String JSONString = "{\r\n  \"description\": \"GIST WOrld\",\r\n  \"files\": {\r\n    \"HelloWorld.txt\": {\r\n      \"content\": \"Updated File\"\r\n    },\r\n    \"Hello.txt\": {\r\n      \"filename\": \"HelloWorld.txt\",\r\n      \"content\": \"modified contents\"\r\n    },\r\n    \"Hello World.txt\": {\r\n      \"content\": \"a new file\"\r\n    },\r\n    \"Hello.txt\": null\r\n  }\r\n}";
		given().body(JSONString).when().patch(baseURL + "/" + resGistID + "?access_token=" + accessToken).then()
		.statusCode(404);
	}

}
