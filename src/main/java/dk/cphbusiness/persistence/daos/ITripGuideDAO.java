package dk.cphbusiness.persistence.daos;

import dk.cphbusiness.dtos.TripDTO;
import dk.cphbusiness.exceptions.EntityNotFoundException;
import dk.cphbusiness.persistence.model.Trip;

import java.util.Set;

/**
 * Purpose:
 *
 * @author: Thomas Hartmann
 */
public interface ITripGuideDAO {
    TripDTO addGuideToTrip(Long tripId, Long guideId) throws EntityNotFoundException;
    Set<TripDTO> getTripsByGuide(int guideId);
}
