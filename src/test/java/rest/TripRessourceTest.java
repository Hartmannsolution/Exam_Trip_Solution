package rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dk.cphbusiness.dtos.TripDTO;
import dk.cphbusiness.persistence.HibernateConfig;
import dk.cphbusiness.persistence.model.Trip;
import dk.cphbusiness.rest.ApplicationConfig;
import dk.cphbusiness.utils.IIdProvider;
import dk.cphbusiness.utils.Populator;
import dk.cphbusiness.rest.RestRoutes;
import dk.cphbusiness.security.SecurityRoutes;
import dk.cphbusiness.utils.Utils;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManagerFactory;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class TripRessourceTest {

    private static ApplicationConfig appConfig;
    private static EntityManagerFactory emf;
    private static ObjectMapper objectMapper = new Utils().getObjectMapper();
    private static final String BASE_URL = "http://localhost:7777/api";
    Map<String, IIdProvider<String>> populatedUsers;
    Map<String, IIdProvider<Long>> populatedTrips;

    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = BASE_URL;
    }

    @BeforeAll
    static void setUpAll() {
        RestAssured.baseURI = "http://localhost:7777/api";

        HibernateConfig.setTestMode(true); // IMPORTANT leave this at the very top of this method in order to use the test database
        RestRoutes restRoutes = new RestRoutes();

        // Setup test database using docker testcontainers
        emf = HibernateConfig.getEntityManagerFactory();

        // Start server
        appConfig = ApplicationConfig.
                getInstance()
                .initiateServer()
                .checkSecurityRoles()
                .setErrorHandling()
                .setGeneralExceptionHandling()
                .setRoute(SecurityRoutes.getSecurityRoutes())
                .setRoute(SecurityRoutes.getSecuredRoutes())
                .setRoute(restRoutes.getTripRoutes())
                .setCORS()
                .setApiExceptionHandling()
                .startServer(7777)
        ;
    }

    @AfterAll
    static void afterAll() {
        HibernateConfig.setTestMode(false);
        appConfig.stopServer();
    }

    @BeforeEach
    void setUpEach() {
        populatedUsers = new Populator().createUsersAndRoles(emf);
        populatedTrips = new Populator().createTripsAndGuides(emf);
    }

    @Test
    @DisplayName("Test if server is up")
    public void testServerIsUp() {
        System.out.println("Testing is server UP");
        given().when().get("/guides").then().statusCode(200);
    }

    private static String securityToken;

    private static void login(String username, String password) {
        ObjectNode objectNode = objectMapper.createObjectNode()
                .put("username", username)
                .put("password", password);
        String loginInput = objectNode.toString();
        securityToken = given()
                .contentType("application/json")
                .body(loginInput)
                //.when().post("/api/login")
                .when().post("/auth/login")
                .then()
                .extract().path("token");
        System.out.println("TOKEN ---> " + securityToken);
    }

    @Test
    @DisplayName("Test Entities from DB")
    public void testEntitiesFromDB() {
        login("admin", "admin123");
        given()
                .contentType("application/json")
                .accept("application/json")
                .header("Authorization", "Bearer " + securityToken)
                .when()
                .get("/trips").then()
                .log().all()
                .statusCode(200)
                .body("size()", equalTo(10));
    }

    @Test
    @DisplayName("Test POST to Trip Entities not allowed for User role")
    public void testEntitiesFromDBNotAllowed() {
        login("user", "user123");
        String json = null;
        try {
            TripDTO trip = new TripDTO(LocalDateTime.now(), LocalDateTime.now(), 12.34, 56.78, "Park walk", 150.00, 1L, Trip.TripCategory.BEACH, null);
            // Convert the trip object to JSON using jackson object mapper
            json = objectMapper.writeValueAsString(trip);
        } catch (JsonProcessingException ex){
            ex.printStackTrace();
        }

        given()
                .contentType("application/json")
                .accept("application/json")
                .header("Authorization", "Bearer " + securityToken)
                .when()
                .body(json)
                .post("/trips").then()
                .statusCode(403); // Forbidden
    }

    @Test
    @DisplayName("Test POST to Trip Entities allowed for Admin role")
    public void testPostWithAdmin() {
        login("admin", "admin123");
        String json = null;
        try {
            TripDTO trip = new TripDTO(LocalDateTime.now(), LocalDateTime.now(), 12.34, 56.78, "Park walk", 150.00, 1L, Trip.TripCategory.BEACH, null);
            // Convert the trip object to JSON using jackson object mapper
            json = objectMapper.writeValueAsString(trip);
        } catch (JsonProcessingException ex){
            ex.printStackTrace();
        }

        given()
                .contentType("application/json")
                .accept("application/json")
                .header("Authorization", "Bearer " + securityToken)
                .when()
                .body(json)
                .post("/trips").then()
                .log().all()
                .statusCode(201)
                .body("name", equalTo("Park walk"));
    }

    @Test
    @DisplayName("Test Log request details")
    public void testLogRequest() {
        System.out.println("Testing logging request details");
        given()
                .log().all()
                .when().get("/trips")
                .then().statusCode(200);
    }

    @Test
    @DisplayName("Test Log response details")
    public void testLogResponse() {
        System.out.println("Testing logging response details");
        given()
                .when().get("/trips")
                .then().log().body().statusCode(200);
    }

    @Test
    @DisplayName("Test adding a guide to a trip successfully")
    void testAddGuideToTripSuccess() {
        // Assuming valid tripId and guideId are 1 and 2
        Long tripId = populatedTrips.get("trip1").getId();
        Long guideId = populatedTrips.get("guide2").getId();

        given()
                .contentType("application/json")
                .pathParam("tripId", tripId)
                .pathParam("guideId", guideId)
                .when()
                .put("/trips/trip/{tripId}/guide/{guideId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200)
                .body("id", equalTo(tripId.intValue()))
                .body("guide.id", equalTo(guideId.intValue()));
    }

    @Test
    @DisplayName("Test adding a guide to a trip when trip is not found")
    void testAddGuideToTripTripNotFound() {
        Long invalidTripId = 999L; // ID that does not exist
        Long guideId = 2L; // Assume this guide exists

        given()
                .contentType("application/json")
                .pathParam("tripId", invalidTripId)
                .pathParam("guideId", guideId)
                .when()
                .post("/trip/{tripId}/guide/{guideId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404);
//                .body("message", containsString("Trip not found with id: " + invalidTripId));
    }

    @Test
    @DisplayName("Test adding a guide to a trip when guide is not found")
    void testAddGuideToTripGuideNotFound() {
        Long tripId = 1L; // Assume this trip exists
        Long invalidGuideId = 999L; // ID that does not exist

        given()
                .contentType("application/json")
                .pathParam("tripId", tripId)
                .pathParam("guideId", invalidGuideId)
                .when()
                .post("/trip/{tripId}/guide/{guideId}")
                .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404);
//                .body("message", containsString("Guide not found with id: " + invalidGuideId));
    }

    @Test
    @DisplayName("Test filter trips by category")
    void testFilterTripsByCategory() {

        given()
                .contentType("application/json")
                .when()
                .get("/trips/category/beach")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK_200)
                .body("size()", equalTo(1));
    }

    @Test
    @DisplayName("Test getting sum of trips prices per guide")
    void testSumOfPrices() {

        given()
                .contentType("application/json")
                .when()
                .get("/trips/sumOfTripsForGuides")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK_200)
                .body("size()", equalTo(3));
    }

    @Test
    @DisplayName("Test addiing packing items to a trip")
    void testAddingPacking() {

        given()
                .contentType("application/json")
                .when()
                .get("/trips/"+((TripDTO) populatedTrips.get("trip1")).getId())
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK_200)
                .body("packingItems", hasSize(7));
    }
}
