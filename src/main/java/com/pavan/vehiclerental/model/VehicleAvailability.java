package com.pavan.vehiclerental.model;

import com.pavan.vehiclerental.enums.VehicleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleAvailability {
    private String id;
    private VehicleStatus status;

    public static List<String> filterVehicles(final List<VehicleAvailability> vehicles,
                                              final VehicleStatus vehicleStatus) {
        final List<VehicleAvailability> filteredVehicles = vehicles.stream()
                .filter(vehicle -> vehicle.getStatus().equals(vehicleStatus))
                .toList();

        return filteredVehicles.stream().map(VehicleAvailability::getId).toList();
    }
}
