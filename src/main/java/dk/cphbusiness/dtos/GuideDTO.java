package dk.cphbusiness.dtos;

/**
 * Purpose:
 *
 * @author: Thomas Hartmann
 */
import dk.cphbusiness.persistence.model.Guide;
import dk.cphbusiness.utils.IIdProvider;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class GuideDTO implements IIdProvider<Long> {
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private int yearsOfExperience;
    private List<String> trips;

    public GuideDTO( String firstname, String lastname, String email, String phone, int yearsOfExperience) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.yearsOfExperience = yearsOfExperience;
    }

    public GuideDTO(Guide guide) {
        if(guide.getId() != null) this.id = guide.getId();
        this.firstname = guide.getFirstName();
        this.lastname = guide.getLastName();
        this.email = guide.getEmail();
        this.phone = guide.getPhone();
        this.yearsOfExperience = guide.getYearsOfExperience();
        this.trips = guide.getTrips().stream().map(trip -> trip.getName()).toList();
    }
    public static Guide toEntity(GuideDTO guideDTO) {
        return Guide.builder()
                .firstName(guideDTO.getFirstname())
                .lastName(guideDTO.getLastname())
                .email(guideDTO.getEmail())
                .phone(guideDTO.getPhone())
                .yearsOfExperience(guideDTO.getYearsOfExperience())
                .build();
    }
}

