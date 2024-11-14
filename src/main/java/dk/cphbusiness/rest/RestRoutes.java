package dk.cphbusiness.rest;

import dk.cphbusiness.persistence.HibernateConfig;
import dk.cphbusiness.persistence.model.Trip;
import dk.cphbusiness.rest.controllers.BookingController;
import dk.cphbusiness.rest.controllers.GuideController;
import dk.cphbusiness.rest.controllers.TripController;
import dk.cphbusiness.rest.controllers.TripMockController;
import dk.cphbusiness.security.SecurityRoutes.Role;
import dk.cphbusiness.utils.Populator;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.*;

/**
 * Purpose: To demonstrate the use of unprotected routes and protected ones
 *
 * Author: Thomas Hartmann
 */
public class RestRoutes {
    private final TripMockController tripMockController = new TripMockController();
    private final TripController tripController = TripController.getInstance();
    private final GuideController guideController = GuideController.getInstance();
    private final BookingController bookingController = BookingController.getInstance();
    private final Populator populator = new Populator();
    private final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

    public EndpointGroup getTripRoutes() {
        return () -> {
            path("/", () -> {
                path("populate", () -> {
                    get("/trips", (ctx)-> {populator.createTripsAndGuides(emf); ctx.json("{\"msg\":\"Success\"}");}, Role.ANYONE);        // GET /mock/populate
                    get("/users", (ctx)-> {populator.createUsersAndRoles(emf); ctx.json("{\"msg\":\"Success\"}");}, Role.ANYONE);        // GET /mock/populate
                    get("/bookings", (ctx)-> {populator.createBookings(emf, populator.createTripsAndGuides(emf), populator.createUsersAndRoles(emf)); ctx.json("{\"msg\":\"Success\"}");}, Role.ANYONE);        // GET /mock/populate
                });
                path("guides", () -> {
                    get("/", guideController.getAll(), Role.ANYONE);        // GET /mock/guides
                    post("/", guideController.create(), Role.ADMIN);        // POST /mock/guides
                    put("/{id}", guideController.update(), Role.ADMIN);    // PUT /mock/guides/:id
                });
                path("trips", () -> {
                    get("/", tripController.getAll(), Role.ANYONE);        // GET /mock/trips
                    get("/sumOfTripsForGuides", tripController.getSumOfTripsByGuide(), Role.ANYONE);   // GET /mock/trips/:id
                    get("/{id}", tripController.getById(), Role.ANYONE);   // GET /mock/trips/:id
                    get("/category/{category}", tripController.getFilteredTrips(), Role.ANYONE);   // GET /mock/trips/:id
                    post("/", tripController.create(), Role.ADMIN);        // POST /mock/trips
                    put("/{id}", tripController.update(), Role.ADMIN);    // PUT /mock/trips/:id
                    delete("/{id}", tripController.delete(), Role.ANYONE); // DELETE /mock/trips/:id
                    put("/trip/{tripId}/guide/{guideId}", tripController.addGuideToTrip(), Role.ANYONE); // POST /mock/trips/:id/guide
                });
                path("bookings", () -> {
                    get("/", bookingController.getAll(), Role.ANYONE);        // GET /mock/trips
                    get("/sum_of_participants_for_trip/{tripId}", bookingController.getSumParticipantForTrip(), Role.ANYONE);   // GET /mock/trips/:id
                    get("/{id}", bookingController.getById(), Role.ANYONE);   // GET /mock/trips/:id
                    get("/get_bookings_by_trip/{tripId}", bookingController.getBookingsByTrip(), Role.ANYONE);   // GET /mock/trips/:id
                    get("/get_bookings_by_participant/{username}", bookingController.getBookingsByParticipant(), Role.ANYONE);   // GET /mock/trips/:id
                    post("/", bookingController.create(), Role.ADMIN);        // POST /mock/trips
                    put("/{id}", bookingController.update(), Role.ADMIN);    // PUT /mock/trips/:id
                    delete("/{id}", bookingController.delete(), Role.ANYONE); // DELETE /mock/trips/:id
                });
            });
            path("mock", () -> {
                path("guides", () -> {
                    get("/", tripMockController.getAllGuides(), Role.ANYONE);        // GET /mock/guides
                    post("/", tripMockController.createGuide(), Role.ANYONE);        // POST /mock/guides
                    put("/{id}", tripMockController.updateGuide(), Role.ANYONE);    // PUT /mock/guides/:id
                });
                path("trips", () -> {
                    get("/", tripMockController.getAll(), Role.ANYONE);        // GET /mock/trips
                    get("/{id}", tripMockController.getById(), Role.ANYONE);   // GET /mock/trips/:id
                    post("/", tripMockController.create(), Role.ANYONE);        // POST /mock/trips
                    put("/{id}", tripMockController.update(), Role.ANYONE);    // PUT /mock/trips/:id
                    delete("/{id}", tripMockController.delete(), Role.ANYONE); // DELETE /mock/trips/:id
                    post("/trip/{tripId}/guide/{guideId}", tripMockController.addGuideToTrip(), Role.ANYONE); // POST /mock/trips/:id/guide
                });
            });
        };
    }
}