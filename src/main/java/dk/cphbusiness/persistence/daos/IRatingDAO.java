package dk.cphbusiness.persistence.daos;

import dk.cphbusiness.dtos.ParticipantDTO;
import dk.cphbusiness.dtos.RatingDTO;
import dk.cphbusiness.dtos.TripDTO;
import dk.cphbusiness.persistence.model.Rating;

import java.util.Set;

/**
 * Purpose:
 *
 * @author: Thomas Hartmann
 */
public interface IRatingDAO {
    Set<RatingDTO> getAllRatings();
    Set<RatingDTO> getRatingsByParticipant(ParticipantDTO participant);
    Set<RatingDTO> getRatingsByTrip(TripDTO trip);
    RatingDTO getRatingById(Long id);
    RatingDTO createRating(RatingDTO rating);
    RatingDTO updateRating(RatingDTO rating);
    RatingDTO deleteRating(Long id);
}
