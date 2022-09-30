package com.pavan.vehiclerental.enums;

import com.pavan.vehiclerental.exception.VehicleTypeNotFoundException;

public enum VehicleType {
    CAR("CAR"),
    BIKE("BIKE"),
    VAN("VAN"),
    BUS("BUS");

    private String value;

    VehicleType(final String value){
        this.value = value;
    }

    private String getValue(){
        return this.value;
    }

    public static VehicleType fromString(final String value){
        for(final VehicleType vehicleType : VehicleType.values()){
            if(vehicleType.value.equals(value)){
                return vehicleType;
            }
        }
        throw new VehicleTypeNotFoundException();
    }
}
