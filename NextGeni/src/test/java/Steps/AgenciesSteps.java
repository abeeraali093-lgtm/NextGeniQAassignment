package Steps;

import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import io.restassured.path.json.JsonPath;
import io.restassured.http.ContentType;
import utils.SeleniumReporter;

public class AgenciesSteps {

    private final String BASE_URL = "https://automation-backend-ec08fe65847a.herokuapp.com/api/v1";
        private Response response;
        private String token;
        private String agencyId;
   // private static final SeleniumReporter reporter = new SeleniumReporter();
    private static final SeleniumReporter reporter = null;

    @When("I POST to login with email {string} and password {string}")
        public void postLogin(String email, String password) {
            RestAssured.baseURI = BASE_URL;
            response = given()
                    .contentType(ContentType.JSON)
                    .body("{\"email\":\"" + email + "\", \"password\":\"" + password + "\"}")
                    .post("/auth/login");
        SeleniumReporter.logApiCall("Login", response);  // Selenium browser log
        }

        @When("I POST to create agency with name {string} address {string} phone {string} email {string}")
        public void postCreateNoAuth(String name, String address, String phone, String email) {
            response = given()
                    .contentType(ContentType.JSON)
                    .body("{\"name\":\"" + name + "\", \"address\":\"" + address + "\", \"phone\":\"" + phone + "\", \"email\":\"" + email + "\"}")
                    .post("/agencies/add");
            SeleniumReporter.logApiCall("Create No Auth", response);
        }

        @And("extract access token from response")
        public void extractToken() {
            token = JsonPath.from(response.asString()).get("tokens.access_token");
        }

        @And("I POST to create agency with name {string} address {string} phone {string} email {string} using token")
        public void postCreateWithToken(String name, String address, String phone, String email) throws InterruptedException {
            response = given()
                    .header("Authorization", "Bearer " + token)
                    .contentType(ContentType.JSON)
                    .body("{\"name\":\"" + name + "\", \"address\":\"" + address + "\", \"phone\":\"" + phone + "\", \"email\":\"" + email + "\"}")
                    .post("/agencies/add");
            SeleniumReporter.logApiCall("Create Auth", (Response) response);
            agencyId = JsonPath.from(response.asString()).get("agency.id");
        }

        @And("extract agency id from create response")
        public void extractAgencyId() {
            agencyId = JsonPath.from(response.asString()).get("agency.id");
        }

        @When("I GET agency by id")
        public void getAgency() throws InterruptedException {
            response = given()
                    .header("Authorization", "Bearer " + token)
                    .get("/agencies/" + agencyId);
            SeleniumReporter.logApiCall("Get Agency", (Response) response);
        }

        @Then("response status is {int}")
        public void verifyStatus(int code) {
            response.then().statusCode(code);
        }

        @And("response message is {string}")
        public void verifyMessage(String msg) {
            response.then().body("message", equalTo(msg));
        }

        @Then("agency name is {string} address is {string} phone is {string} email is {string}")
        public void verifyAgencyDetails(String name, String addr, String phone, String email) {
            JsonPath jp = JsonPath.from(response.asString());
            response.then().body("agency.name", equalTo(name),
                    "agency.address", equalTo(addr),
                    "agency.phone", equalTo(phone),
                    "agency.email", equalTo(email));
        }
    }


