package dk.cphbusiness.utils;

import dk.cphbusiness.dtos.GuideDTO;
import dk.cphbusiness.dtos.TripDTO;
import dk.cphbusiness.persistence.HibernateConfig;
import dk.cphbusiness.persistence.daos.GuideDAO;
import dk.cphbusiness.persistence.daos.IDAO;
import dk.cphbusiness.persistence.daos.ITripGuideDAO;
import dk.cphbusiness.persistence.daos.TripDAO;
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
import java.util.Map;

/**
 * Purpose: To populate the database with users and roles
 * Author: Thomas Hartmann
 */
public class Populator {
    // method to create users and roles before each test
    public Map<String, IIdProvider<String>> createUsersAndRoles(EntityManagerFactory emf) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM User u").executeUpdate();
            em.createQuery("DELETE FROM Role r").executeUpdate();
            User user = new User("user", "user123");
            User admin = new User("admin", "admin123");
            User superUser = new User("super", "super123");
            Role userRole = new Role("user");
            Role adminRole = new Role("admin");
            user.addRole(userRole);
            admin.addRole(adminRole);
            superUser.addRole(userRole);
            superUser.addRole(adminRole);
            em.persist(user);
            em.persist(admin);
            em.persist(superUser);
            em.persist(userRole);
            em.persist(adminRole);
            em.getTransaction().commit();
            return Map.of("user", user, "admin", admin, "superUser", superUser, "userRole", new Role("user"), "adminRole", new Role("admin"));
        }
    }

    public Map<String, IIdProvider<Long>> createTripsAndGuides(EntityManagerFactory emf) {
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
        TripDTO trip1, trip2, trip3;
        User user, admin, superUser = null;
        emf = HibernateConfig.getEntityManagerFactory();
        securityDAO = new SecurityDAO(emf);
        securityController = SecurityController.getInstance();
        tripDAO = TripDAO.getTripDAO(emf);
        guideDAO = GuideDAO.getGuideDAO(emf);
        tripGuideDAO = TripDAO.getTripGuideDAO(emf);
        trip1 = new TripDTO( LocalDateTime.now(), LocalDateTime.now().plusDays(1), 12.34, 56.78, "Beach Day", 150.00, 1L, Trip.TripCategory.BEACH, null);
        trip2 = new TripDTO( LocalDateTime.now(), LocalDateTime.now().plusDays(2), 13.45, 57.89, "City Tour", 200.00, 1L, Trip.TripCategory.CITY, null);
        trip3 = new TripDTO( LocalDateTime.now(), LocalDateTime.now().plusDays(3), 14.56, 58.90, "Forest Adventure", 180.00, 2L, Trip.TripCategory.FOREST, null);
        guide1 = new GuideDTO("John", "Doe", "john@mail.com", "12345678", 10);
        guide2 = new GuideDTO("Jane", "Doe", "jdo@mail.com", "12345678", 11);
        guide3 = new GuideDTO("Jack", "Swan", "jsw@mail.com", "12345678", 12);
        try {
            trip1 = tripDAO.create(trip1);
            trip2 = tripDAO.create(trip2);
            trip3 = tripDAO.create(trip3);

            guide1 = guideDAO.create(guide1);
            guide2 = guideDAO.create(guide2);
            guide3 = guideDAO.create(guide3);

            trip1 = tripGuideDAO.addGuideToTrip(trip1.getId(), guide1.getId());
            trip2 = tripGuideDAO.addGuideToTrip(trip2.getId(), guide1.getId());
            trip3 = tripGuideDAO.addGuideToTrip(trip3.getId(), guide3.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Map.of("trip1", trip1, "trip2", trip2, "trip3", trip3, "guide1", guide1, "guide2", guide2, "guide3", guide3);
    }

    public static void main(String[] args) {
        Populator populator = new Populator();
        Map<String, IIdProvider<String>> populatedUsers = populator.createUsersAndRoles(HibernateConfig.getEntityManagerFactory());
        Map<String, IIdProvider<Long>> populatedTrips = populator.createTripsAndGuides(HibernateConfig.getEntityManagerFactory());
    }
}

