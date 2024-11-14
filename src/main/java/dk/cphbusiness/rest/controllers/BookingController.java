package dk.cphbusiness.rest.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dk.cphbusiness.dtos.BookingDTO;
import dk.cphbusiness.exceptions.ApiException;
import dk.cphbusiness.persistence.daos.IBookingSelector;
import dk.cphbusiness.persistence.daos.IDAO;
import dk.cphbusiness.persistence.daos.BookingDAO;
import dk.cphbusiness.persistence.HibernateConfig;
import dk.cphbusiness.utils.Utils;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import io.javalin.validation.BodyValidator;
import jakarta.persistence.EntityNotFoundException;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * REST Controller for BookingDTO.
 * Handles CRUD operations and additional functionality for Bookings.
 */
public class BookingController implements IController {

    private static BookingController instance;
    private static IDAO<BookingDTO> bookingDAO;
    private static ObjectMapper objectMapper = Utils.getObjectMapper();

    private BookingController() {
    }

    public static BookingController getInstance() {
        if (instance == null) {
            instance = new BookingController();
        }
        bookingDAO = BookingDAO.getBookingDAO(HibernateConfig.getEntityManagerFactory());
        return instance;
    }

    @Override
    public Handler getAll() {
        return ctx -> {
            ctx.status(HttpStatus.OK).json(bookingDAO.getAll());
        };
    }

    @Override
    public Handler getById() {
        return ctx -> {
            Long id = Long.parseLong(ctx.pathParam("id"));
            BookingDTO booking = bookingDAO.getById(id);
            if (booking == null) {
                ctx.attribute("msg", "No booking with that ID");
                throw new ApiException(404, "No booking with that ID");
            }
            ctx.status(HttpStatus.OK).json(booking);
        };
    }

    @Override
    public Handler create() {
        return ctx -> {
            BodyValidator<BookingDTO> validator = ctx.bodyValidator(BookingDTO.class);
            BookingDTO booking = validator.get();
            BookingDTO created = bookingDAO.create(booking);
            ctx.json(created).status(HttpStatus.CREATED);
        };
    }

    @Override
    public Handler update() {
        return ctx -> {
            Long id = Long.parseLong(ctx.pathParam("id"));
            BookingDTO booking = ctx.bodyAsClass(BookingDTO.class);
            booking.setId(id);
            BookingDTO updated = bookingDAO.update(booking);
            ctx.json(updated);
        };
    }

    @Override
    public Handler delete() {
        return ctx -> {
            Long id = Long.parseLong(ctx.pathParam("id"));
            BookingDTO booking = bookingDAO.getById(id);
            if (booking == null) {
                throw new ApiException(404, "No booking with that ID");
            }
            bookingDAO.delete(booking);
            ctx.status(204).json(booking);
        };
    }

    public Handler getBookingsByParticipant() {
        return ctx -> {
            String username = ctx.pathParam("username");
            Set<BookingDTO> bookings = ((IBookingSelector) bookingDAO).getBookingsByParticipant(username);
            ctx.status(HttpStatus.OK).json(bookings);
        };
    }

    public Handler getBookingsByTrip() {
        return ctx -> {
            Long tripId = Long.parseLong(ctx.pathParam("tripId"));
            Set<BookingDTO> bookings = ((IBookingSelector) bookingDAO).getBookingsByTrip(tripId);
            ctx.status(HttpStatus.OK).json(bookings);
        };
    }

    public Handler getSumParticipantForTrip() {
        return ctx -> {
            // Get the sum of participant
           int result = bookingDAO.getAll()
                   .stream()
                   .filter(booking -> booking.getTrip().getId() == Long.parseLong(ctx.pathParam("tripId")))
                   .mapToInt(BookingDTO::getNumberOfParticipants)
                     .sum();
           // Object node to hold the result
            ObjectNode objectNode = objectMapper.createObjectNode();
            ctx.status(HttpStatus.OK).json(objectNode.put("sum", result));
        };
    }
}
