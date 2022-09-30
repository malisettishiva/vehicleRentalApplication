package com.pavan.vehiclerental.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Slot {
    private String branchId;
    private String vehicleType;
    private Integer startTime;
    private Integer endTime;
    private Integer availableVehiclesCnt;
    private List<VehicleAvailability> vehicles;
}
