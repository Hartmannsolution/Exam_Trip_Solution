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
@Table(name = "ratings")
@NamedQueries(@NamedQuery(name = "Rating.deleteAllRows", query = "DELETE from Rating"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Rating implements Serializable,  IIdProvider<Long> {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "booking_id", length = 25)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "username")
    private Participant participant;

    @ManyToOne
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @Enumerated(EnumType.STRING)
    private RatingScore ratingScore;
    private String comment;

    public Rating(Participant participant, Trip trip, RatingScore score, String comment) {
        this.participant = participant;
        this.trip = trip;
        this.ratingScore = score;
        this.comment = comment;
    }

    @Override
    public Long getId() {
        return id;
    }

    public enum RatingScore {
        ONE, TWO, THREE, FOUR, FIVE
    }
}
