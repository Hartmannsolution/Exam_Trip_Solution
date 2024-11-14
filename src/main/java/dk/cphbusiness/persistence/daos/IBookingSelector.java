package dk.cphbusiness.persistence.daos;

import dk.cphbusiness.dtos.BookingDTO;

import java.util.Set;

public interface IBookingSelector {
    Set<BookingDTO> getBookingsByParticipant(String username);
    Set<BookingDTO> getBookingsByTrip(Long tripId);
}
