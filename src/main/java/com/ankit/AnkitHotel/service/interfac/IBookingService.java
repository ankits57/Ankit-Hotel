package com.ankit.AnkitHotel.service.interfac;

import com.ankit.AnkitHotel.dto.Response;
import com.ankit.AnkitHotel.entity.Booking;

public interface IBookingService {
    Response saveBooking(Long roomId, Long userId, Booking bookingRequest);
    Response findBookingByConfirmationCode(String confirmationCode);
    Response getAllBookings();
    Response cancelBooking(Long bookingId);

}
