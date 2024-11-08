package dk.cphbusiness.rest.controllers;

/**
 * Purpose:
 *
 * @author: Thomas Hartmann
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.cphbusiness.dtos.TripDTO;
import dk.cphbusiness.dtos.GuideDTO;
import dk.cphbusiness.exceptions.ApiException;
import dk.cphbusiness.persistence.model.Trip;
import dk.cphbusiness.utils.Utils;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import io.javalin.validation.BodyValidator;

import java.time.LocalDateTime;
import java.util.*;

public class TripMockController implements IController {
    ObjectMapper objectMapper = new Utils().getObjectMapper();
    static Map<Long, TripDTO> trips = new HashMap<>();
    static Map<Long, GuideDTO> guides = new HashMap<>();
    private static Long tripIdCounter = 1L; // Counter for generating unique trip IDs
    private static Long guideIdCounter = 1L; // Counter for generating unique guide IDs

    static {
        // Initialize Guides
        GuideDTO guide1 = new GuideDTO(guideIdCounter++, "Allison", "Johnson", "alice@mail.com", "12345678", 10, null);
        GuideDTO guide2 = new GuideDTO(guideIdCounter++, "Bob", "Smith", "bob@mail.com", "87654321", 5, null);

        guides.put(guide1.getId(), guide1);
        guides.put(guide2.getId(), guide2);

        // Initialize Trips and assign them to guides
        TripDTO trip1 = new TripDTO(
                tripIdCounter++, // Use counter to generate ID
                LocalDateTime.of(2024, 5, 10, 9, 0),
                LocalDateTime.of(2024, 5, 10, 17, 0),
                12.34, 56.78, "Beach Day", 150.00, guide1.getId(), Trip.TripCategory.BEACH, guide1);

        TripDTO trip2 = new TripDTO(tripIdCounter++, LocalDateTime.of(2024, 6, 15, 10, 0), LocalDateTime.of(2024, 6, 15, 18, 0),
                13.45, 57.89, "City Tour", 200.00, guide1.getId(), Trip.TripCategory.CITY, guide1);

        TripDTO trip3 = new TripDTO(tripIdCounter++, LocalDateTime.of(2024, 7, 20, 8, 0), LocalDateTime.of(2024, 7, 20, 16, 0),
                14.56, 58.90, "Forest Adventure", 180.00, guide2.getId(), Trip.TripCategory.FOREST, guide2);

        TripDTO trip4 = new TripDTO(tripIdCounter++, LocalDateTime.of(2024, 8, 25, 9, 0), LocalDateTime.of(2024, 8, 25, 17, 0),
                15.67, 59.01, "Lake Visit", 220.00, guide2.getId(), Trip.TripCategory.LAKE, guide2);

        // Assign trips to guides' list of trips
        guide1.setTrips(List.of(trip1.getName(), trip2.getName()));
        guide2.setTrips(List.of(trip3.getName(), trip4.getName()));

        // Add trips to the trips map
        trips.put(trip1.getId(), trip1);
        trips.put(trip2.getId(), trip2);
        trips.put(trip3.getId(), trip3);
        trips.put(trip4.getId(), trip4);
    }

    // 1. Get all trips
    @Override
    public Handler getAll() {
        return ctx -> {
            ctx.json(trips.values());
        };
    }

    // 2. Get a trip by id
    @Override
    public Handler getById() {
        return ctx -> {
            Long id = Long.valueOf(ctx.pathParam("id"));
            System.out.println("ID: " + id);
            if (!trips.containsKey(id)) {
                throw new ApiException(404, "No trip found with that ID");
            }
            ctx.json(trips.get(id));
        };
    }

    // 3. Add a new trip
    @Override
    public Handler create() {
        return ctx -> {
            TripDTO tdto = objectMapper.readValue(ctx.body(), TripDTO.class);
            System.out.println("TripDTO: " + tdto);

            BodyValidator<TripDTO> validator = ctx.bodyValidator(TripDTO.class);
            validator.check(trip -> trip.getName().length() > 0, "Trip name must not be empty");
            TripDTO trip = validator.get();
            trip.setId(tripIdCounter++); // Generate new ID
            trips.put(trip.getId(), trip);
            ctx.json(trip).status(HttpStatus.CREATED);
        };
    }

    // 4. Update an existing trip
    @Override
    public Handler update() {
        return ctx -> {
            Long id = Long.valueOf(ctx.pathParam("id"));
            if (!trips.containsKey(id)) {
                throw new ApiException(404, "No trip found with that ID");
            }
            TripDTO updatedTrip = ctx.bodyAsClass(TripDTO.class);
            trips.put(id, updatedTrip);
            ctx.json(updatedTrip);
        };
    }

    // 5. Delete a trip
    @Override
    public Handler delete() {
        return ctx -> {
            Long id = Long.valueOf(ctx.pathParam("id"));
            if (!trips.containsKey(id)) {
                throw new ApiException(404, "No trip found with that ID");
            }
            TripDTO removedTrip = trips.remove(id);
            ctx.json(removedTrip);
        };
    }

    public Handler getAllGuides() {
        return ctx -> {
            ctx.json(guides.values());
        };
    }

    // 6. Add a guide to a trip
    public Handler addGuideToTrip() {
        return ctx -> {
            Long tripId = Long.valueOf(ctx.pathParam("tripId"));
            Long guideId = Long.valueOf(ctx.pathParam("guideId"));

            if (!trips.containsKey(tripId)) {
                throw new ApiException(404, "No trip found with that ID");
            }
            if (!guides.containsKey(guideId)) {
                throw new ApiException(404, "No guide found with that ID");
            }

            TripDTO trip = trips.get(tripId);
            GuideDTO guide = guides.get(guideId);

            trip.setGuide(guide); // Assuming TripDTO has a GuideDTO field
            ctx.json(trip);
        };
    }

    // Method to create a new GuideDTO
    public Handler createGuide() {
        return ctx -> {
            // Validate and parse the GuideDTO from the request body
            BodyValidator<GuideDTO> validator = ctx.bodyValidator(GuideDTO.class);
            validator.check(guide -> guide.getFirstname().length() > 0, "First name must not be empty");
            validator.check(guide -> guide.getLastname().length() > 0, "Last name must not be empty");
            validator.check(guide -> guide.getEmail().contains("@"), "Email must be valid");
            validator.check(guide -> guide.getYearsOfExperience() >= 0, "Years of experience must be non-negative");

            GuideDTO guide = validator.get();

            // Generate a new ID for the guide and store it in the map
            guide.setId(guideIdCounter++);
            guides.put(guide.getId(), guide);

            // Return the created GuideDTO with a 201 Created status
            ctx.json(guide).status(HttpStatus.CREATED);
        };
    }

    // Method to update an existing GuideDTO
    public Handler updateGuide() {
        return ctx -> {
            // Validate the Long path parameter
            Long guideId = Long.valueOf(ctx.pathParam("id"));
            if (!guides.containsKey(guideId)) {
                throw new ApiException(404, "Guide not found");
            }

            // Validate and parse the updated GuideDTO from the request body
            BodyValidator<GuideDTO> validator = ctx.bodyValidator(GuideDTO.class);
            validator.check(guide -> guide.getFirstname().length() > 0, "First name must not be empty");
            validator.check(guide -> guide.getLastname().length() > 0, "Last name must not be empty");
            validator.check(guide -> guide.getEmail().contains("@"), "Email must be valid");
            validator.check(guide -> guide.getYearsOfExperience() >= 0, "Years of experience must be non-negative");

            GuideDTO updatedGuide = validator.get();

            // Update the guide in the map with the new details
            guides.put(guideId, updatedGuide);

            // Return the updated GuideDTO
            ctx.json(updatedGuide);
        };
    }

    // Additional Helper Methods (Optional)
    public static Map<Long, TripDTO> getTripCollection() {
        return trips;
    }

    public static Map<Long, GuideDTO> getGuideCollection() {
        return guides;
    }
}
