package dk.cphbusiness.dtos;

import dk.cphbusiness.persistence.model.Booking;
import dk.cphbusiness.persistence.model.Rating;
import dk.cphbusiness.utils.IIdProvider;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RatingDTO implements IIdProvider<Long> {
    private Long id;
    private ParticipantDTO participant;
    private TripDTO trip;
    private Rating.RatingScore ratingScore;
    private String comment;

    public RatingDTO(Rating rating) {
        if(rating.getId() != null) this.id = rating.getId();
        if (rating.getParticipant() != null) {
            this.participant = new ParticipantDTO(rating.getParticipant());
        }
        if (rating.getTrip() != null) {
            this.trip = new TripDTO(rating.getTrip());
        }
        this.ratingScore = rating.getRatingScore();
        this.comment = rating.getComment();
    }
    public RatingDTO(ParticipantDTO participant, TripDTO trip, Rating.RatingScore ratingScore, String comment) {
        this.participant = participant;
        this.trip = trip;
        this.ratingScore = ratingScore;
        this.comment = comment;
    }
}
