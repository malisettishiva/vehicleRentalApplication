package com.pavan.vehiclerental.store;

import com.pavan.vehiclerental.exception.BookingAlreadyExistsException;
import com.pavan.vehiclerental.exception.BookingNotFoundException;
import com.pavan.vehiclerental.model.Booking;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingManager implements StoreRepository<Booking, String> {
    private final Map<String, Booking> bookings;

    public BookingManager() {
        this.bookings = new HashMap<>();
    }

    @Override
    public List<Booking> findAll() {
        return this.bookings.values().stream().toList();
    }

    @Override
    public Booking findById(String bookingId) {
        if (!this.bookings.containsKey(bookingId)) {
            throw new BookingNotFoundException();
        }

        return this.bookings.get(bookingId);
    }

    @Override
    public void save(Booking booking) {
        if (this.bookings.containsKey(booking.getId())) {
            throw new BookingAlreadyExistsException();
        }

        this.bookings.put(booking.getId(), booking);
    }

    @Override
    public Booking update(Booking booking) {
        if (!this.bookings.containsKey(booking.getId())) {
            throw new BookingNotFoundException();
        }

        this.bookings.put(booking.getId(), booking);
        return booking;
    }

    @Override
    public void delete(String bookingId) {
        if (!this.bookings.containsKey(bookingId)) {
            throw new BookingNotFoundException();
        }

        this.bookings.remove(bookingId);
    }
}
