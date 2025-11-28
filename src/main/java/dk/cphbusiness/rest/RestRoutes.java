package dk.cphbusiness.rest;

import dk.cphbusiness.persistence.HibernateConfig;
import dk.cphbusiness.persistence.model.Trip;
import dk.cphbusiness.rest.controllers.GuideController;
import dk.cphbusiness.rest.controllers.TripController;
import dk.cphbusiness.rest.controllers.TripMockController;
import dk.cphbusiness.security.SecurityRoutes.Role;
import dk.cphbusiness.utils.Populator;
import io.javalin.apibuilder.EndpointGroup;

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

    public EndpointGroup getTripRoutes() {
        return () -> {
            path("", () -> {
                path("populate", () -> {
                    get("trips", (ctx)-> {new Populator().createTripsAndGuides(HibernateConfig.getEntityManagerFactory()); ctx.json("{\"msg\":\"Success\"}");}, Role.ADMIN);
                    get("users", (ctx)-> {new Populator().createUsersAndRoles(HibernateConfig.getEntityManagerFactory()); ctx.json("{\"msg\":\"Success\"}");}, Role.ADMIN);
                });
                path("guides", () -> {
                    get("", guideController.getAll(), Role.ADMIN);        // GET /guides
                    post("", guideController.create(), Role.ANYONE);        // POST /guides
                    put("{id}", guideController.update(), Role.ANYONE);    // PUT /guides/:id
                });
                path("trips", () -> {
                    get("", tripController.getAll(), Role.ANYONE);        // GET /trips
                    get("sumOfTripsForGuides", tripController.getSumOfTripsByGuide(), Role.ANYONE);   // GET /trips/:id
                    get("{id}", tripController.getById(), Role.USER);   // GET /trips/:id
                    get("category/{category}", tripController.getFilteredTrips(), Role.ANYONE);   // GET /trips/:id
                    post("", tripController.create(), Role.ANYONE);        // POST /trips
                    put("{id}", tripController.update(), Role.ANYONE);    // PUT /trips/:id
                    delete("{id}", tripController.delete(), Role.ANYONE); // DELETE /trips/:id
                    put("trip/{tripId}/guide/{guideId}", tripController.addGuideToTrip(), Role.ANYONE); // POST /trips/:id/guide
                });
            });
            path("mock", () -> {
                get("reset", tripMockController.reset(), Role.ANYONE);
                path("guides", () -> {
                    get("", tripMockController.getAllGuides(), Role.ANYONE);        // GET /mock/guides
                    post("", tripMockController.createGuide(), Role.ANYONE);        // POST /mock/guides
                    put("{id}", tripMockController.updateGuide(), Role.ANYONE);    // PUT /mock/guides/:id
                });
                path("trips", () -> {
                    get("", tripMockController.getAll(), Role.ANYONE);        // GET /mock/trips
                    get("{id}", tripMockController.getById(), Role.ANYONE);   // GET /mock/trips/:id
                    post("", tripMockController.create(), Role.ANYONE);        // POST /mock/trips
                    put("{id}", tripMockController.update(), Role.ANYONE);    // PUT /mock/trips/:id
                    delete("{id}", tripMockController.delete(), Role.ANYONE); // DELETE /mock/trips/:id
                    post("trip/{tripId}/guide/{guideId}", tripMockController.addGuideToTrip(), Role.ANYONE); // POST /mock/trips/:id/guide
                });
            });
        };
    }
}