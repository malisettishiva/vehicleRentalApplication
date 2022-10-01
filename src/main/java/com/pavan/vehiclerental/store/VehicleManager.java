package com.pavan.vehiclerental.store;

import com.pavan.vehiclerental.exception.VehicleAlreadyExistsException;
import com.pavan.vehiclerental.exception.VehicleNotFoundException;
import com.pavan.vehiclerental.model.Vehicle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VehicleManager implements StoreRepository<Vehicle, String> {

    private static volatile VehicleManager instance = null;

    private final Map<String, Vehicle> vehicles;

    private VehicleManager() {
        this.vehicles = new HashMap<>();
    }

    public static VehicleManager getInstance() {
        if (instance == null) {
            synchronized (VehicleManager.class) {
                if (instance == null) {
                    instance = new VehicleManager();
                }
            }
        }
        return instance;
    }

    @Override
    public List<Vehicle> findAll() {
        return this.vehicles.values().stream().collect(Collectors.toList());
    }

    @Override
    public Vehicle findById(String vehicleId) {
        if (!this.vehicles.containsKey(vehicleId)) {
            throw new VehicleNotFoundException();
        }

        return this.vehicles.get(vehicleId);
    }

    @Override
    public void save(Vehicle vehicle) {
        if (this.vehicles.containsKey(vehicle.getId())) {
            throw new VehicleAlreadyExistsException();
        }

        this.vehicles.put(vehicle.getId(), vehicle);
    }

    @Override
    public Vehicle update(Vehicle vehicle) {
        if (!this.vehicles.containsKey(vehicle.getId())) {
            throw new VehicleNotFoundException();
        }

        this.vehicles.put(vehicle.getId(), vehicle);
        return vehicle;
    }

    @Override
    public void delete(String vehicleId) {
        if (!this.vehicles.containsKey(vehicleId)) {
            throw new VehicleNotFoundException();
        }

        this.vehicles.remove(vehicleId);
    }

    @Override
    public void eraseAll() {
        this.vehicles.clear();
    }
}
