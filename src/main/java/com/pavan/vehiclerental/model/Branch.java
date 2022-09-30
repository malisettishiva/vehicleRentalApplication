package com.pavan.vehiclerental.model;

import com.pavan.vehiclerental.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Branch {
    private String id;
    private String name;
    private Location location;
    private List<VehicleType> vehicleTypes;
    private List<String> vehicleIds;
}
