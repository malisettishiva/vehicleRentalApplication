package com.pavan.vehiclerental.strategy;

import com.pavan.vehiclerental.model.VehicleAvailability;

import java.util.Comparator;

public class PricingComparator implements Comparator<VehicleAvailability> {

    @Override
    public int compare(VehicleAvailability v1, VehicleAvailability v2) {
        if (v1.getPrice() > v2.getPrice()) {
            return 1;
        } else if (v1.getPrice() < v2.getPrice()) {
            return -1;
        }

        return 0;
    }
}
