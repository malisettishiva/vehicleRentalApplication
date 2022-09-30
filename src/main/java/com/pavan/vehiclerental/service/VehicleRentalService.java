package com.pavan.vehiclerental.service;

import com.pavan.vehiclerental.enums.VehicleStatus;
import com.pavan.vehiclerental.enums.VehicleType;
import com.pavan.vehiclerental.exception.InvalidSlotDurationException;
import com.pavan.vehiclerental.model.Branch;
import com.pavan.vehiclerental.model.Slot;
import com.pavan.vehiclerental.model.Vehicle;
import com.pavan.vehiclerental.store.BranchManager;
import com.pavan.vehiclerental.validator.RangeValidator;
import lombok.NonNull;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.pavan.vehiclerental.constants.SlotIntervalConstants.*;

public class VehicleRentalService {

    private final BranchManager branchManager;

    private final VehicleService vehicleService;
    private final BookingService bookingService;

    private final SlotsService slotsService;

    public VehicleRentalService(final BranchManager branchManager,
                                final VehicleService vehicleService, final BookingService bookingService,
                                final SlotsService slotsService) {
        this.vehicleService = vehicleService;
        this.bookingService = bookingService;
        this.branchManager = branchManager;
        this.slotsService = slotsService;
    }

    private List<Slot> generateSlots(final String branchId, final String vehicleType) {
        final List<Slot> slots = new ArrayList<>();
        final int interval = SLOT_INTERVAL;
        for (int i = DAY_START; i < DAY_END; i += interval) {
            if (i + interval > DAY_END) break;
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

    public boolean onboardBranch(@NonNull final String branchName, @NonNull final List<String> vehicleTypes) {
        final Branch branch = Branch.builder()
                .id(branchName)
                .name(branchName)
                .vehicleTypes(vehicleTypes.stream().map(VehicleType::fromString).toList())
                .vehicleIds(new ArrayList<>())
                .build();

        slotsService.onboardSlots(branchName, vehicleTypes, DAY_START, DAY_END, SLOT_INTERVAL);

        branchManager.save(branch);
        return true;
    }

    private boolean isVehicleTypeAllowed(final List<VehicleType> allowedVehicleTypes, final String vehicleType) {
        return allowedVehicleTypes.contains(VehicleType.fromString(vehicleType));
    }

    public Boolean onboardVehicle(@NonNull final String branchId, @NonNull final String vehicleType,
                                  @NonNull final String vehicleId, @NonNull final Double price) {

        final Branch branch = branchManager.findById(branchId);
        if (!isVehicleTypeAllowed(branch.getVehicleTypes(), vehicleType)) return false;

        if (!vehicleService.addVehicle(branchId, vehicleType, vehicleId, price)) return false;

        final List<String> updatedVehicleIds = Optional.ofNullable(branch.getVehicleIds()).orElse(new ArrayList<>());
        updatedVehicleIds.add(vehicleId);
        branch.setVehicleIds(updatedVehicleIds);
        branchManager.update(branch);

        return true;
    }

    public Double bookVehicle(@NonNull final String branchId, @NonNull final String vehicleType,
                              @NonNull final Integer startTime, @NonNull final Integer endTime) {
        if (!RangeValidator.isValid(startTime, endTime)) {
            throw new InvalidSlotDurationException();
        }

        final Branch branch = branchManager.findById(branchId);
        final VehicleType vehicleTypeValue = VehicleType.fromString(vehicleType);

        return bookingService.bookVehicle(branch.getId(), vehicleTypeValue.toString(), startTime, endTime);
    }

    public List<Vehicle> getAllVehicles(@NonNull final String branchId, @NonNull final Integer startTime,
                                        @NonNull final Integer endTime,
                                        @NonNull Pageable pageable,
                                        @NonNull final VehicleStatus status) {
        if (!RangeValidator.isValid(startTime, endTime)) {
            throw new InvalidSlotDurationException();
        }

        final Branch branch = branchManager.findById(branchId);

        return vehicleService.getAllVehicles(branch, startTime, endTime, pageable, status);
    }

}
