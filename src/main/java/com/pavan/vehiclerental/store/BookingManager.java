package com.pavan.vehiclerental.store;

import com.pavan.vehiclerental.exception.BookingAlreadyExistsException;
import com.pavan.vehiclerental.exception.BookingNotFoundException;
import com.pavan.vehiclerental.model.Booking;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BookingManager implements StoreRepository<Booking, String> {

    private static volatile BookingManager instance = null;

    private final Map<String, Booking> bookings;

    private BookingManager() {
        this.bookings = new HashMap<>();
    }

    public static BookingManager getInstance() {
        if (instance == null) {
            synchronized (BookingManager.class) {
                if (instance == null) {
                    instance = new BookingManager();
                }
            }
        }
        return instance;
    }

    @Override
    public List<Booking> findAll() {
        return this.bookings.values().stream().collect(Collectors.toList());
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

    @Override
    public void eraseAll() {
        this.bookings.clear();
    }
}
