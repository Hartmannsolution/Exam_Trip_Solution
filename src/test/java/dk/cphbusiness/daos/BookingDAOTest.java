package dk.cphbusiness.daos;

import dk.cphbusiness.dtos.BookingDTO;
import dk.cphbusiness.dtos.ParticipantDTO;
import dk.cphbusiness.dtos.TripDTO;
import dk.cphbusiness.persistence.daos.BookingDAO;
import dk.cphbusiness.persistence.daos.IBookingSelector;
import dk.cphbusiness.persistence.daos.IDAO;
import dk.cphbusiness.persistence.model.Participant;
import dk.cphbusiness.persistence.model.User;
import dk.cphbusiness.utils.IIdProvider;
import dk.cphbusiness.utils.Populator;
import jakarta.persistence.EntityManagerFactory;
import dk.cphbusiness.persistence.HibernateConfig;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BookingDAOTest {
    private static EntityManagerFactory emf;
    private static IDAO<BookingDTO> bookingDAO;
    private static IBookingSelector bookingSelector;

    Map<String, IIdProvider<String>> populatedUsers;
    Map<String, IIdProvider<Long>> populatedTrips;
    Map<String, IIdProvider<Long>> populatedBookings;

    @BeforeAll
    static void setUpAll() {
        HibernateConfig.setTestMode(true);
        emf = HibernateConfig.getEntityManagerFactory();
        bookingDAO = BookingDAO.getBookingDAO(emf);
        bookingSelector = BookingDAO.getBookingSelector(emf);
    }

    @AfterAll
    static void tearDownAll() {
        HibernateConfig.setTestMode(false);
    }

    @BeforeEach
    void setUp() {
        populatedUsers = new Populator().createUsersAndRoles(emf);
        populatedTrips = new Populator().createTripsAndGuides(emf);
        populatedBookings = new Populator().createBookings(emf,  populatedTrips, populatedUsers);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("Test that we can create a booking")
    void create() {
        String username = ((User)populatedUsers.get("user1")).getUsername();
        ParticipantDTO participant1 = new ParticipantDTO(username, Participant.ExperienceLevel.BEGINNER);
        TripDTO trip1 = (TripDTO)populatedTrips.get("trip1");
        BookingDTO booking = new BookingDTO(participant1, trip1, 1, false, "No comment");
        try {
            booking = bookingDAO.create(booking);
        } catch (Exception e) {
            fail("Error creating booking");
        }
        assertNotNull(booking.getId());
    }

    @Test
    @DisplayName("Test that we can get all bookings")
    void getAll() {
        assertEquals(43, bookingDAO.getAll().size());  // Assuming there are 10 bookings populated
    }

    @Test
    @DisplayName("Test that we can get a booking by id")
    void getById() {
        BookingDTO booking = null;
        try {
            booking = bookingDAO.getById(populatedBookings.get("booking1").getId());
        } catch (EntityNotFoundException e) {
            fail("Booking not found");
        }
        assertEquals("First booking", booking.getComment());  // Assuming the expected username is "user1"
    }

    @Test
    @DisplayName("Test that we can update a booking")
    void update() {
        BookingDTO booking = null;
        try {
            booking = bookingDAO.getById(populatedBookings.get("booking1").getId());
            booking.setComment("New Comment");
            BookingDTO updated = bookingDAO.update(booking);
            assertEquals("New Comment", updated.getComment());
        } catch (EntityNotFoundException e) {
            fail("Booking not found");
        }
    }

    @Test
    @DisplayName("Test that we can delete a booking")
    void delete() {
        BookingDTO booking = null;
        try {
            booking = bookingDAO.getById(populatedBookings.get("booking1").getId());
            bookingDAO.delete(booking);
            assertEquals(42, bookingDAO.getAll().size());  // Assuming one booking was deleted
        } catch (EntityNotFoundException e) {
            fail("Booking not found");
        }
    }

    @Test
    @DisplayName("Test that we can create a booking for a user and trip")
    void createBookingForUserAndTrip() {
        User user1 = (User)populatedUsers.get("user1");
        TripDTO trip1 = (TripDTO)populatedTrips.get("trip1");
        BookingDTO booking = new BookingDTO(new ParticipantDTO(new Participant(user1.getUsername(), Participant.ExperienceLevel.BEGINNER)), trip1, 1, false, "No comment");
        try {
            booking = bookingDAO.create(booking);
            assertNotNull(booking.getId());
        } catch (Exception e) {
            fail("Error creating booking");
        }
    }

    @Test
    @DisplayName("Test that we can associate a user with a booking")
    void associateUserWithBooking() {
        // Assume 'user1' exists and 'booking1' exists
        BookingDTO booking = null;
        try {
            booking = bookingDAO.getById(populatedBookings.get("booking1").getId());
            ParticipantDTO participant = booking.getParticipant();
            assertNotNull(participant);
        } catch (EntityNotFoundException e) {
            fail("Booking or User not found");
        }
    }

    @Test
    @DisplayName("Test that we can find all bookings for a specific trip")
    void findBookingsForTrip() {
        // Assuming the Populator has created bookings for trips
        TripDTO trip = (TripDTO) populatedTrips.get("trip1");
        try {
            Set<BookingDTO> bookingsForTrip = bookingSelector.getBookingsByTrip(trip.getId());
            assertTrue(bookingsForTrip != null && bookingsForTrip.size() > 0);  // Assuming at least one booking exists for the trip
            assertTrue(bookingsForTrip.size() > 0);  // Assuming at least one booking exists for the trip
        } catch (EntityNotFoundException e) {
            fail("Trip or Bookings not found");
        }
    }
}
