package dk.cphbusiness.utils;

import dk.cphbusiness.dtos.BookingDTO;
import dk.cphbusiness.dtos.GuideDTO;
import dk.cphbusiness.dtos.ParticipantDTO;
import dk.cphbusiness.dtos.TripDTO;
import dk.cphbusiness.persistence.HibernateConfig;
import dk.cphbusiness.persistence.daos.*;
import dk.cphbusiness.persistence.model.*;
import dk.cphbusiness.security.ISecurityController;
import dk.cphbusiness.security.ISecurityDAO;
import dk.cphbusiness.security.SecurityController;
import dk.cphbusiness.security.SecurityDAO;
import dk.cphbusiness.persistence.model.Role;
import dk.cphbusiness.persistence.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Purpose: To populate the database with users and roles
 * Author: Thomas Hartmann
 */
public class Populator {
    // method to create users and roles before each test
    public Map<String, IIdProvider<String>> createUsersAndRoles(EntityManagerFactory emf) {
        Map<String, IIdProvider<String>> users = new HashMap<>();
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Booking b").executeUpdate();
            em.createQuery("DELETE FROM Participant p").executeUpdate();
            em.createQuery("DELETE FROM User u").executeUpdate();
            em.createQuery("DELETE FROM Role r").executeUpdate();
            User user1 = new User("user1", "user123");
            User user2 = new User("user2", "user123");
            User user3 = new User("user3", "user123");
            User user4 = new User("user4", "user123");
            User user5 = new User("user5", "user123");
            User user6 = new User("user6", "user123");
            User user7 = new User("user7", "user123");
            User admin = new User("admin", "admin123");
            User superUser = new User("super", "super123");
            Role userRole = new Role("user");
            Role adminRole = new Role("admin");
            user1.addRole(userRole);
            user2.addRole(userRole);
            user3.addRole(userRole);
            user4.addRole(userRole);
            user5.addRole(userRole);
            user6.addRole(userRole);
            user7.addRole(userRole);
            admin.addRole(adminRole);
            superUser.addRole(userRole);
            superUser.addRole(adminRole);
            em.persist(user1);
            em.persist(user2);
            em.persist(user3);
            em.persist(user4);
            em.persist(user5);
            em.persist(user6);
            em.persist(user7);
            em.persist(admin);
            em.persist(superUser);
            em.persist(userRole);
            em.persist(adminRole);
            em.getTransaction().commit();

            users.put("user1", user1);
            users.put("user2", user2);
            users.put("user3", user3);
            users.put("user4", user4);
            users.put("user5", user5);
            users.put("user6", user6);
            users.put("user7", user7);
            users.put("admin", admin);
            users.put("super", superUser);
            return users;
        }
    }

    public Map<String, IIdProvider<Long>> createTripsAndGuides(EntityManagerFactory emf) {
        Map<String, IIdProvider<Long>> tripsAndGuides = new HashMap<>();
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Trip t").executeUpdate();
            em.createQuery("DELETE FROM Guide g").executeUpdate();
            // reset the sequence
            em.createNativeQuery("ALTER SEQUENCE trip_id_seq RESTART WITH 1").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE guide_id_seq RESTART WITH 1").executeUpdate();
            em.getTransaction().commit();
        }
        IDAO<TripDTO> tripDAO;
        IDAO<GuideDTO> guideDAO;
        ITripGuideDAO tripGuideDAO;
        ISecurityDAO securityDAO;
        ISecurityController securityController;

        GuideDTO guide1, guide2, guide3;
        TripDTO trip1, trip2, trip3, trip4, trip5, trip6, trip7, trip8, trip9, trip10, trip11;
        User user, admin, superUser = null;
        emf = HibernateConfig.getEntityManagerFactory();
        securityDAO = new SecurityDAO(emf);
        securityController = SecurityController.getInstance();
        tripDAO = TripDAO.getTripDAO(emf);
        guideDAO = GuideDAO.getGuideDAO(emf);
        tripGuideDAO = TripDAO.getTripGuideDAO(emf);
        LocalDateTime base = LocalDateTime.now();
        trip1 = new TripDTO(base.plusDays(14), base.plusDays(15), 12.34, 56.78, "Beach Holiday", 150.00, 1L, Trip.TripCategory.BEACH, null);
        trip2 = new TripDTO(base.plusDays(30), base.plusDays(31), 13.45, 57.89, "City Tour", 200.00, 1L, Trip.TripCategory.CITY, null);
        trip3 = new TripDTO(base.plusDays(50), base.plusDays(52), 14.56, 58.90, "Forest Adventure", 180.00, 2L, Trip.TripCategory.FOREST, null);
        trip4 = new TripDTO(base.plusDays(40), base.plusDays(54), 14.96, 58.95, "Forest Climbing", 280.00, 2L, Trip.TripCategory.FOREST, null);
        trip5 = new TripDTO(base.plusDays(60), base.plusDays(62), 14.94, 58.75, "Forest Dwelling", 780.00, 2L, Trip.TripCategory.FOREST, null);
        trip6 = new TripDTO(base.plusDays(70), base.plusDays(72), 14.94, 58.75, "Lake swimming", 130.00, 2L, Trip.TripCategory.LAKE, null);
        trip7 = new TripDTO(base.plusDays(70), base.plusDays(72), 14.94, 58.75, "Shark fishing", 510.00, 2L, Trip.TripCategory.SEA, null);
        trip8 = new TripDTO(base.plusDays(70), base.plusDays(72), 14.94, 58.75, "Cross Country Skiing", 444.00, 2L, Trip.TripCategory.SNOW, null);
        trip9 = new TripDTO(base.plusDays(70), base.plusDays(72), 14.94, 58.75, "Snow boarding", 440.00, 2L, Trip.TripCategory.SNOW, null);
        trip10 = new TripDTO(base.plusDays(70), base.plusDays(72), 14.94, 58.75, "Living under a bridge", 10.00, 2L, Trip.TripCategory.CITY, null);
        trip11 = new TripDTO(base.plusDays(72), base.plusDays(75), 14.94, 58.75, "Test", 10.00, 2L, Trip.TripCategory.CITY, null);

        guide1 = new GuideDTO("Andreas", "Turkey", "andreas@mail.com", "33293922", 10);
        guide2 = new GuideDTO("Jane", "Doe", "jdo@mail.com", "12345678", 11);
        guide3 = new GuideDTO("Brian", "Swan", "jsw@mail.com", "44993434", 12);
        try {
            trip1 = tripDAO.create(trip1);
            trip2 = tripDAO.create(trip2);
            trip3 = tripDAO.create(trip3);
            trip4 = tripDAO.create(trip4);
            trip5 = tripDAO.create(trip5);
            trip6 = tripDAO.create(trip6);
            trip7 = tripDAO.create(trip7);
            trip8 = tripDAO.create(trip8);
            trip9 = tripDAO.create(trip9);
            trip10 = tripDAO.create(trip10);
            trip11 = tripDAO.create(trip11);

            guide1 = guideDAO.create(guide1);
            guide2 = guideDAO.create(guide2);
            guide3 = guideDAO.create(guide3);

            trip1 = tripGuideDAO.addGuideToTrip(trip1.getId(), guide1.getId());
            trip2 = tripGuideDAO.addGuideToTrip(trip2.getId(), guide1.getId());
            trip3 = tripGuideDAO.addGuideToTrip(trip3.getId(), guide2.getId());
            trip4 = tripGuideDAO.addGuideToTrip(trip4.getId(), guide2.getId());
            trip5 = tripGuideDAO.addGuideToTrip(trip5.getId(), guide2.getId());
            trip6 = tripGuideDAO.addGuideToTrip(trip6.getId(), guide3.getId());
            trip7 = tripGuideDAO.addGuideToTrip(trip7.getId(), guide3.getId());
            trip8 = tripGuideDAO.addGuideToTrip(trip8.getId(), guide3.getId());
            trip9 = tripGuideDAO.addGuideToTrip(trip9.getId(), guide1.getId());
            trip10 = tripGuideDAO.addGuideToTrip(trip10.getId(), guide1.getId());
            trip10 = tripGuideDAO.addGuideToTrip(trip11.getId(), guide1.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        tripsAndGuides.put("trip1", trip1);
        tripsAndGuides.put("trip2", trip2);
        tripsAndGuides.put("trip3", trip3);
        tripsAndGuides.put("trip4", trip4);
        tripsAndGuides.put("trip5", trip5);
        tripsAndGuides.put("trip6", trip6);
        tripsAndGuides.put("trip7", trip7);
        tripsAndGuides.put("trip8", trip8);
        tripsAndGuides.put("trip9", trip9);
        tripsAndGuides.put("trip10", trip10);
        tripsAndGuides.put("trip11", trip11);
        tripsAndGuides.put("guide1", guide1);
        tripsAndGuides.put("guide2", guide2);
        tripsAndGuides.put("guide3", guide3);
        return tripsAndGuides;
//        return Map.of("trip1", trip1, "trip2", trip2, "trip3", trip3, "guide1", guide1, "guide2", guide2, "guide3", guide3);
    }

    public Map<String, IIdProvider<Long>> createBookings(EntityManagerFactory emf, Map<String, IIdProvider<Long>> trips, Map<String, IIdProvider<String>> users) {
        Map<String, IIdProvider<Long>> bookings = new HashMap<>();
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Booking b").executeUpdate();  // Delete previous bookings
            em.getTransaction().commit();
        }

        IDAO<BookingDTO> bookingDAO = BookingDAO.getBookingDAO(emf);
        IDAO<TripDTO> tripDAO = TripDAO.getTripDAO(emf);

        // 7 User to 10 trips
        Set<String> userList = users.keySet();
        // Random user: 1-7
//        String randomUser = userList.stream().skip((int) (Math.random() * userList.size())).findFirst().get();

        AtomicInteger counter = new AtomicInteger(1);
        trips.forEach((k, v) -> {
            if(k.equals("trip11")) return; // Skip the last trip (Used for testing)

            // 4 bookings per trip
            for(int i = 0; i < 4; i++) {
                User randomUser = (User) users
                        .get(userList
                                .stream()
                                .skip((int) (Math.random() * userList.size()))
                                .findFirst()
                                .get());
                try {
                    BookingDTO booking = new BookingDTO(
                            new ParticipantDTO(
                            new Participant(randomUser.getUsername()
                                    , Participant.ExperienceLevel.BEGINNER))
                            , (TripDTO) v
                            , (int) (Math.random() * 6)+1
                            , false
                            , "First booking");
                    BookingDTO created = bookingDAO.create(booking);
                    bookings.put("booking" + counter.getAndIncrement(), created);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // add User 1 to trip 1-3 (for testing)
        for(int i = 1; i <= 3; i++) {
            User user1 = (User) users.get("user1");
            String tripNo = "trip"+i;
            TripDTO trip8 = (TripDTO) trips.get(tripNo);
            try {
                BookingDTO booking = new BookingDTO(
                        new ParticipantDTO(
                        new Participant(user1.getUsername()
                                , Participant.ExperienceLevel.BEGINNER))
                        , trip8
                        , (int) (Math.random() * 6)+1
                        , false
                        , "User 1 booking");
                BookingDTO created = bookingDAO.create(booking);
                bookings.put("booking" + counter.getAndIncrement(), created);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bookings;
    }

    // Add additional helper methods as necessary, including getUserDAO and getBookingDAO.
    public static void main(String[] args) {
        Populator populator = new Populator();
        Map<String, IIdProvider<String>> populatedUsers = populator.createUsersAndRoles(HibernateConfig.getEntityManagerFactory());
        Map<String, IIdProvider<Long>> populatedTrips = populator.createTripsAndGuides(HibernateConfig.getEntityManagerFactory());
        Map<String, IIdProvider<Long>> populatedBookings = populator.createBookings(HibernateConfig.getEntityManagerFactory(), populatedTrips, populatedUsers);
        populatedBookings.forEach((k, v) -> System.out.println(k + " : " + v));
    }
}

