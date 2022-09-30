package com.pavan.vehiclerental.service;

import com.pavan.vehiclerental.enums.VehicleStatus;
import com.pavan.vehiclerental.exception.*;
import com.pavan.vehiclerental.model.Vehicle;
import com.pavan.vehiclerental.store.BookingManager;
import com.pavan.vehiclerental.store.BranchManager;
import com.pavan.vehiclerental.store.SlotsManager;
import com.pavan.vehiclerental.store.VehicleManager;
import com.pavan.vehiclerental.strategy.DefaultVehicleSelectionStrategy;
import com.pavan.vehiclerental.strategy.VehicleSelectionStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class VehicleRentalServiceTest {

    private VehicleRentalService vehicleRentalService;

    final BranchManager branchManager = BranchManager.getInstance();
    final VehicleManager vehicleManager = VehicleManager.getInstance();
    final SlotsManager slotsManager = SlotsManager.getInstance();
    final BookingManager bookingManager = BookingManager.getInstance();

    @SuppressWarnings("null")
    public static <T> T giveNull() {
        return null;
    }

    @BeforeEach
    void setUp(){

        branchManager.eraseAll();
        vehicleManager.eraseAll();
        slotsManager.eraseAll();
        bookingManager.eraseAll();

        final VehicleSelectionStrategy vehicleSelectionStrategy = new DefaultVehicleSelectionStrategy(
                slotsManager, vehicleManager);

        this.vehicleRentalService = new VehicleRentalService(branchManager, vehicleManager, slotsManager,
                bookingManager, vehicleSelectionStrategy);
    }

    @Test
    void testOnboardBranch(){

        assertTrue(vehicleRentalService.getBranchService().addBranch("B1", List.of("CAR", "BIKE")));

        assertThrows(NullPointerException.class, () -> {
            vehicleRentalService.getBranchService().addBranch(giveNull(), List.of("CAR", "BIKE"));
        });

        assertThrows(NullPointerException.class, () -> {
            vehicleRentalService.getBranchService().addBranch("B2", giveNull());
        });

        // When adding a vehicle unsupported by branch
        assertThrows(VehicleTypeNotFoundException.class, () -> {
            vehicleRentalService.getBranchService().addBranch("B2", List.of("SHIP"));
        });

        // When adding a branch which already exists
        assertThrows(BranchAlreadyExistsException.class, () -> {
            vehicleRentalService.getBranchService().addBranch("B1", List.of("CAR", "VAN"));
        });

    }

    @Test
    void testOnboardVehicle(){

        assertTrue(vehicleRentalService.getBranchService().addBranch("B1", List.of("CAR", "BIKE")));

        assertThrows(NullPointerException.class, () -> {
            vehicleRentalService.getVehicleService()
                    .addVehicle(giveNull(), "CAR", "C1", 200.0);
        });

        assertThrows(NullPointerException.class, () -> {
            vehicleRentalService.getVehicleService()
                    .addVehicle("B1", giveNull(), "C1", 200.0);
        });

        assertThrows(NullPointerException.class, () -> {
            vehicleRentalService.getVehicleService()
                    .addVehicle("B1", "CAR", giveNull(), 200.0);
        });

        assertThrows(NullPointerException.class, () -> {
            vehicleRentalService.getVehicleService()
                    .addVehicle("B1", "CAR", "C1", giveNull());
        });

        assertTrue(vehicleRentalService.getVehicleService()
                .addVehicle("B1", "CAR", "C1", 100.0));

        // When adding a vehicle unsupported by branch
        assertFalse(vehicleRentalService.getVehicleService()
                .addVehicle("B1", "VAN", "C1", 100.0));

        // When adding a vehicle of unsupported type
        assertThrows(VehicleTypeNotFoundException.class, () -> {
            vehicleRentalService.getVehicleService()
                    .addVehicle("B1", "SHIP", "C1", 100.0);
        });

        // Adding a vehicle to non-existent branch
        assertThrows(BranchNotFoundException.class, () -> {
            vehicleRentalService.getVehicleService()
                    .addVehicle("B2", "CAR", "C1", 100.0);
        });

        // Adding a vehicle which already exists
        assertThrows(VehicleAlreadyExistsException.class, () -> {
            vehicleRentalService.getVehicleService()
                    .addVehicle("B1", "CAR", "C1", 200.0);
        });

    }

    @Test
    void testBookingVehicle(){
        vehicleRentalService.getBranchService().addBranch("B1", List.of("CAR", "BIKE", "VAN"));
        vehicleRentalService.getVehicleService().addVehicle("B1", "CAR", "V1", 500.0);
        vehicleRentalService.getVehicleService().addVehicle("B1", "CAR", "V2", 1000.0);
        vehicleRentalService.getVehicleService().addVehicle("B1", "BIKE", "V3", 250.0);
        vehicleRentalService.getVehicleService().addVehicle("B1", "BIKE", "V4", 300.0);

        assertThrows(NullPointerException.class, () -> {
            vehicleRentalService.getBookingService()
                    .bookVehicle(giveNull(), "VAN", 1, 5);
        });

        assertThrows(NullPointerException.class, () -> {
            vehicleRentalService.getBookingService()
                    .bookVehicle("B1", giveNull(), 1, 5);
        });

        assertThrows(NullPointerException.class, () -> {
            vehicleRentalService.getBookingService()
                    .bookVehicle("B1", "VAN", giveNull(), 5);
        });

        assertThrows(NullPointerException.class, () -> {
            vehicleRentalService.getBookingService()
                    .bookVehicle("B1", "VAN", 1, giveNull());
        });

        assertThrows(InvalidSlotDurationException.class, () -> {
            vehicleRentalService.getBookingService()
                    .bookVehicle("B1", "VAN", 5, 1);
        });

        assertThrows(InvalidSlotDurationException.class, () -> {
            vehicleRentalService.getBookingService()
                    .bookVehicle("B1", "VAN", 1, 1);
        });

        assertEquals(-1.0, vehicleRentalService.getBookingService()
                .bookVehicle("B1", "VAN", 1, 5));
        assertEquals(1000.0, vehicleRentalService.getBookingService()
                .bookVehicle("B1", "CAR", 1, 3));
        assertEquals(250.0, vehicleRentalService.getBookingService()
                .bookVehicle("B1", "BIKE", 2, 3));
        assertEquals(900.0, vehicleRentalService.getBookingService()
                .bookVehicle("B1", "BIKE", 2, 5));

        assertThrows(BranchNotFoundException.class, () -> {
            vehicleRentalService.getBookingService().bookVehicle("B2", "VAN", 1, 5);
        });

        assertThrows(VehicleTypeNotFoundException.class, () -> {
            vehicleRentalService.getBookingService().bookVehicle("B1", "SHIP", 1, 5);
        });

    }

    @Test
    void testFetchAvailableVehicles(){
        vehicleRentalService.getBranchService().addBranch("B1", List.of("CAR", "BIKE", "VAN"));
        vehicleRentalService.getVehicleService().addVehicle("B1", "CAR", "V1", 500.0);
        vehicleRentalService.getVehicleService().addVehicle("B1", "CAR", "V2", 1000.0);
        vehicleRentalService.getVehicleService().addVehicle("B1", "BIKE", "V3", 250.0);
        vehicleRentalService.getVehicleService().addVehicle("B1", "BIKE", "V4", 300.0);

        assertThrows(NullPointerException.class, ()->{
            vehicleRentalService.getVehicleService()
                    .getAllVehicles(giveNull(), 1, 5,
                            PageRequest.of(0, 1000, Sort.Direction.ASC, "price"),
                            VehicleStatus.AVAILABLE);
        });

        assertThrows(NullPointerException.class, ()->{
            vehicleRentalService.getVehicleService()
                    .getAllVehicles("B1", giveNull(), 5,
                            PageRequest.of(0, 1000, Sort.Direction.ASC, "price"),
                            VehicleStatus.AVAILABLE);
        });

        assertThrows(NullPointerException.class, ()->{
            vehicleRentalService.getVehicleService()
                    .getAllVehicles("B1", 1, giveNull(),
                            PageRequest.of(0, 1000, Sort.Direction.ASC, "price"),
                            VehicleStatus.AVAILABLE);
        });

        assertThrows(NullPointerException.class, ()->{
            vehicleRentalService.getVehicleService()
                    .getAllVehicles("B1", 1, 5,
                            giveNull(),
                            VehicleStatus.AVAILABLE);
        });

        assertThrows(NullPointerException.class, ()->{
            vehicleRentalService.getVehicleService()
                    .getAllVehicles("B1", 1, 5,
                            PageRequest.of(0, 1000, Sort.Direction.ASC, "price"),
                            giveNull());
        });

        assertThrows(InvalidSlotDurationException.class, () -> {
            vehicleRentalService.getBookingService()
                    .bookVehicle("B1", "VAN", 5, 1);
        });

        assertThrows(InvalidSlotDurationException.class, () -> {
            vehicleRentalService.getBookingService()
                    .bookVehicle("B1", "VAN", 1, 1);
        });

        vehicleRentalService.getBookingService()
                .bookVehicle("B1", "VAN", 1, 5);
        vehicleRentalService.getBookingService()
                .bookVehicle("B1", "CAR", 1, 3);
        vehicleRentalService.getBookingService()
                .bookVehicle("B1", "BIKE", 2, 3);
        vehicleRentalService.getBookingService()
                .bookVehicle("B1", "BIKE", 2, 5);

        assertThrows(InvalidSlotDurationException.class, () -> {
            vehicleRentalService.getVehicleService()
                    .getAllVehicles("B1", 5, 1,
                            PageRequest.of(0, 1000, Sort.Direction.ASC, "price"),
                            VehicleStatus.AVAILABLE);
        });

        assertThrows(InvalidSlotDurationException.class, () -> {
            vehicleRentalService.getVehicleService()
                    .getAllVehicles("B1", 1, 1,
                            PageRequest.of(0, 1000, Sort.Direction.ASC, "price"),
                            VehicleStatus.AVAILABLE);
        });

        assertEquals(List.of("V2"), vehicleRentalService.getVehicleService()
                .getAllVehicles("B1", 1, 5,
                        PageRequest.of(0, 1000, Sort.Direction.ASC, "price"),
                        VehicleStatus.AVAILABLE)
                .stream().map(Vehicle::getId).toList()
        );

        vehicleRentalService.getBookingService()
                .bookVehicle("B1", "CAR", 1, 5);

        assertEquals(new ArrayList<>(), vehicleRentalService.getVehicleService()
                .getAllVehicles(
                        "B1", 1, 5,
                        PageRequest.of(0, 1000, Sort.Direction.ASC, "price"),
                        VehicleStatus.AVAILABLE)
                .stream().map(Vehicle::getId).toList()
        );

    }
}
