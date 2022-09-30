package com.pavan.vehiclerental.store;

import com.pavan.vehiclerental.enums.VehicleStatus;
import com.pavan.vehiclerental.exception.SlotAlreadyExistsException;
import com.pavan.vehiclerental.exception.SlotNotFoundException;
import com.pavan.vehiclerental.model.Slot;
import com.pavan.vehiclerental.model.VehicleAvailability;

import java.util.*;

public class SlotsManager implements StoreRepository<Slot, SlotID>, BulkDataExecutor<Slot, SlotID> {

    private static volatile SlotsManager instance = null;

    private final Map<SlotID, Slot> vehicleAvailabilitySlots;

    private SlotsManager() {
        this.vehicleAvailabilitySlots = new HashMap<>();
    }

    public static SlotsManager getInstance() {
        if (instance == null) {
            synchronized (SlotsManager.class) {
                if (instance == null) {
                    instance = new SlotsManager();
                }
            }
        }

        return instance;
    }

    @Override
    public List<Slot> findAll() {
        return this.vehicleAvailabilitySlots.values().stream().toList();
    }

    @Override
    public Slot findById(SlotID slotID) {
        if (!this.vehicleAvailabilitySlots.containsKey(slotID)) {
            throw new SlotNotFoundException();
        }

        return this.vehicleAvailabilitySlots.get(slotID);
    }

    @Override
    public void save(Slot slot) {

        final SlotID slotID = generateSlotId(slot.getBranchId(), slot.getVehicleType(), slot.getStartTime(),
                slot.getEndTime());

        if (this.vehicleAvailabilitySlots.containsKey(slotID)) {
            throw new SlotAlreadyExistsException();
        }

        this.vehicleAvailabilitySlots.put(slotID, slot);
    }

    @Override
    public Slot update(Slot slot) {
        final SlotID slotID = generateSlotId(slot.getBranchId(), slot.getVehicleType(), slot.getStartTime(),
                slot.getEndTime());

        if (!this.vehicleAvailabilitySlots.containsKey(slotID)) {
            throw new SlotNotFoundException();
        }

        this.vehicleAvailabilitySlots.put(slotID, slot);
        return slot;
    }

    @Override
    public void delete(SlotID slotID) {
        if (!this.vehicleAvailabilitySlots.containsKey(slotID)) {
            throw new SlotAlreadyExistsException();
        }

        this.vehicleAvailabilitySlots.remove(slotID);
    }

    @Override
    public void eraseAll() {
        this.vehicleAvailabilitySlots.clear();
    }

    @Override
    public void saveAll(List<Slot> slots) {
        for (final Slot slot : slots) {
            final SlotID slotID = generateSlotId(slot.getBranchId(), slot.getVehicleType(), slot.getStartTime(),
                    slot.getEndTime());
            this.vehicleAvailabilitySlots.put(slotID, slot);
        }
    }

    @Override
    public void updateAll(List<Slot> slots) {
        for (final Slot slot : slots) {
            final SlotID slotID = SlotID.builder()
                    .branchId(slot.getBranchId())
                    .vehicleType(slot.getVehicleType())
                    .startTime(slot.getStartTime())
                    .endTime(slot.getEndTime())
                    .build();
            this.vehicleAvailabilitySlots.put(slotID, slot);
        }
    }

    private SlotID generateSlotId(final String branchId, final String vehicleType,
                                  final Integer startTime, final Integer endTime) {
        return SlotID.builder()
                .branchId(branchId)
                .vehicleType(vehicleType)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }

    public List<Slot> findAll(final String branchId, final String vehicleType,
                              final Integer startTime, final Integer endTime,
                              final Integer interval) {

        final List<Slot> filteredSlots = new ArrayList<>();
        for (int i = startTime; i < endTime; i += interval) {
            if (i + interval > endTime) break;
            final SlotID slotID = generateSlotId(branchId, vehicleType, i, i + interval);
            filteredSlots.add(findById(slotID));
        }
        return filteredSlots;
    }

    public List<String> getAllVehicles(final String branchId, final String vehicleType,
                                       final Integer startTime, final Integer endTime,
                                       final Integer interval) {

        final List<Slot> filteredSlots = findAll(branchId, vehicleType, startTime, endTime, interval);

        List<String> filteredVehicles = null;
        for (final Slot slot : filteredSlots) {
            if (filteredVehicles == null) {
                filteredVehicles = new ArrayList<>(
                        slot.getVehicles().stream().map(VehicleAvailability::getId).toList());
            } else {
                filteredVehicles.retainAll(
                        slot.getVehicles().stream().map(VehicleAvailability::getId).toList());
            }
        }

        return Optional.ofNullable(filteredVehicles).orElse(new ArrayList<>());
    }

    public List<String> getAllVehicles(final String branchId, final String vehicleType,
                                       final Integer startTime, final Integer endTime,
                                       final Integer interval, final VehicleStatus status) {

        final List<Slot> filteredSlots = findAll(branchId, vehicleType, startTime, endTime, interval);

        List<String> filteredVehicles = null;
        for (final Slot slot : filteredSlots) {
            if (filteredVehicles == null) {
                filteredVehicles = new ArrayList<>(
                        VehicleAvailability.filterVehicles(slot.getVehicles(), status));
            } else {
                filteredVehicles.retainAll(
                        VehicleAvailability.filterVehicles(slot.getVehicles(), status));
            }
        }

        return Optional.ofNullable(filteredVehicles).orElse(new ArrayList<>());
    }

}
