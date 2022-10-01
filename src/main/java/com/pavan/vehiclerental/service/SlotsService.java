package com.pavan.vehiclerental.service;

import com.pavan.vehiclerental.enums.VehicleStatus;
import com.pavan.vehiclerental.model.Slot;
import com.pavan.vehiclerental.model.VehicleAvailability;
import com.pavan.vehiclerental.store.SlotID;
import com.pavan.vehiclerental.store.SlotsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.pavan.vehiclerental.constants.SlotIntervalConstants.SLOT_INTERVAL;

public class SlotsService {

    private final static Integer interval = SLOT_INTERVAL;

    private final SlotsManager slotsManager;

    public SlotsService(final SlotsManager slotsManager) {
        this.slotsManager = slotsManager;
    }

    private List<Slot> populateSlots(final String branchId, final String vehicleType,
                                     final Integer startTime, final Integer endTime) {
        final List<Slot> slots = new ArrayList<>();
        for (int i = startTime; i < endTime; i += interval) {
            if (i + interval > endTime) break;
            final Slot slot = Slot.builder()
                    .branchId(branchId)
                    .vehicleType(vehicleType)
                    .startTime(i)
                    .endTime(i + interval)
                    .vehicles(new ArrayList<>())
                    .build();
            slots.add(slot);
        }
        return slots;
    }

    private List<SlotID> populateSlotIDs(final String branchId, final String vehicleType,
                                         final Integer startTime, final Integer endTime) {
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
                                     final Integer startTime, final Integer endTime) {
        final List<Slot> slots = new ArrayList<>();
        for (final String vehicleType : vehicleTypes) {
            slots.addAll(populateSlots(branchId, vehicleType, startTime, endTime));
        }
        return slots;
    }

    public void onboardSlots(final String branchId, final List<String> vehicleTypes,
                             final Integer startTime, final Integer endTime) {

        final List<Slot> slots = populateSlots(branchId, vehicleTypes, startTime, endTime);
        slotsManager.saveAll(slots);
    }

    private boolean isStatusMatch(final VehicleAvailability vehicleAvailability, final VehicleStatus status) {
        if (status == null) return true;
        return vehicleAvailability.getStatus().equals(status);
    }

    public List<Slot> fetchSlots(final String branchId, final String vehicleType,
                                 final Integer startTime, final Integer endTime) {
        final List<SlotID> slotIDs = populateSlotIDs(branchId, vehicleType, startTime, endTime);
        final List<Slot> filteredSlots = new ArrayList<>();
        for (final SlotID slotID : slotIDs) {
            filteredSlots.add(slotsManager.findById(slotID));
        }
        return filteredSlots;
    }

    public List<VehicleAvailability> fetchVehicles(final String branchId, final String vehicleType,
                                                   final Integer startTime, final Integer endTime,
                                                   final VehicleStatus status) {

        final List<Slot> filteredSlots = fetchSlots(branchId, vehicleType, startTime, endTime);
        List<VehicleAvailability> filteredVehicles = null;
        for (final Slot slot : filteredSlots) {
            final List<VehicleAvailability> vehiclesWithGivenStatus = slot.getVehicles().stream()
                    .filter(vehicle -> isStatusMatch(vehicle, status))
                    .toList();

            if (filteredVehicles == null) {
                filteredVehicles = new ArrayList<>(vehiclesWithGivenStatus);
            } else {
                filteredVehicles.retainAll(vehiclesWithGivenStatus);
            }
        }

        return Optional.ofNullable(filteredVehicles).orElse(new ArrayList<>());
    }

    public boolean addVehicleAvailability(final Slot slot, final List<VehicleAvailability> vehicleAvailabilities) {
        final List<VehicleAvailability> temp = new ArrayList<>();
        for (final VehicleAvailability vehicleAvailability : vehicleAvailabilities) {
            final VehicleAvailability vehicleAvailability1 = VehicleAvailability.builder()
                    .id(vehicleAvailability.getId())
                    .price(vehicleAvailability.getPrice())
                    .status(vehicleAvailability.getStatus())
                    .build();
            temp.add(vehicleAvailability1);
        }
        slot.getVehicles().addAll(temp);

        slotsManager.update(slot);
        return true;
    }

    public boolean addVehicleAvailability(final List<Slot> slots, final List<VehicleAvailability> vehicleAvailabilities) {

        for (final Slot slot : slots) {
            final List<VehicleAvailability> temp = new ArrayList<>();
            for (final VehicleAvailability vehicleAvailability : vehicleAvailabilities) {
                final VehicleAvailability vehicleAvailability1 = VehicleAvailability.builder()
                        .id(vehicleAvailability.getId())
                        .price(vehicleAvailability.getPrice())
                        .status(vehicleAvailability.getStatus())
                        .build();
                temp.add(vehicleAvailability1);
            }

            slot.getVehicles().addAll(temp);
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
