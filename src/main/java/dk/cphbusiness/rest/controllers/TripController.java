package dk.cphbusiness.rest.controllers;

/**
 * Purpose:
 *
 * @author: Thomas Hartmann
 */

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.cphbusiness.dtos.PackingItemDTO;
import dk.cphbusiness.exceptions.ValidationException;
import dk.cphbusiness.persistence.daos.IDAO;
import dk.cphbusiness.persistence.daos.ITripGuideDAO;
import dk.cphbusiness.persistence.daos.TripDAO;
import dk.cphbusiness.dtos.TripDTO;
import dk.cphbusiness.exceptions.ApiException;
import dk.cphbusiness.persistence.HibernateConfig;
import dk.cphbusiness.utils.Utils;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import io.javalin.validation.BodyValidator;
import jakarta.persistence.EntityNotFoundException;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Purpose: REST controller for TripDTO.
 * Author: Thomas Hartmann
 */
public class TripController implements IController {

    private static TripController instance;
    private static IDAO<TripDTO> tripDAO;
    private static ITripGuideDAO tripGuideDAO;
    private static ObjectMapper objectMapper = Utils.getObjectMapper();

    private TripController() {
    }

    public static TripController getInstance() {
        if (instance == null) {
            instance = new TripController();
        }
        tripDAO = TripDAO.getTripDAO(HibernateConfig.getEntityManagerFactory());
        tripGuideDAO = TripDAO.getTripGuideDAO(HibernateConfig.getEntityManagerFactory());
        return instance;
    }

    @Override
    public Handler getAll() {
        return ctx -> {
            ctx.status(HttpStatus.OK).json(tripDAO.getAll());
        };
    }

    @Override
    public Handler getById() {
        return ctx -> {
            Long id = Long.parseLong(ctx.pathParam("id"));
            TripDTO trip = tripDAO.getById(id);
            if (trip == null) {
                ctx.attribute("msg", "No trip with that id");
                throw new ApiException(404, "No trip with that id");
            }
            trip = addPackingItems(addPackingItems(trip));
            ctx.status(HttpStatus.OK).json(trip);
        };
    }

    private TripDTO addPackingItems(TripDTO trip) {
        String category = trip.getCategory().toString();
        String url = "https://packingapi.cphbusinessapps.dk/packinglist/" + category.toLowerCase();
        System.out.println("URL: " + url);
        try {
            JsonNode allData = objectMapper.readTree(new URL(url));
            Set<PackingItemDTO> packingItems = objectMapper.convertValue(allData.get("items"), new TypeReference<Set<PackingItemDTO>>() { });
            System.out.println("Packing items: " + packingItems);
            trip.setPackingItems(packingItems);
            return trip;
        } catch (IOException e) {
            throw new ApiException(500, "An error occurred while fetching packing items");
        }
    }

    @Override
    public Handler create() {
        return ctx -> {
            try {
                TripDTO trip = ctx.bodyAsClass(TripDTO.class);
//                BodyValidator<TripDTO> validator = ctx.bodyValidator(TripDTO.class);
//                TripDTO trip = validator.get();
                TripDTO created = tripDAO.create(trip);
                ctx.json(created).status(HttpStatus.CREATED);
            } catch (Exception e) {
                e.printStackTrace();
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("An error occurred: " + e.getMessage());
            }
        };
    }

    @Override
    public Handler update() {
        return ctx -> {
            Long id = Long.parseLong(ctx.pathParam("id"));
            TripDTO trip = ctx.bodyAsClass(TripDTO.class);
            trip.setId(id);
            TripDTO updated = tripDAO.update(trip);
            ctx.json(updated);
        };
    }

    @Override
    public Handler delete() {
        return ctx -> {
            Long id = Long.parseLong(ctx.pathParam("id"));
            TripDTO trip = tripDAO.getById(id);
            if (trip == null) {
                throw new ApiException(404, "No trip with that id");
            }
            tripDAO.delete(trip);
            ctx.json(trip);
        };
    }

    public Handler addGuideToTrip() {
        return ctx -> {
            Long tripId = Long.valueOf(ctx.pathParam("tripId"));
            Long guideId = Long.valueOf(ctx.pathParam("guideId"));

            try {
                TripDTO updatedTrip = tripGuideDAO.addGuideToTrip(tripId, guideId);
                ctx.json(updatedTrip).status(HttpStatus.OK);
            } catch (EntityNotFoundException e) {
                throw new ApiException(404, e.getMessage());
            } catch (Exception e) {
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("An error occurred: " + e.getMessage());
            }
        };
    }

    public Handler getFilteredTrips() {
        return ctx -> {
            Set<TripDTO> trips = tripDAO
                    .getAll()
                    .stream()
                    .filter((trip) -> trip.getCategory().toString().equals(ctx.pathParam("category").toUpperCase()))
                    .collect(Collectors.toSet());
            ctx.status(HttpStatus.OK).json(trips);
        };
    }

    public Handler getSumOfTripsByGuide() {
        return ctx -> {
            Map<String, Double> result = tripDAO.getAll()
                    .stream()
                    .collect(Collectors.groupingBy(trip->trip.getGuide().getLastname(), Collectors.summingDouble(TripDTO::getPrice)));
            ctx.json(result).status(HttpStatus.OK);
        };
    }
}

