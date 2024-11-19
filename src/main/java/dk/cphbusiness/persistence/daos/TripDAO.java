package dk.cphbusiness.persistence.daos;

import dk.cphbusiness.dtos.ParticipantDTO;
import dk.cphbusiness.dtos.RatingDTO;
import dk.cphbusiness.dtos.TripDTO;
import dk.cphbusiness.persistence.model.Guide;
import dk.cphbusiness.persistence.model.Participant;
import dk.cphbusiness.persistence.model.Rating;
import dk.cphbusiness.persistence.model.Trip;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;

import javax.lang.model.UnknownEntityException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Purpose:
 *
 * @author: Thomas Hartmann
 */
public class TripDAO implements IDAO<TripDTO>, ITripGuideDAO, IRatingDAO {

    private EntityManagerFactory emf;
    private static IDAO tripDAO;
    private static ITripGuideDAO tripGuideDAO;
    protected TripDAO( EntityManagerFactory emf) {
        this.emf = emf;
    }

    public static IDAO getTripDAO(EntityManagerFactory _emf) {
        if(tripDAO == null) {
            tripDAO = new TripDAO(_emf);
        }
        return tripDAO;
    }

    public static ITripGuideDAO getTripGuideDAO(EntityManagerFactory _emf) {
        if(tripGuideDAO == null) { tripGuideDAO = new TripDAO(_emf); }
        return tripGuideDAO;
    }

    // Getter is used in E.G PersonDAO to get emf from super class
    public EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }
    // Queries
    @Override
    public TripDTO getById(Object id) throws EntityNotFoundException {
        try (EntityManager em = emf.createEntityManager()) {
            Trip found = em.find(Trip.class, id);
            if (found == null) {
                throw new EntityNotFoundException("Unknown entity with id: " + id);
            }
            return new TripDTO(found);
        }
    }

    @Override
    public Set<TripDTO> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<TripDTO> query = em.createQuery("SELECT new dk.cphbusiness.dtos.TripDTO(t) FROM  Trip t", TripDTO.class);
            Set<TripDTO> result = query.getResultStream().collect(Collectors.toSet());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public TripDTO create(TripDTO dto) throws Exception{
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Trip trip = TripDTO.toEntity(dto);
            em.persist(trip);
            em.getTransaction().commit();
            return new TripDTO(trip);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error creating trip: " + e.getMessage());
        }
    }

    @Override
    public TripDTO update(TripDTO tripDTO) throws EntityNotFoundException {
        try (EntityManager em = emf.createEntityManager()) {
            Trip trip = TripDTO.toEntity(tripDTO);
            Trip found = em.find(Trip.class, trip.getId());
            if(found == null) {
                throw new EntityNotFoundException("Trip not found with id: " + tripDTO.getId());
            }
            em.getTransaction().begin();
            Trip merged = em.merge(trip);
            em.getTransaction().commit();
            return new TripDTO(merged);
        }
    }

    @Override
    public void delete(TripDTO tripDTO) throws EntityNotFoundException {
        try (EntityManager em = emf.createEntityManager()) {
            Trip trip = TripDTO.toEntity(tripDTO);
            Trip found = em.find(Trip.class, trip.getId());
            if(found == null) {
                throw new EntityNotFoundException();
            }
            em.getTransaction().begin();
            em.remove(found);
            em.getTransaction().commit();
        }
    }

    @Override
    public TripDTO addGuideToTrip(Long tripId, Long guideId) throws EntityNotFoundException {
        try (EntityManager em = emf.createEntityManager()) {
            Trip trip = em.find(Trip.class, tripId);
            Guide guide = em.find(Guide.class, guideId);
            if(trip == null) {
                throw new EntityNotFoundException("Trip not found with id: " + tripId);
            } else if(guide == null) {
                throw new EntityNotFoundException("Guide not found with id: " + guideId);
            }
            em.getTransaction().begin();
            trip.addGuide(guide);
            em.getTransaction().commit();
            return new TripDTO(trip);
        }
    }

    @Override
    public Set<TripDTO> getTripsByGuide(int guideId) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<TripDTO> query = em.createQuery("SELECT new dk.cphbusiness.dtos.TripDTO(t) FROM Trip t WHERE t.guide.id = :id", TripDTO.class);
            query.setParameter("id", guideId);
            Set<TripDTO> result = query.getResultStream().collect(Collectors.toSet());
            return result;
        }
    }

    @Override
    public Set<RatingDTO> getAllRatings() {
        try(EntityManager em = emf.createEntityManager()) {
            TypedQuery<RatingDTO> query = em.createQuery("SELECT new dk.cphbusiness.dtos.RatingDTO(r) FROM Rating r", RatingDTO.class);
            Set<RatingDTO> result = query.getResultStream().collect(Collectors.toSet());
            return result;
        }
    }

    @Override
    public Set<RatingDTO> getRatingsByParticipant(ParticipantDTO participant) {
        try(EntityManager em = emf.createEntityManager()) {
            TypedQuery<RatingDTO> query = em.createQuery("SELECT new dk.cphbusiness.dtos.RatingDTO(r) FROM Rating r WHERE r.participant.username = :username", RatingDTO.class);
            query.setParameter("username", participant.getUsername());
            Set<RatingDTO> result = query.getResultStream().collect(Collectors.toSet());
            return result;
        }
    }

    @Override
    public Set<RatingDTO> getRatingsByTrip(TripDTO trip) {
        try(EntityManager em = emf.createEntityManager()) {
            TypedQuery<RatingDTO> query = em.createQuery("SELECT new dk.cphbusiness.dtos.RatingDTO(r) FROM Rating r WHERE r.trip.id = :id", RatingDTO.class);
            query.setParameter("id", trip.getId());
            Set<RatingDTO> result = query.getResultStream().collect(Collectors.toSet());
            return result;
        }
    }

    @Override
    public RatingDTO getRatingById(Long id) {
            try(EntityManager em = emf.createEntityManager()) {
                Rating rating = em.find(Rating.class, id);
                if(rating == null) {
                    throw new EntityNotFoundException("Rating not found with id: " + id);
                }
                return new RatingDTO(rating);
        }
    }

    @Override
    public RatingDTO createRating(RatingDTO rating) throws IllegalArgumentException {
        try(EntityManager em = emf.createEntityManager()) {
            Participant participant = em.find(Participant.class, rating.getParticipant().getUsername());
            Trip trip = em.find(Trip.class, rating.getTrip().getId());
            if(participant != null && trip != null) {
                throw new IllegalArgumentException("A rating of that trip by that participant already exists");
            }
            em.getTransaction().begin();
            em.persist(new Rating(participant, trip, rating.getRatingScore(), rating.getComment()));
            em.getTransaction().commit();
            return new RatingDTO();
        }
    }

    @Override
    public RatingDTO updateRating(RatingDTO rating) {
        try(EntityManager em = emf.createEntityManager()) {
            Rating found = em.find(Rating.class, rating.getId());
            if(found == null) {
                throw new EntityNotFoundException("Rating not found with id: " + rating.getId());
            }
            em.getTransaction().begin();
            found.setRatingScore(rating.getRatingScore());
            found.setComment(rating.getComment());
            em.getTransaction().commit();
            return new RatingDTO(found);
        }
    }

    @Override
    public RatingDTO deleteRating(Long id) {
        try(EntityManager em = emf.createEntityManager()) {
            Rating found = em.find(Rating.class, id);
            if(found == null) {
                throw new EntityNotFoundException("Rating not found with id: " + id);
            }
            em.getTransaction().begin();
            em.remove(found);
            em.getTransaction().commit();
            return new RatingDTO(found);
        }
    }
}
