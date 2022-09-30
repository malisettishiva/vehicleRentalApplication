package com.pavan.vehiclerental.model;

import com.pavan.vehiclerental.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    private String id;
    private String branchId;
    private List<String> vehicleIds;
    private Double price;
    private BookingStatus status;
}
