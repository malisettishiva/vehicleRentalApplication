package com.pavan.vehiclerental.service;

import com.pavan.vehiclerental.exception.BranchAlreadyExistsException;
import com.pavan.vehiclerental.exception.BranchNotFoundException;
import com.pavan.vehiclerental.exception.VehicleAlreadyExistsException;
import com.pavan.vehiclerental.exception.VehicleTypeNotFoundException;
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

    @BeforeEach
    void setUp(){
        final BranchManager branchManager = new BranchManager();
        final VehicleManager vehicleManager = new VehicleManager();
        final SlotsManager slotsManager = new SlotsManager();
        final BookingManager bookingManager = new BookingManager();

        final VehicleSelectionStrategy vehicleSelectionStrategy = new DefaultVehicleSelectionStrategy(
                slotsManager, vehicleManager);

        vehicleRentalService = new VehicleRentalService(branchManager, vehicleManager, slotsManager,
                bookingManager, vehicleSelectionStrategy);
    }

    @Test
    void testOnboardBranch(){

        assertTrue(vehicleRentalService.getBranchService().addBranch("B1", List.of("CAR", "BIKE")));

        assertThrows(NullPointerException.class, () -> {
            vehicleRentalService.getBranchService().addBranch("B2", null);
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

        // TODO : Unpointers check

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

        vehicleRentalService.getBookingService()
                .bookVehicle("B1", "VAN", 1, 5);
        vehicleRentalService.getBookingService()
                .bookVehicle("B1", "CAR", 1, 3);
        vehicleRentalService.getBookingService()
                .bookVehicle("B1", "BIKE", 2, 3);
        vehicleRentalService.getBookingService()
                .bookVehicle("B1", "BIKE", 2, 5);

        assertEquals(List.of("V2"), vehicleRentalService.getVehicleService().getAllAvailableVehicles(
                "B1", 1, 5, PageRequest.of(0, 1000, Sort.Direction.ASC, "price"))
                .stream().map(Vehicle::getId).toList()
        );

        vehicleRentalService.getBookingService()
                .bookVehicle("B1", "CAR", 1, 5);

        assertEquals(new ArrayList<>(), vehicleRentalService.getVehicleService().getAllAvailableVehicles(
                        "B1", 1, 5, PageRequest.of(0, 1000, Sort.Direction.ASC, "price"))
                .stream().map(Vehicle::getId).toList()
        );

    }
}
