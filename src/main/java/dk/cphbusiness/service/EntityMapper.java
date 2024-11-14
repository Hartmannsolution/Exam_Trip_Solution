package dk.cphbusiness.service;

import dk.cphbusiness.dtos.BookingDTO;
import dk.cphbusiness.dtos.ParticipantDTO;
import dk.cphbusiness.dtos.TripDTO;
import dk.cphbusiness.persistence.model.Booking;
import dk.cphbusiness.persistence.model.Participant;
import dk.cphbusiness.persistence.model.Trip;
import dk.cphbusiness.persistence.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;

public class EntityMapper {

    private static EntityManagerFactory emf;

    public EntityMapper(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public Participant dtoToParticipant(ParticipantDTO participantDTO) {
        try(EntityManager em = emf.createEntityManager()) {
            Participant participant = ParticipantDTO.toEntity(participantDTO);
            User user = em.find(User.class, participantDTO.getUsername());
            Participant foundParticipant = em.find(Participant.class, participantDTO.getUsername());

            if (user == null) {
                throw new EntityNotFoundException("User with username " + participantDTO.getUsername() + " not found");
            }

            if(foundParticipant != null) {
                return foundParticipant;
            }

            em.getTransaction().begin();
            participant.setUser(user);
            em.persist(participant);
            em.getTransaction().commit();

            return participant;
        }
    }

    public Booking dtoToBooking(BookingDTO bookingDTO) {
        ParticipantDTO participantDTO = bookingDTO.getParticipant();
        TripDTO tripDTO = bookingDTO.getTrip();
        if(participantDTO == null || tripDTO == null) {
            throw new IllegalArgumentException("Participant and Trip must be set");
        }
        Participant participantEntity = dtoToParticipant(participantDTO);
        Trip tripEntity = TripDTO.toEntity(tripDTO);

        Booking booking = new Booking(participantEntity, tripEntity, bookingDTO.getNumberOfParticipants(), bookingDTO.isPaid(), bookingDTO.getComment());
        booking.setBookingId(bookingDTO.getId());

        return booking;
    }
}
