package com.pavan.vehiclerental.model;

import com.pavan.vehiclerental.enums.VehicleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleAvailability {
    private String id;
    private Double price;
    private VehicleStatus status;
}
