package dk.cphbusiness.persistence.model;

/**
 * Purpose:
 *
 * @author: Thomas Hartmann
 */

import dk.cphbusiness.utils.IIdProvider;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Purpose of this class is to represent a Trip entity
 * Author: Your Name
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@NamedQueries({
        @NamedQuery(name = "Trip.deleteAll", query = "DELETE FROM Trip")
})
public class Trip implements IIdProvider<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "longitude", nullable = false)
    private double longitude;

    @Column(name = "latitude", nullable = false)
    private double latitude;

    @Column(name = "price", nullable = false)
    private double price;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private TripCategory category;

    @ManyToOne
    @JoinColumn(name = "guide_id")
    private Guide guide;

    @Builder
    public Trip(Long id, String name, LocalDateTime startTime, LocalDateTime endTime, double longitude, double latitude, double price, TripCategory category, Guide guide) {
        if(id != null) this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.longitude = longitude;
        this.latitude = latitude;
        this.price = price;
        this.category = category;
        this.guide = guide;
    }
    public void addGuide(Guide guide) {
        this.guide = guide;
        guide.getTrips().add(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trip trip = (Trip) o;
        return id.equals(trip.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Trip{" +
                ", name='" + name + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", price=" + price +
                ", category=" + category +
                ", guide=" + guide.getId() + // Display guide ID
                '}';
    }

    public enum TripCategory {
        BEACH, CITY, FOREST, LAKE, SEA, SNOW
    }
}

