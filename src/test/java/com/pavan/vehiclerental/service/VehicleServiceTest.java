package com.pavan.vehiclerental.service;

import com.pavan.vehiclerental.enums.VehicleStatus;
import com.pavan.vehiclerental.enums.VehicleType;
import com.pavan.vehiclerental.factory.VehicleOnboardingFactory;
import com.pavan.vehiclerental.model.Branch;
import com.pavan.vehiclerental.model.Car;
import com.pavan.vehiclerental.model.Slot;
import com.pavan.vehiclerental.model.VehicleAvailability;
import com.pavan.vehiclerental.store.VehicleManager;
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
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class VehicleServiceTest {

    private VehicleOnboardingFactory vehicleOnboardingFactoryMock;
    private VehicleManager vehicleManagerMock;
    private VehicleService vehicleServiceMock;
    private SlotsService slotsServiceMock;

    @Before
    public void setUp() {
        this.vehicleManagerMock = mock(VehicleManager.class);
        this.slotsServiceMock = mock(SlotsService.class);
        this.vehicleOnboardingFactoryMock = mock(VehicleOnboardingFactory.class);
        this.vehicleServiceMock = new VehicleService(vehicleManagerMock, slotsServiceMock, vehicleOnboardingFactoryMock);
    }

    @Test
    public void testSlotServiceFetchVehiclesInvocation() {
        final Branch branch = Branch.builder()
                .id("B1")
                .name("B1")
                .vehicleTypes(List.of(VehicleType.fromString("CAR"), VehicleType.fromString("VAN")))
                .vehicleIds(List.of("V1"))
                .build();

        vehicleServiceMock.getAllVehicles(branch, 1, 5,
                PageRequest.of(0, 1000, Sort.Direction.ASC, "price"), VehicleStatus.AVAILABLE);

        verify(slotsServiceMock, times(1))
                .fetchVehicles("B1", "CAR", 1, 5, VehicleStatus.AVAILABLE);
        verify(slotsServiceMock, times(1))
                .fetchVehicles("B1", "VAN", 1, 5, VehicleStatus.AVAILABLE);
    }

    @Test
    public void testSlotServiceVehicleManagerInvocation() {

        when(slotsServiceMock.fetchVehicles("B1", "CAR", 1, 5, VehicleStatus.AVAILABLE))
                .thenReturn(List.of(VehicleAvailability.builder()
                        .id("V1")
                        .price(500.0)
                        .status(VehicleStatus.AVAILABLE)
                        .build()));

        final Branch branch = Branch.builder()
                .id("B1")
                .name("B1")
                .vehicleTypes(List.of(VehicleType.fromString("CAR"), VehicleType.fromString("VAN")))
                .vehicleIds(List.of("V1"))
                .build();

        vehicleServiceMock.getAllVehicles(branch, 1, 5,
                PageRequest.of(0, 1000, Sort.Direction.ASC, "price"), VehicleStatus.AVAILABLE);

        verify(vehicleManagerMock, times(1))
                .findById("V1");
    }

    @Test
    public void testVehicleCreation() {

        final Car car = new Car("V1", 500.0);

        when(vehicleOnboardingFactoryMock.createInstance("V1", "CAR", 500.0))
                .thenReturn(car);

        vehicleServiceMock.addVehicle("B1", "CAR", "V1", 500.0);

        verify(vehicleManagerMock, times(1))
                .save(car);
    }

    @Test
    public void testVehicleCreationSlotsAddition() {

        final Car car = new Car("V1", 500.0);

        when(vehicleOnboardingFactoryMock.createInstance("V1", "CAR", 500.0))
                .thenReturn(car);

        final List<Slot> filteredSlots = new ArrayList<>();
        for (int i = DAY_START; i < DAY_END; i++) {
            final Slot slot = Slot.builder()
                    .branchId("B1")
                    .vehicleType("CAR")
                    .startTime(i)
                    .endTime(i + 1)
                    .vehicles(List.of(
                            VehicleAvailability.builder()
                                    .id("V1")
                                    .status(VehicleStatus.AVAILABLE)
                                    .price(500.0)
                                    .build()
                    ))
                    .build();
            filteredSlots.add(slot);
        }

        when(slotsServiceMock.fetchSlots("B1", "CAR", DAY_START, DAY_END))
                .thenReturn(filteredSlots);

        vehicleServiceMock.addVehicle("B1", "CAR", "V1", 500.0);

        verify(slotsServiceMock, times(1))
                .fetchSlots("B1", "CAR", DAY_START, DAY_END);

        verify(slotsServiceMock, times(1))
                .addVehicleAvailability(filteredSlots, List.of(
                        VehicleAvailability.builder()
                                .id("V1")
                                .price(500.0)
                                .status(VehicleStatus.AVAILABLE)
                                .build()
                ));
    }
}
