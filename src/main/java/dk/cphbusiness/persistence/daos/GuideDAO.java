package dk.cphbusiness.persistence.daos;

/**
 * Purpose:
 *
 * @author: Thomas Hartmann
 */
import dk.cphbusiness.dtos.GuideDTO;
import dk.cphbusiness.persistence.model.Guide; // Assuming there's a Guide entity class
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;

import javax.lang.model.UnknownEntityException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Purpose: Data Access Object for GuideDTO
 *
 * @author: Thomas Hartmann
 */
public class GuideDAO implements IDAO<GuideDTO> {
    private EntityManagerFactory emf;
    private static IDAO<GuideDTO> guideDAO;

    protected GuideDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public static IDAO<GuideDTO> getGuideDAO(EntityManagerFactory _emf) {
        if(guideDAO == null) {
            guideDAO = new GuideDAO(_emf);
        }
        return guideDAO;
    }

    // Getter is used in other DAOs to get emf from superclass
    public EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    // Queries
    @Override
    public GuideDTO getById(Object id) throws EntityNotFoundException {
        try (EntityManager em = emf.createEntityManager()) {
            Guide guide = em.find(Guide.class, id);
            if (guide == null) {
                throw new EntityNotFoundException("Unknown entity with id: " + id);
            }
            return new GuideDTO(guide);
        } catch (UnknownEntityException e) {
            throw new EntityNotFoundException("Unknown entity with id: " + id);
        }
    }

    @Override
    public Set<GuideDTO> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<GuideDTO> query = em.createQuery("SELECT new dk.cphbusiness.dtos.GuideDTO(g) FROM Guide g", GuideDTO.class);
            Set<GuideDTO> result = query.getResultStream().collect(Collectors.toSet());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public GuideDTO create(GuideDTO dto) throws Exception {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Guide guide = GuideDTO.toEntity(dto);
            em.persist(guide);
            em.getTransaction().commit();
            return new GuideDTO(guide);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error creating guide: " + e.getMessage());
        }
    }

    @Override
    public GuideDTO update(GuideDTO dto) throws jakarta.persistence.EntityNotFoundException {
        try (EntityManager em = emf.createEntityManager()) {
            Guide guide = GuideDTO.toEntity(dto);
            Guide found = em.find(Guide.class, guide.getId());
            if (found == null) {
                throw new jakarta.persistence.EntityNotFoundException("Guide not found with id: " + guide.getId());
            }
            em.getTransaction().begin();
            Guide merged = em.merge(guide);
            em.getTransaction().commit();
            return new GuideDTO(merged);
        }
    }

    @Override
    public void delete(GuideDTO dto) throws jakarta.persistence.EntityNotFoundException {
        try (EntityManager em = emf.createEntityManager()) {
            Guide guide = GuideDTO.toEntity(dto);
            Guide found = em.find(Guide.class, guide.getId());
            if (found == null) {
                throw new jakarta.persistence.EntityNotFoundException("Guide not found with id: " + guide.getId());
            }
            em.getTransaction().begin();
            em.remove(found);
            em.getTransaction().commit();
        }
    }
}

