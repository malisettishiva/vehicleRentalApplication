package com.pavan.vehiclerental.store;

import com.pavan.vehiclerental.exception.BranchAlreadyExistsException;
import com.pavan.vehiclerental.exception.BranchNotFoundException;
import com.pavan.vehiclerental.model.Branch;
import lombok.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BranchManager implements StoreRepository<Branch, String> {
    private final Map<String, Branch> branches;

    public BranchManager() {
        this.branches = new HashMap<>();
    }

    @Override
    public List<Branch> findAll() {
        return this.branches.values().stream().toList();
    }

    @Override
    public Branch findById(final String branchId) {
        if (!this.branches.containsKey(branchId)) {
            throw new BranchNotFoundException();
        }

        return this.branches.get(branchId);
    }

    @Override
    public void save(@NonNull final Branch branch) {
        if (this.branches.containsKey(branch.getId())) {
            throw new BranchAlreadyExistsException();
        }

        this.branches.put(branch.getId(), branch);
    }

    @Override
    public Branch update(@NonNull final Branch branch) {
        if (!this.branches.containsKey(branch.getId())) {
            throw new BranchNotFoundException();
        }

        this.branches.put(branch.getId(), branch);
        return branch;
    }

    @Override
    public void delete(final String branchId) {
        if (!this.branches.containsKey(branchId)) {
            throw new BranchNotFoundException();
        }

        this.branches.remove(branchId);
    }
}
