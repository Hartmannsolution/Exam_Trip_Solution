package dk.cphbusiness.rest.controllers;

import dk.cphbusiness.daos.ParticipantDAO;
import dk.cphbusiness.dtos.BookingDTO;
import dk.cphbusiness.dtos.ParticipantDTO;
import dk.cphbusiness.exceptions.ApiException;
import dk.cphbusiness.persistence.HibernateConfig;
import dk.cphbusiness.persistence.daos.IDAO;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;

/**
 * Purpose:
 *
 * @author: Thomas Hartmann
 */
public class ParticipantController implements IController {
    private static ParticipantController intance;
    private static IDAO<ParticipantDTO> participantDAO;

    private ParticipantController() {
    }

    public static ParticipantController getParticipantDAO() {
        if (intance == null) {
            intance = new ParticipantController();
            participantDAO = ParticipantDAO.getParticipantDAO(HibernateConfig.getEntityManagerFactory());
        }
        return intance;
    }

    @Override
    public Handler getAll() {
        return ctx -> {
            ctx.status(HttpStatus.OK).json(participantDAO.getAll());
        };
    }

    @Override
    public Handler getById() {
        return ctx -> {
            Long id = Long.parseLong(ctx.pathParam("id"));
            ParticipantDTO booking = participantDAO.getById(id);
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
            ParticipantDTO bookingDTO = ctx.bodyAsClass(ParticipantDTO.class);
            participantDAO.create(bookingDTO);
            ctx.status(HttpStatus.CREATED).json(bookingDTO);
        };
    }

    @Override
    public Handler update() {
        return ctx -> {
            ParticipantDTO bookingDTO = ctx.bodyAsClass(ParticipantDTO.class);
            participantDAO.update(bookingDTO);
            ctx.status(HttpStatus.OK).json(bookingDTO);
        };
    }

    @Override
    public Handler delete() {
        return ctx -> {
            Long id = Long.parseLong(ctx.pathParam("id"));
            ParticipantDTO found = participantDAO.getById(id);
            participantDAO.delete(found);
            ctx.status(HttpStatus.NO_CONTENT);
        };
    }

}
