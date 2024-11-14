package dk.cphbusiness.persistence.daos;

import dk.cphbusiness.dtos.BookingDTO;
import dk.cphbusiness.persistence.model.Booking;
import dk.cphbusiness.persistence.HibernateConfig;
import dk.cphbusiness.persistence.model.Participant;
import dk.cphbusiness.service.EntityMapper;
import jakarta.persistence.*;

import java.util.Set;
import java.util.stream.Collectors;

public class BookingDAO implements IDAO<BookingDTO>, IBookingSelector {
    private EntityManagerFactory emf;
    private static IDAO bookingDAO;
    private static IBookingSelector bookingSelector;

    protected BookingDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public static IDAO getBookingDAO(EntityManagerFactory _emf) {
        if (bookingDAO == null) {
            bookingDAO = new BookingDAO(_emf);
        }
        return bookingDAO;
    }

    public static IBookingSelector getBookingSelector(EntityManagerFactory _emf) {
        if (bookingSelector == null) {
            bookingSelector = new BookingDAO(_emf);
        }
        return bookingSelector;
    }

    @Override
    public BookingDTO getById(Object id) throws EntityNotFoundException {
        try (EntityManager em = HibernateConfig.getEntityManagerFactory().createEntityManager()) {
            Booking booking = em.find(Booking.class, id);
            if (booking == null) {
                throw new EntityNotFoundException("Booking with ID " + id + " not found");
            }
            return new BookingDTO(booking);
        }
    }

    @Override
    public Set<BookingDTO> getAll() {
        try (EntityManager em = HibernateConfig.getEntityManagerFactory().createEntityManager()) {
            Set<Booking> bookings = em.createQuery("SELECT b FROM Booking b", Booking.class)
                    .getResultStream()
                    .collect(Collectors.toSet());
            return bookings.stream().map(BookingDTO::new).collect(Collectors.toSet());
        }
    }

    @Override
    public BookingDTO create(BookingDTO bookingDTO) throws Exception {
        Booking booking = new EntityMapper(HibernateConfig.getEntityManagerFactory())
                .dtoToBooking(bookingDTO); // checks if trip and participant exists and creates Participant in DB if not already

        try (EntityManager em = HibernateConfig.getEntityManagerFactory().createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            Participant participant = booking.getParticipant();
            Long tripId = bookingDTO.getTrip().getId();
            String participantId = bookingDTO.getParticipant().getUsername();

            try {
                tx.begin();
                em.persist(booking);
                tx.commit();
                return new BookingDTO(booking);
            } catch (Exception e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                throw new Exception("Error saving Booking", e);
            }
        }
    }

    @Override
    public BookingDTO update(BookingDTO dto) throws EntityNotFoundException {
        try (EntityManager em = HibernateConfig.getEntityManagerFactory().createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            Booking booking = new EntityMapper(HibernateConfig.getEntityManagerFactory())
                    .dtoToBooking(dto);
            try {
                tx.begin();
                if (booking.getId() == null) {
                    throw new EntityNotFoundException("Booking with ID " + dto.getId() + " not found");
                }
                booking = em.merge(booking);
                tx.commit();
                return new BookingDTO(booking);
            } catch (IllegalArgumentException | TransactionRequiredException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                throw new RuntimeException("Error updating Booking", e);
            }
        }
    }

    @Override
    public void delete(BookingDTO dto) throws EntityNotFoundException {
        try (EntityManager em = HibernateConfig.getEntityManagerFactory().createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            tx.begin();
            Booking booking = em.find(Booking.class, dto.getId());
            if (booking == null) {
                throw new EntityNotFoundException("Booking with ID " + dto.getId() + " not found");
            }
            em.remove(booking);
            tx.commit();
        }
    }

    @Override
    public Set<BookingDTO> getBookingsByParticipant(String username) {
        try (EntityManager em = HibernateConfig.getEntityManagerFactory().createEntityManager()) {
            Set<Booking> bookings = em.createQuery("SELECT b FROM Booking b WHERE b.participant.username = :username", Booking.class)
                    .setParameter("username", username)
                    .getResultStream()
                    .collect(Collectors.toSet());
            return bookings.stream().map(BookingDTO::new).collect(Collectors.toSet());
        }
    }

    @Override
    public Set<BookingDTO> getBookingsByTrip(Long tripId) {
        try (EntityManager em = HibernateConfig.getEntityManagerFactory().createEntityManager()) {
            Set<Booking> bookings = em.createQuery("SELECT b FROM Booking b WHERE b.trip.id = :tripId", Booking.class)
                    .setParameter("tripId", tripId)
                    .getResultStream()
                    .collect(Collectors.toSet());
            return bookings.stream().map(BookingDTO::new).collect(Collectors.toSet());
        }
    }
}
