package dk.cphbusiness.daos;

import dk.cphbusiness.dtos.GuideDTO;
import dk.cphbusiness.dtos.TripDTO;
import dk.cphbusiness.exceptions.EntityNotFoundException;
import dk.cphbusiness.persistence.daos.GuideDAO;
import dk.cphbusiness.persistence.daos.IDAO;
import dk.cphbusiness.persistence.daos.ITripGuideDAO;
import dk.cphbusiness.persistence.daos.TripDAO;
import dk.cphbusiness.persistence.model.*;
import dk.cphbusiness.utils.IIdProvider;
import dk.cphbusiness.utils.Populator;
import dk.cphbusiness.security.ISecurityController;
import dk.cphbusiness.security.ISecurityDAO;
import dk.cphbusiness.security.SecurityController;
import dk.cphbusiness.security.SecurityDAO;
import jakarta.persistence.EntityManagerFactory;
import dk.cphbusiness.persistence.HibernateConfig;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

//@Disabled
class DAOTest {
    private static EntityManagerFactory emf;
    private static IDAO<TripDTO> tripDAO;
    private static IDAO<GuideDTO> guideDAO;
    private static ITripGuideDAO tripGuideDAO;
    private static ISecurityDAO securityDAO;
    private static ISecurityController securityController;

    Map<String, IIdProvider<String>> populatedUsers;
    Map<String, IIdProvider<Long>> populatedTrips;


    @BeforeAll
    static void setUpAll() {
        HibernateConfig.setTestMode(true);
        emf = HibernateConfig.getEntityManagerFactory();
        securityDAO = new SecurityDAO(emf);
        securityController = SecurityController.getInstance();
        tripDAO = TripDAO.getTripDAO(emf);
        guideDAO = GuideDAO.getGuideDAO(emf);
        tripGuideDAO = TripDAO.getTripGuideDAO(emf);
    }

    @AfterAll
    static void tearDownAll() {
        HibernateConfig.setTestMode(false);
    }

    @BeforeEach
    void setUp() {
        populatedUsers = new Populator().createUsersAndRoles(emf);
        populatedTrips = new Populator().createTripsAndGuides(emf);
        // Create 3 users and 2 roles: user, admin and super and user and admin roles
        // Populate the persons with addresses, Phone and Hobbies
//        trip1 = new TripDTO( LocalDateTime.now(), LocalDateTime.now(), 12.34, 56.78, "Beach Day", 150.00, 1L, Trip.TripCategory.BEACH, null);
//        trip2 = new TripDTO( LocalDateTime.now(), LocalDateTime.now(), 13.45, 57.89, "City Tour", 200.00, 1L, Trip.TripCategory.CITY, null);
//        trip3 = new TripDTO( LocalDateTime.now(), LocalDateTime.now(), 14.56, 58.90, "Forest Adventure", 180.00, 2L, Trip.TripCategory.FOREST, null);
//        guide1 = new GuideDTO("John", "Doe", "john@mail.com", "12345678", 10);
//        guide2 = new GuideDTO("Jane", "Doe", "jdo@mail.com", "12345678", 11);
//        guide3 = new GuideDTO("Jack", "Swan", "jsw@mail.com", "12345678", 12);
//        try {
//            tripDAO.create(trip1);
//            tripDAO.create(trip2);
//            tripDAO.create(trip3);
//            guide1 = guideDAO.create(guide1);
//            guide2 = guideDAO.create(guide2);
//            guide3 = guideDAO.create(guide3);
//            trip1 = tripGuideDAO.addGuideToTrip(1L, 1L);
//            trip2 = tripGuideDAO.addGuideToTrip(2L, 1L);
//            trip3 = tripGuideDAO.addGuideToTrip(3L, 3L);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("Test that we can create a trip")
    void create() {
        TripDTO trip = new TripDTO( LocalDateTime.now(), LocalDateTime.now(), 15.67, 59.01, "Lake Visit", 220.00, 2L, Trip.TripCategory.LAKE, null);
        try {
            tripDAO.create(trip);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(11, tripDAO.getAll().size());
    }

    @Test
    @DisplayName("Test that we can create a guide")
    void testCreateGuide() {
        GuideDTO guide = new GuideDTO(4L, "Dennis", "Ritchie", "dre@mail.dk", "12345678", 10, null);
        try {
            guideDAO.create(guide);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(4, guideDAO.getAll().size());
    }

    @Test
    @DisplayName("Test that we can get all trips")
    void getAll() {
        assertEquals(10, tripDAO.getAll().size());
    }

    @Test
    @DisplayName("Test that we can get a trip by id")
    void getById() {
        TripDTO trip = null;
        try {
            trip = tripDAO.findById(populatedTrips.get("trip1").getId());
        } catch (EntityNotFoundException e) {
            throw new RuntimeException(e);
        }
        assertEquals("Beach Holiday", trip.getName());
    }

    @Test
    @DisplayName("Test that we can update a trip")
    void update() {
        TripDTO trip = null;
        try {
            trip = tripDAO.findById(populatedTrips.get("trip1").getId());
            trip.setName("Totally different name");
            TripDTO updated = tripDAO.update(trip);
            assertEquals("Totally different name", updated.getName());
        } catch (EntityNotFoundException e) {
            fail("Trip not found");
        }

    }

    @Test
    @DisplayName("Test that we can delete a trip")
    void delete() {
        TripDTO trip = null;
        try {
            trip = tripDAO.findById(populatedTrips.get("trip1").getId());
            tripDAO.delete(trip);
            assertEquals(9, tripDAO.getAll().size());
        } catch (EntityNotFoundException e) {
            fail("Trip not found");
        }
    }

    @Test
    @DisplayName("Test that we can add a guide to a trip")
    void testAddGuide() {
        TripDTO trip = new TripDTO(LocalDateTime.now(), LocalDateTime.now(), 15.67, 59.01, "Lake Visit", 220.00, 2L, Trip.TripCategory.LAKE, null);
        try {
            trip = tripDAO.create(trip);
            GuideDTO guide = ((TripDTO)populatedTrips.get("trip1")).getGuide();
            trip = tripGuideDAO.addGuideToTrip(trip.getId(), guide.getId());
            assertNotNull(trip.getGuide());
        } catch (EntityNotFoundException e) {
            fail("Guide or Trip not found");
        } catch (Exception e) {
            fail("Error adding guide to trip");
        }
    }
}