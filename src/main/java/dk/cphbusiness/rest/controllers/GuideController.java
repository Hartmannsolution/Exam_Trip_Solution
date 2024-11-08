package dk.cphbusiness.rest.controllers;

import dk.cphbusiness.persistence.daos.GuideDAO;
import dk.cphbusiness.dtos.GuideDTO;
import dk.cphbusiness.exceptions.ApiException;
import dk.cphbusiness.persistence.HibernateConfig;
import dk.cphbusiness.persistence.daos.IDAO;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import io.javalin.validation.BodyValidator;

/**
 * Purpose: REST controller for GuideDTO.
 * Author: Thomas Hartmann
 */
public class GuideController implements IController {

    private static GuideController instance;
    private static IDAO<GuideDTO> guideDAO;

    private GuideController() { }

    public static GuideController getInstance() {
        if (instance == null) {
            instance = new GuideController();
        }
        guideDAO = GuideDAO.getGuideDAO(HibernateConfig.getEntityManagerFactory());
        return instance;
    }

    @Override
    public Handler getAll() {
        return ctx -> {
            ctx.status(HttpStatus.OK).json(guideDAO.getAll());
        };
    }

    @Override
    public Handler getById() {
        return ctx -> {
            Long id = Long.parseLong(ctx.pathParam("id"));
            GuideDTO guide = guideDAO.findById(id);
            if (guide == null) {
                ctx.attribute("msg", "No guide with that id");
                throw new ApiException(404, "No guide with that id");
            }
            ctx.status(HttpStatus.OK).json(guide);
        };
    }

    @Override
    public Handler create() {
        return ctx -> {
            BodyValidator<GuideDTO> validator = ctx.bodyValidator(GuideDTO.class);
            GuideDTO guide = validator.get();
            GuideDTO created = guideDAO.create(guide);
            ctx.json(created).status(HttpStatus.CREATED);
        };
    }

    @Override
    public Handler update() {
        return ctx -> {
            Long id = Long.parseLong(ctx.pathParam("id"));
            GuideDTO guide = ctx.bodyAsClass(GuideDTO.class);
            guide.setId(id);
            GuideDTO updated = guideDAO.update(guide);
            ctx.json(updated);
        };
    }

    @Override
    public Handler delete() {
        return ctx -> {
            Long id = Long.parseLong(ctx.pathParam("id"));
            GuideDTO guide = guideDAO.findById(id);
            if (guide == null) {
                throw new ApiException(404, "No guide with that id");
            }
            guideDAO.delete(guide);
            ctx.json(guide);
        };
    }
}
