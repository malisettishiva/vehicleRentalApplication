package com.pavan.vehiclerental.service;

import com.pavan.vehiclerental.enums.VehicleStatus;
import com.pavan.vehiclerental.enums.VehicleType;
import com.pavan.vehiclerental.model.Branch;
import com.pavan.vehiclerental.store.BranchManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static com.pavan.vehiclerental.constants.SlotIntervalConstants.DAY_END;
import static com.pavan.vehiclerental.constants.SlotIntervalConstants.DAY_START;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class VehicleRentalServiceTest {

    private VehicleRentalService vehicleRentalServiceMock;
    private SlotsService slotsServiceMock;
    private BranchManager branchManagerMock;
    private VehicleService vehicleServiceMock;
    private BookingService bookingServiceMock;

    @Before
    public void setUp() {
        this.vehicleServiceMock = mock(VehicleService.class);
        this.bookingServiceMock = mock(BookingService.class);
        this.slotsServiceMock = mock(SlotsService.class);
        this.branchManagerMock = mock(BranchManager.class);

        this.vehicleRentalServiceMock = new VehicleRentalService(branchManagerMock, vehicleServiceMock, bookingServiceMock,
                slotsServiceMock);
    }

    @Test
    public void testOnboardBranch() {
        vehicleRentalServiceMock.onboardBranch("B1", List.of("CAR"));

        verify(slotsServiceMock, times(1))
                .onboardSlots("B1", List.of("CAR"), DAY_START, DAY_END);

        verify(branchManagerMock, times(1))
                .save(Branch.builder()
                        .id("B1")
                        .name("B1")
                        .vehicleTypes(List.of(VehicleType.CAR))
                        .vehicleIds(new ArrayList<>())
                        .build());
    }

    @Test
    public void testOnboardVehicle() {

        final List<String> vehicleIds = new ArrayList<>();
        vehicleIds.add("V1");
        when(branchManagerMock.findById("B1"))
                .thenReturn(Branch.builder()
                        .id("B1")
                        .name("B1")
                        .vehicleTypes(List.of(VehicleType.CAR))
                        .vehicleIds(vehicleIds)
                        .build());

        when(vehicleServiceMock.addVehicle("B1", "CAR", "V2", 500.0))
                .thenReturn(true);

        vehicleRentalServiceMock.onboardVehicle("B1", "CAR", "V2", 500.0);

        verify(vehicleServiceMock, times(1))
                .addVehicle("B1", "CAR", "V2", 500.0);

        verify(branchManagerMock, times(1))
                .update(Branch.builder()
                        .id("B1")
                        .name("B1")
                        .vehicleTypes(List.of(VehicleType.CAR))
                        .vehicleIds(List.of("V1", "V2"))
                        .build());

        when(vehicleServiceMock.addVehicle("B1", "CAR", "V2", 500.0))
                .thenReturn(false);

        assertEquals(false, vehicleRentalServiceMock.onboardVehicle("B1", "CAR", "V2", 500.0));
    }

    @Test
    public void testVehicleBooking() {
        final List<String> vehicleIds = new ArrayList<>();
        vehicleIds.add("V1");
        when(branchManagerMock.findById("B1"))
                .thenReturn(Branch.builder()
                        .id("B1")
                        .name("B1")
                        .vehicleTypes(List.of(VehicleType.CAR))
                        .vehicleIds(vehicleIds)
                        .build());

        vehicleRentalServiceMock.bookVehicle("B1", "CAR", 1, 5);

        verify(branchManagerMock, times(1))
                .findById("B1");

        verify(bookingServiceMock, times(1))
                .bookVehicle("B1", "CAR", 1, 5);
    }

    @Test
    public void testGetVehicles() {
        final List<String> vehicleIds = new ArrayList<>();
        vehicleIds.add("V1");
        final Branch branch = Branch.builder()
                .id("B1")
                .name("B1")
                .vehicleTypes(List.of(VehicleType.CAR))
                .vehicleIds(vehicleIds)
                .build();

        when(branchManagerMock.findById("B1"))
                .thenReturn(branch);

        vehicleRentalServiceMock.getAllVehicles("B1", 1, 5,
                PageRequest.of(0, 1000, Sort.Direction.ASC, "price"),
                VehicleStatus.AVAILABLE);

        verify(branchManagerMock, times(1))
                .findById("B1");

        verify(vehicleServiceMock, times(1))
                .getAllVehicles(branch, 1, 5,
                        PageRequest.of(0, 1000, Sort.Direction.ASC, "price"),
                        VehicleStatus.AVAILABLE);
    }

}
