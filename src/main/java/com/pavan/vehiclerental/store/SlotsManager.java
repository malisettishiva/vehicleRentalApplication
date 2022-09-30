package com.pavan.vehiclerental.store;

import com.pavan.vehiclerental.exception.SlotAlreadyExistsException;
import com.pavan.vehiclerental.exception.SlotNotFoundException;
import com.pavan.vehiclerental.model.Slot;

import java.util.*;

public class SlotsManager implements StoreRepository<Slot, SlotID>, BulkDataExecutor<Slot, SlotID> {

    private final Map<SlotID, Slot> vehicleAvailabilitySlots;

    public SlotsManager() {
        this.vehicleAvailabilitySlots = new HashMap<>();
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
                              final Integer startTime, final Integer endTime) {

        final List<Slot> filteredSlots = new ArrayList<>();
        for (int i = startTime; i < endTime; i++) {
            final SlotID slotID = generateSlotId(branchId, vehicleType, i, i + 1);
            filteredSlots.add(findById(slotID));
        }
        return filteredSlots;
    }

    public List<String> getAllAvailableVehicles(final String branchId, final String vehicleType,
                                                final Integer startTime, final Integer endTime) {

        final List<Slot> filteredSlots = findAll(branchId, vehicleType, startTime, endTime);

        List<String> availableVehicles = null;
        for (final Slot slot : filteredSlots) {
            if (availableVehicles == null) {
                availableVehicles = new ArrayList<>(slot.getVehicleIds());
            } else {
                availableVehicles.retainAll(slot.getVehicleIds());
            }
        }

        return Optional.ofNullable(availableVehicles).orElse(new ArrayList<>());
    }
}
