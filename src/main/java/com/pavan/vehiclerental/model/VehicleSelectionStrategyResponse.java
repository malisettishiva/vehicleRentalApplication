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
public class VehicleSelectionStrategyResponse {
    private List<String> selectedVehicles;
    private Integer totalVehicles;
    private Integer totalAvailableVehicles;
    private Integer totalBookedVehicles;
    private Double totalAmount;
}
