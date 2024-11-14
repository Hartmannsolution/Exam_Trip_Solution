package dk.cphbusiness.persistence.model;

import dk.cphbusiness.utils.IIdProvider;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * Purpose: To handle security in the API
 * Author: Thomas Hartmann
 */
@Entity
@Table(name = "bookings")
@NamedQueries(@NamedQuery(name = "Booking.deleteAllRows", query = "DELETE from User"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Booking implements Serializable,  IIdProvider<Long> {

    @Serial
    private static final long serialVersionUID = 1L;


    @Id
    @Column(name = "booking_id", length = 25)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @ManyToOne
    @JoinColumn(name = "username")
    private Participant participant;

    @ManyToOne
    @JoinColumn(name = "trip_id")
    private Trip trip;

    private int numberOfParticipants;
    private boolean isPaid;
    private String comment;

    public Booking(Participant participant, Trip trip, int numberOfParticipants, boolean isPaid, String comment) {
        this.participant = participant;
        this.trip = trip;
        this.numberOfParticipants = numberOfParticipants;
        this.isPaid = isPaid;
        this.comment = comment;
    }

    @Override
    public Long getId() {
        return bookingId;
    }
}
