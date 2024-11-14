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
    private Participant.ExperienceLevel level;

    public ParticipantDTO(Participant participant) {
        this.username = participant.getUsername();
        this.level = participant.getLevel();
    }
    public static Participant toEntity(ParticipantDTO participantDTO) {
        return new Participant(participantDTO.getUsername(), participantDTO.getLevel());
    }
}
