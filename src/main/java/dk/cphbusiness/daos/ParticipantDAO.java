package dk.cphbusiness.daos;

import dk.cphbusiness.dtos.ParticipantDTO;
import dk.cphbusiness.persistence.daos.IDAO;
import dk.cphbusiness.persistence.model.Participant;
import dk.cphbusiness.persistence.model.User;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;

import java.util.Set;

/**
 * Purpose:
 *
 * @author: Thomas Hartmann
 */
public class ParticipantDAO implements IDAO<ParticipantDTO> {
    private static ParticipantDAO instance;
    private EntityManagerFactory emf;

    protected ParticipantDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public static ParticipantDAO getParticipantDAO(EntityManagerFactory emf) {
        if (instance == null) {
            instance = new ParticipantDAO(emf);
        }
        return instance;
    }


    @Override
    public ParticipantDTO getById(Object id) throws EntityNotFoundException {
        try(var em = emf.createEntityManager()) {
            var found = em.find(ParticipantDTO.class, id);
            if (found == null) {
                throw new EntityNotFoundException("Unknown entity with id: " + id);
            }
            return found;
        }
    }

    @Override
    public Set<ParticipantDTO> getAll() {
        try(var em = emf.createEntityManager()) {
            var query = em.createQuery("SELECT new dk.cphbusiness.dtos.ParticipantDTO(p) FROM Participant p", ParticipantDTO.class);
            return Set.copyOf(query.getResultList());
        }
    }

    @Override
    public ParticipantDTO create(ParticipantDTO dto) throws Exception {
        try(var em = emf.createEntityManager()) {
            // A Particpant must be a security user first (be able to login)
            User user = em.find(User.class, dto.getUsername());
            if (user != null) {
                throw new Exception("User does not exist");
            }
            em.getTransaction().begin();
            em.persist(dto);
            em.getTransaction().commit();
            return dto;
        }
    }

    @Override
    public ParticipantDTO update(ParticipantDTO dto) throws EntityNotFoundException {
        try(var em = emf.createEntityManager()) {
            var found = em.find(Participant.class, dto.getUsername());
            if (found == null) {
                throw new EntityNotFoundException("Unknown entity with username: " + dto.getUsername());
            }
            em.getTransaction().begin();
            found.setLevel(dto.getLevel());
            found.setEmail(dto.getEmail());
            found.setPhone(dto.getPhone());
            em.getTransaction().commit();
            return new ParticipantDTO(found);
        }
    }

    @Override
    public void delete(ParticipantDTO dto) throws EntityNotFoundException {
        try(var em = emf.createEntityManager()) {
            var found = em.find(Participant.class, dto.getUsername());
            if (found == null) {
                throw new EntityNotFoundException("Unknown entity with username: " + dto.getUsername());
            }
            em.getTransaction().begin();
            em.remove(found);
            em.getTransaction().commit();
        }
    }
}
