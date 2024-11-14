package dk.cphbusiness.dtos;

import dk.cphbusiness.persistence.model.Booking;
import dk.cphbusiness.persistence.model.Participant;
import dk.cphbusiness.persistence.model.Trip;
import dk.cphbusiness.utils.IIdProvider;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BookingDTO implements IIdProvider<Long> {
    private Long id;
    private ParticipantDTO participant;
    private TripDTO trip;
    private int numberOfParticipants;
    private boolean isPaid;
    private String comment;

    public BookingDTO(Booking booking) {
        if(booking.getBookingId() != null) this.id = booking.getBookingId();
        if (booking.getParticipant() != null) {
            this.participant = new ParticipantDTO(booking.getParticipant());
        }
        if (booking.getTrip() != null) {
            this.trip = new TripDTO(booking.getTrip());
        }
        this.numberOfParticipants = booking.getNumberOfParticipants();
        this.isPaid = booking.isPaid();
        this.comment = booking.getComment();
    }
    public BookingDTO(ParticipantDTO participant, TripDTO trip, int numberOfParticipants, boolean isPaid, String comment) {
        this.participant = participant;
        this.trip = trip;
        this.numberOfParticipants = numberOfParticipants;
        this.isPaid = isPaid;
        this.comment = comment;
    }

}
