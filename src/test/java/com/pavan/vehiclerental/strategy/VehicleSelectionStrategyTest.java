package com.pavan.vehiclerental.strategy;

import com.pavan.vehiclerental.enums.VehicleStatus;
import com.pavan.vehiclerental.model.VehicleAvailability;
import com.pavan.vehiclerental.model.VehicleSelectionStrategyResponse;
import com.pavan.vehiclerental.service.SlotsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class VehicleSelectionStrategyTest {

    private SlotsService slotsServiceMock;
    private VehicleSelectionStrategy vehicleSelectionStrategyMock;

    @Before
    public void setUp() {
        this.slotsServiceMock = mock(SlotsService.class);
        this.vehicleSelectionStrategyMock = new DefaultVehicleSelectionStrategy(slotsServiceMock);
    }

    @Test
    public void testVehicleSelection() {

        final List<VehicleAvailability> vehicleAvailabilities = new ArrayList<>();
        vehicleAvailabilities.add(VehicleAvailability.builder()
                .id("V1")
                .price(500.0)
                .status(VehicleStatus.AVAILABLE)
                .build());
        vehicleAvailabilities.add(VehicleAvailability.builder()
                .id("V2")
                .price(200.0)
                .status(VehicleStatus.AVAILABLE)
                .build());

        when(slotsServiceMock.fetchVehicles("B1", "CAR", 1, 5, VehicleStatus.AVAILABLE))
                .thenReturn(vehicleAvailabilities);

        assertEquals(VehicleSelectionStrategyResponse.builder()
                .selectedVehicles(List.of("V2"))
                .totalAmount(800.0)
                .build(), vehicleSelectionStrategyMock.selectVehicle("B1", "CAR", 1, 5));

    }
}
