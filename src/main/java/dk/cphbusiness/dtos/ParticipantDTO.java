package dk.cphbusiness.dtos;

import dk.bugelhartmann.UserDTO;
import dk.cphbusiness.persistence.model.Participant;
import dk.cphbusiness.persistence.model.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ParticipantDTO {
    private String username;
    private String phone;
    private String email;
    private Participant.ExperienceLevel level;



    public ParticipantDTO(String username, Participant.ExperienceLevel level) {
        this.username = username;
        this.level = level;
    }

    public ParticipantDTO(String username) {
        this.username = username;
    }

    public ParticipantDTO(Participant participant) {
        this.username = participant.getUsername();
        this.level = participant.getLevel();
        this.phone = participant.getPhone();
        this.email = participant.getEmail();
    }
    public static Participant toEntity(ParticipantDTO participantDTO) {
        return new Participant(participantDTO.getUsername(), participantDTO.phone, participantDTO.email, participantDTO.getLevel());
    }
}
