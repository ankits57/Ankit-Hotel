package com.ankit.AnkitHotel.service.impl;

import com.ankit.AnkitHotel.dto.BookingDTO;
import com.ankit.AnkitHotel.dto.Response;
import com.ankit.AnkitHotel.entity.Booking;
import com.ankit.AnkitHotel.entity.Room;
import com.ankit.AnkitHotel.entity.User;
import com.ankit.AnkitHotel.exception.OurException;
import com.ankit.AnkitHotel.repository.BookingRepository;
import com.ankit.AnkitHotel.repository.RoomRepository;
import com.ankit.AnkitHotel.repository.UserRepository;
import com.ankit.AnkitHotel.service.interfac.IBookingService;
import com.ankit.AnkitHotel.service.interfac.IRoomService;
import com.ankit.AnkitHotel.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService implements IBookingService {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private IRoomService roomService;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public Response saveBooking(Long roomId, Long userId, Booking bookingRequest) {
        Response response = new Response();
        try{
            if(bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())){
                throw new IllegalArgumentException("Check in date must come after check out date");
            }
            Room room = roomRepository.findById(roomId).orElseThrow(()->new OurException("Room not Found"));
            User user = userRepository.findById(userId).orElseThrow(()->new OurException("User not found"));
            List<Booking> existingBookings = room.getBookings();

            if(!roomisAvailable(bookingRequest, existingBookings)){
                throw new OurException("Room Not Available for selected Date range");
            }
            bookingRequest.setRoom(room);
            bookingRequest.setUser(user);
            String bookingConfirmationCode = Utils.generateRandomConfirmationCode(10);
            bookingRequest.setBookingConfirmationCode(bookingConfirmationCode);
            bookingRepository.save(bookingRequest);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setBookingConfirmationCode(bookingConfirmationCode);
        }catch(OurException e){
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        }catch(Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error receiving the booking "+e.getMessage());
        }
        return response;
    }

    @Override
    public Response findBookingByConfirmationCode(String confirmationCode) {
        Response response = new Response();
        try{
            Booking booking = bookingRepository.findByBookingConfirmationCode(confirmationCode).orElseThrow(()->new OurException("Booking not found"));
            BookingDTO bookingDTO = Utils.mapBookingEntityToBookingDTOPlusBookedRoom(booking, true);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setBooking(bookingDTO);
        }catch(OurException e){
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        }catch(Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error receiving the booking "+e.getMessage());
        }
        return response;
    }

    @Override
    public Response getAllBookings() {
        Response response = new Response();
        try{
            List<Booking> bookingList = bookingRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
            List<BookingDTO> bookingDTOList = Utils.mapBookingListEntityToBookingListDTO(bookingList);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setBookingList(bookingDTOList);
        }catch(OurException e){
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        }catch(Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error finding a booking "+e.getMessage());
        }
        return response;
    }

    @Override
    public Response cancelBooking(Long bookingId) {
        Response response = new Response();
        try{
            Booking bookings = bookingRepository.findById(bookingId).orElseThrow(()->new OurException("Booking not found"));
            bookingRepository.deleteById(bookingId);
            response.setStatusCode(200);
            response.setMessage("successful");
        }catch(OurException e){
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        }catch(Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Cancelling the booking "+e.getMessage());
        }
        return response;
    }

    private boolean roomisAvailable(Booking bookingRequest, List<Booking> existingBookings) {
        return existingBookings.stream()
                .noneMatch(existingBooking ->
                        bookingRequest.getCheckInDate().equals(existingBooking.getCheckInDate())
                                ||bookingRequest.getCheckOutDate().isBefore(existingBooking.getCheckOutDate())
                                ||(bookingRequest.getCheckInDate().isAfter(existingBooking.getCheckInDate())
                                && bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckOutDate()))
                                ||(bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckOutDate()))
                                ||(bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckOutDate()))

                                ||(bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckInDate()))

                                ||(bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(bookingRequest.getCheckInDate()))

                );
    }
}
