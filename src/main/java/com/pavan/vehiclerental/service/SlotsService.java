package com.pavan.vehiclerental.service;

import com.pavan.vehiclerental.enums.VehicleStatus;
import com.pavan.vehiclerental.model.Slot;
import com.pavan.vehiclerental.model.Vehicle;
import com.pavan.vehiclerental.model.VehicleAvailability;
import com.pavan.vehiclerental.store.SlotID;
import com.pavan.vehiclerental.store.SlotsManager;
import com.pavan.vehiclerental.store.VehicleManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SlotsService {

    private final SlotsManager slotsManager;
    private final VehicleManager vehicleManager;

    public SlotsService(final SlotsManager slotsManager, final VehicleManager vehicleManager) {
        this.slotsManager = slotsManager;
        this.vehicleManager = vehicleManager;
    }

    private List<Slot> populateSlots(final String branchId, final String vehicleType,
                                     final Integer startTime, final Integer endTime, final Integer interval) {
        final List<Slot> slots = new ArrayList<>();
        for (int i = startTime; i < endTime; i += interval) {
            if (i + interval > endTime) break;
            final Slot slot = Slot.builder()
                    .branchId(branchId)
                    .vehicleType(vehicleType)
                    .startTime(i)
                    .endTime(i + interval)
                    .availableVehiclesCnt(0)
                    .vehicles(new ArrayList<>())
                    .build();
            slots.add(slot);
        }
        return slots;
    }

    private List<SlotID> populateSlotIDs(final String branchId, final String vehicleType,
                                         final Integer startTime, final Integer endTime, final Integer interval) {
        final List<SlotID> slotIDS = new ArrayList<>();
        for (int i = startTime; i < endTime; i += interval) {
            if (i + interval > endTime) break;
            final SlotID slotID = SlotID.builder()
                    .branchId(branchId)
                    .vehicleType(vehicleType)
                    .startTime(i)
                    .endTime(i + interval)
                    .build();
            slotIDS.add(slotID);
        }

        return slotIDS;
    }

    private List<Slot> populateSlots(final String branchId, final List<String> vehicleTypes,
                                     final Integer startTime, final Integer endTime, final Integer interval) {
        final List<Slot> slots = new ArrayList<>();
        for (final String vehicleType : vehicleTypes) {
            slots.addAll(populateSlots(branchId, vehicleType, startTime, endTime, interval));
        }
        return slots;
    }

    public void onboardSlots(final String branchId, final List<String> vehicleTypes,
                             final Integer startTime, final Integer endTime, final Integer interval) {

        final List<Slot> slots = populateSlots(branchId, vehicleTypes, startTime, endTime, interval);
        slotsManager.saveAll(slots);
    }

    private boolean isStatusMatch(final VehicleAvailability vehicleAvailability, final VehicleStatus status) {
        if (status == null) return true;
        return vehicleAvailability.getStatus().equals(status);
    }

    public List<Slot> fetchSlots(final String branchId, final String vehicleType,
                                 final Integer startTime, final Integer endTime, final Integer interval) {
        final List<SlotID> slotIDs = populateSlotIDs(branchId, vehicleType, startTime, endTime, interval);
        final List<Slot> filteredSlots = new ArrayList<>();
        for (final SlotID slotID : slotIDs) {
            filteredSlots.add(slotsManager.findById(slotID));
        }
        return filteredSlots;
    }

    public List<Vehicle> fetchVehicles(final String branchId, final String vehicleType,
                                       final Integer startTime, final Integer endTime, final Integer interval,
                                       final VehicleStatus status) {

        final List<Slot> filteredSlots = fetchSlots(branchId, vehicleType, startTime, endTime, interval);

        List<String> filteredVehicles = null;
        for (final Slot slot : filteredSlots) {
            final List<String> vehiclesWithGivenStatus = slot.getVehicles().stream()
                    .filter(vehicle -> isStatusMatch(vehicle, status))
                    .map(VehicleAvailability::getId)
                    .toList();

            if (filteredVehicles == null) {
                filteredVehicles = new ArrayList<>(vehiclesWithGivenStatus);
            } else {
                filteredVehicles.retainAll(vehiclesWithGivenStatus);
            }
        }

        filteredVehicles = Optional.ofNullable(filteredVehicles).orElse(new ArrayList<>());

        return filteredVehicles.stream().map(vehicleManager::findById).toList();
    }

    public boolean addVehicleAvailability(final List<Slot> slots, final List<String> vehicleIds) {

        final List<VehicleAvailability> newVehicles = vehicleIds.stream()
                .map(vehicleId -> VehicleAvailability.builder()
                        .id(vehicleId).status(VehicleStatus.AVAILABLE)
                        .build())
                .toList();

        for (final Slot slot : slots) {
            slot.getVehicles().addAll(newVehicles);
        }
        slotsManager.updateAll(slots);
        return true;
    }

    public boolean updateVehicleAvailability(final List<Slot> slots, final List<String> vehicleIds,
                                             final VehicleStatus status) {
        for (final Slot slot : slots) {
            slot.getVehicles().stream().filter(vehicle -> vehicleIds.contains(vehicle.getId()))
                    .forEach(vehicle -> vehicle.setStatus(status));
        }
        slotsManager.updateAll(slots);
        return true;
    }

}
