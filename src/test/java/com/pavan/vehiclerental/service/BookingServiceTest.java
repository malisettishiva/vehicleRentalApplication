package com.pavan.vehiclerental.service;

import com.pavan.vehiclerental.enums.VehicleStatus;
import com.pavan.vehiclerental.model.Slot;
import com.pavan.vehiclerental.model.VehicleAvailability;
import com.pavan.vehiclerental.model.VehicleSelectionStrategyResponse;
import com.pavan.vehiclerental.store.BookingManager;
import com.pavan.vehiclerental.strategy.PricingStrategy;
import com.pavan.vehiclerental.strategy.VehicleSelectionStrategy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BookingServiceTest {

    private BookingManager bookingManagerMock;
    private PricingStrategy pricingStrategyMock;
    private VehicleSelectionStrategy vehicleSelectionStrategyMock;
    private SlotsService slotsServiceMock;
    private BookingService bookingServiceMock;

    @Before
    public void setUp() {
        this.bookingManagerMock = mock(BookingManager.class);
        this.pricingStrategyMock = mock(PricingStrategy.class);
        this.vehicleSelectionStrategyMock = mock(VehicleSelectionStrategy.class);
        this.slotsServiceMock = mock(SlotsService.class);
        this.bookingServiceMock = new BookingService(bookingManagerMock, slotsServiceMock,
                vehicleSelectionStrategyMock, pricingStrategyMock);
    }

    @Test
    public void testVehicleSelectionStrategyInvocation() {

        when(vehicleSelectionStrategyMock.selectVehicle("B1", "CAR", 1, 5))
                .thenReturn(VehicleSelectionStrategyResponse.builder()
                        .selectedVehicles(List.of("V1"))
                        .totalAmount(2000.0)
                        .build());

        bookingServiceMock.bookVehicle("B1", "CAR", 1, 5);
        verify(vehicleSelectionStrategyMock,
                times(1)).selectVehicle("B1", "CAR", 1, 5);
    }

    @Test
    public void testFetchSlotsInvocation() {
        when(vehicleSelectionStrategyMock.selectVehicle("B1", "CAR", 1, 5))
                .thenReturn(VehicleSelectionStrategyResponse.builder()
                        .selectedVehicles(List.of("V1"))
                        .totalAmount(2000.0)
                        .build());

        bookingServiceMock.bookVehicle("B1", "CAR", 1, 5);
        verify(slotsServiceMock, times(1))
                .fetchSlots("B1", "CAR", 1, 5);
    }

    @Test
    public void testSlotsUpdateInvocation() {

        final List<Slot> filteredSlots = new ArrayList<>();
        for (int i = 1; i < 5; i++) {
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

        when(slotsServiceMock.fetchSlots("B1", "CAR", 1, 5))
                .thenReturn(filteredSlots);

        when(vehicleSelectionStrategyMock.selectVehicle("B1", "CAR", 1, 5))
                .thenReturn(VehicleSelectionStrategyResponse.builder()
                        .selectedVehicles(List.of("V1"))
                        .totalAmount(2000.0)
                        .build());

        bookingServiceMock.bookVehicle("B1", "CAR", 1, 5);
        verify(slotsServiceMock, times(1))
                .updateVehicleAvailability(filteredSlots, List.of("V1"), VehicleStatus.BOOKED);
    }

    @Test
    public void testPricingStrategyInvocation() {
        when(vehicleSelectionStrategyMock.selectVehicle("B1", "CAR", 1, 5))
                .thenReturn(VehicleSelectionStrategyResponse.builder()
                        .selectedVehicles(List.of("V1"))
                        .totalAmount(2000.0)
                        .build());

        bookingServiceMock.bookVehicle("B1", "CAR", 1, 5);
        verify(pricingStrategyMock, times(1))
                .getPrice(VehicleSelectionStrategyResponse.builder()
                        .selectedVehicles(List.of("V1"))
                        .totalAmount(2000.0)
                        .build());
    }
}
