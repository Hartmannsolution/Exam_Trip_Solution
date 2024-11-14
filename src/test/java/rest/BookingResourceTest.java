package rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dk.cphbusiness.dtos.BookingDTO;
import dk.cphbusiness.dtos.ParticipantDTO;
import dk.cphbusiness.dtos.TripDTO;
import dk.cphbusiness.persistence.HibernateConfig;
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

public class BookingResourceTest {

    private static ApplicationConfig appConfig;
    private static EntityManagerFactory emf;
    private static ObjectMapper objectMapper = new Utils().getObjectMapper();
    private static final String BASE_URL = "http://localhost:7777/api";
    Map<String, IIdProvider<String>> populatedUsers;
    Map<String, IIdProvider<Long>> populatedBookings;
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

        emf = HibernateConfig.getEntityManagerFactory();
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
                .startServer(7777);
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
        populatedBookings = new Populator().createBookings(emf, populatedTrips, populatedUsers);
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
                .when().post("/auth/login")
                .then()
                .extract().path("token");
    }

    @Test
    @DisplayName("Test if server is up")
    public void testServerIsUp() {
        System.out.println("Testing is server UP");
        given().when().get("/bookings").then().statusCode(200);
    }

    @Test
    @DisplayName("Test retrieving all bookings")
    public void testGetAllBookings() {
        login("user", "user123");
        given()
                .contentType("application/json")
                .accept("application/json")
                .header("Authorization", "Bearer " + securityToken)
                .when()
                .get("/bookings")
                .then()
                .log().all()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }

    @Test
    @DisplayName("Test posting a new booking (Admin only)")
    public void testCreateBooking() {
        login("admin", "admin123");
        String json = null;
        ParticipantDTO participant = ((BookingDTO) populatedBookings.get("booking1")).getParticipant();
        TripDTO trip = (TripDTO) populatedTrips.get("trip11");
        try {
            BookingDTO booking = new BookingDTO(participant, trip, 3, false, "No comment");
            json = objectMapper.writeValueAsString(booking);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }

        given()
                .contentType("application/json")
                .accept("application/json")
                .header("Authorization", "Bearer " + securityToken)
                .body(json)
                .when()
                .post("/bookings")
                .then()
                .log().all()
                .statusCode(201)
                .body("id", equalTo(44));
    }

    @Test
    @DisplayName("Test retrieving booking by ID")
    public void testGetBookingById() {
        BookingDTO booking1 = (BookingDTO) populatedBookings.get("booking1");
        Long bookingId = booking1.getId();
        given()
                .contentType("application/json")
                .when()
                .get("/bookings/" + bookingId)
                .then()
                .log().all()
                .statusCode(HttpStatus.OK_200)
                .body("id", equalTo(bookingId.intValue()))
                .body("comment", equalTo(booking1.getComment()));
    }

    @Test
    @DisplayName("Test retrieving bookings by trip ID")
    public void testGetBookingsByTrip() {
        Long tripId = populatedBookings.get("booking1").getId();

        given()
                .contentType("application/json")
                .when()
                .get("/bookings/get_bookings_by_trip/" + tripId)
                .then()
                .log().all()
                .statusCode(HttpStatus.OK_200)
                .body("size()", greaterThan(0));
    }

    @Test
    @DisplayName("Test retrieving bookings by participant username")
    public void testGetBookingsByParticipant() {
        String username = "user1";
        given()
                .contentType("application/json")
                .when()
                .get("/bookings/get_bookings_by_participant/" + username)
                .then()
                .log().all()
                .statusCode(HttpStatus.OK_200)
                .body("size()", greaterThan(2)); // user1 has at least 3 bookings
    }

    @Test
    @DisplayName("Test updating a booking")
    public void testUpdateBooking() {
        login("admin", "admin123");
        Long bookingId = populatedBookings.get("booking1").getId();
        String json = null;
        ParticipantDTO participant = ((BookingDTO) populatedBookings.get("booking1")).getParticipant();
        TripDTO trip = (TripDTO) populatedTrips.get("trip11");
        String comment = "";
        try {
            comment = "Updated booking at: "+ LocalDateTime.now();
            BookingDTO booking = new BookingDTO(participant, trip, 3, true, comment);
            json = objectMapper.writeValueAsString(booking);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }

        given()
                .contentType("application/json")
                .accept("application/json")
                .header("Authorization", "Bearer " + securityToken)
                .body(json)
                .when()
                .put("/bookings/" + bookingId)
                .then()
                .statusCode(HttpStatus.OK_200)
                .body("comment", equalTo(comment));
    }

    @Test
    @DisplayName("Test deleting a booking")
    public void testDeleteBooking() {
        login("admin", "admin123");
        Long bookingId = populatedBookings.get("booking1").getId();

        given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + securityToken)
                .when()
                .delete("/bookings/" + bookingId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT_204);
    }

    @Test
    @DisplayName("Test sum of participants for a trip")
    public void testSumOfParticipantsForTrip() {
        Long tripId = populatedTrips.get("trip1").getId();

        given()
                .contentType("application/json")
                .when()
                .get("/bookings/sum_of_participants_for_trip/" + tripId)
                .then()
                .log().all()
                .statusCode(HttpStatus.OK_200)
                .body("sum", greaterThan(0));
    }
}
