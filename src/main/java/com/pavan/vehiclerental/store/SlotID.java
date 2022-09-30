package com.pavan.vehiclerental.store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SlotID {
    private String branchId;
    private String vehicleType;
    private Integer startTime;
    private Integer endTime;
}
