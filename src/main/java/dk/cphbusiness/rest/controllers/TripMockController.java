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
    private TripController tripController = TripController.getInstance();

    static {
        initializeData();
    }

    private static void initializeData() {
        GuideDTO guide1 = new GuideDTO(guideIdCounter++, "Allison", "Johnson", "alice@mail.com", "12345678", 10, null);
        GuideDTO guide2 = new GuideDTO(guideIdCounter++, "Bob", "Smith", "bob@mail.com", "87654321", 5, null);
        GuideDTO guide3 = new GuideDTO(guideIdCounter++, "Charlie", "Brown", "charlie@mail.com", "11223344", 8, null);
        GuideDTO guide4 = new GuideDTO(guideIdCounter++, "Diana", "Prince", "diana@mail.com", "44332211", 12, null);

        guides.put(guide1.getId(), guide1);
        guides.put(guide2.getId(), guide2);
        guides.put(guide3.getId(), guide2);
        guides.put(guide4.getId(), guide2);

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

        TripDTO trip5 = new TripDTO(tripIdCounter++, LocalDateTime.of(2024, 9, 5, 7, 0), LocalDateTime.of(2024, 9, 5, 15, 0),
                16.78, 60.12, "Mountain Hike", 250.00, guide3.getId(), Trip.TripCategory.SEA, guide3);
        TripDTO trip6 = new TripDTO(tripIdCounter++, LocalDateTime.of(2024, 10, 10, 6, 0), LocalDateTime.of(2024, 10, 10, 14, 0),
                17.89, 61.23, "Desert Safari", 300.00, guide4.getId(), Trip.TripCategory.BEACH, guide4);
        TripDTO trip7 = new TripDTO(tripIdCounter++, LocalDateTime.of(2024, 11, 15, 5, 0), LocalDateTime.of(2024, 11, 15, 13, 0),
                18.90, 62.34, "Jungle Expedition", 350.00, guide3.getId(), Trip.TripCategory.FOREST, guide3);
        TripDTO trip8 = new TripDTO(tripIdCounter++, LocalDateTime.of(2024, 12, 20, 4, 0), LocalDateTime.of(2024, 12, 20, 12, 0),
                19.01, 63.45, "Snow Trekking", 400.00, guide4.getId(), Trip.TripCategory.SNOW, guide4);

        // Assign trips to guides' list of trips
        guide1.setTrips(List.of(trip1.getName(), trip2.getName()));
        guide2.setTrips(List.of(trip3.getName(), trip4.getName()));
        guide3.setTrips(List.of(trip5.getName(), trip7.getName()));
        guide4.setTrips(List.of(trip6.getName(), trip8.getName()));

        // Add trips to the trips map
        trips.put(trip1.getId(), trip1);
        trips.put(trip2.getId(), trip2);
        trips.put(trip3.getId(), trip3);
        trips.put(trip4.getId(), trip4);
        trips.put(trip5.getId(), trip5);
        trips.put(trip6.getId(), trip6);
        trips.put(trip7.getId(), trip7);
        trips.put(trip8.getId(), trip8);
    }

    // Reset method to clear and reinitialize data
    public Handler reset() {
        return ctx -> {
            trips.clear();
            guides.clear();
            tripIdCounter = 1L;
            guideIdCounter = 1L;
            initializeData();
        };
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
            TripDTO trip = trips.get(id);
            trip = tripController.addPackingItems(trip);
            ctx.json(trip);
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
