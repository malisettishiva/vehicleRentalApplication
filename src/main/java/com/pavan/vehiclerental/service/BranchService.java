package com.pavan.vehiclerental.service;

import com.pavan.vehiclerental.enums.VehicleType;
import com.pavan.vehiclerental.model.Branch;
import com.pavan.vehiclerental.model.Slot;
import com.pavan.vehiclerental.store.BranchManager;
import com.pavan.vehiclerental.store.SlotsManager;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

public class BranchService {

    private final BranchManager branchManager;
    private final SlotsManager slotsManager;

    public BranchService(final BranchManager branchManager, final SlotsManager slotsManager) {
        this.branchManager = branchManager;
        this.slotsManager = slotsManager;
    }

    private List<Slot> generateSlots(final String branchId, final String vehicleType) {
        final List<Slot> slots = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            final Slot slot = Slot.builder()
                    .branchId(branchId)
                    .vehicleType(vehicleType)
                    .startTime(i)
                    .endTime(i + 1)
                    .availableVehiclesCnt(0)
                    .vehicleIds(new ArrayList<>())
                    .build();
            slots.add(slot);
        }
        return slots;
    }

    public boolean addBranch(final String branchName, @NonNull final List<String> vehicleTypes) {
        final Branch branch = Branch.builder()
                .id(branchName)
                .name(branchName)
                .vehicleTypes(vehicleTypes.stream().map(VehicleType::fromString).toList())
                .vehicleIds(new ArrayList<>())
                .build();

        // create slots for each vehicleType
        for (final String vehicleType : vehicleTypes) {
            final List<Slot> slots = generateSlots(branchName, vehicleType);
            slotsManager.saveAll(slots);
        }

        branchManager.save(branch);
        return true;
    }
}
