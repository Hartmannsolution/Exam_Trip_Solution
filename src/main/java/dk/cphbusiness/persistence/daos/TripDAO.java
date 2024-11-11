package dk.cphbusiness.persistence.daos;

import dk.cphbusiness.dtos.TripDTO;
import dk.cphbusiness.persistence.model.Guide;
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
public class TripDAO implements IDAO<TripDTO>, ITripGuideDAO {
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
    public TripDTO findById(Object id) throws EntityNotFoundException {
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
}
