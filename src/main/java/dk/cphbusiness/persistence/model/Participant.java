package dk.cphbusiness.persistence.model;

import dk.cphbusiness.utils.IIdProvider;
import jakarta.persistence.*;
import lombok.*;
import org.mindrot.jbcrypt.BCrypt;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Purpose: To handle security in the API
 * Author: Thomas Hartmann
 */
@Entity
@Table(name = "participants")
@NamedQueries(@NamedQuery(name = "Participant.deleteAllRows", query = "DELETE from User"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Participant implements Serializable,  IIdProvider<String> {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "username", length = 25)
    private String username;
    private String phone;
    private String email;
    private ExperienceLevel level;

    @OneToOne
    @JoinColumn(name = "username")
    @MapsId // This is to tell JPA that the primary key of this entity is a foreign key
    private User user;

    @OneToMany(mappedBy = "participant")
    private Set<Booking> bookings = new HashSet<>();

    public Participant(String userName, ExperienceLevel level) {
        this.username = userName;
        this.level = level;
    }

    public Participant(String userName, String phone, String email, ExperienceLevel level) {
        this.username = userName;
        this.phone = phone;
        this.email = email;
        this.level = level;
    }

    public String getId(){
        return username;
    }
    public static enum ExperienceLevel {
        BEGINNER, INTERMEDIATE, ADVANCED
    }
}
