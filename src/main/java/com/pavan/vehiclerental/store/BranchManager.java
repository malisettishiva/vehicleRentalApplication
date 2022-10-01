package com.pavan.vehiclerental.store;

import com.pavan.vehiclerental.exception.BranchAlreadyExistsException;
import com.pavan.vehiclerental.exception.BranchNotFoundException;
import com.pavan.vehiclerental.model.Branch;
import lombok.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BranchManager implements StoreRepository<Branch, String> {

    private static volatile BranchManager instance = null;
    private final Map<String, Branch> branches;

    private BranchManager() {
        this.branches = new HashMap<>();
    }

    public static BranchManager getInstance() {
        if (instance == null) {
            synchronized (BranchManager.class) {
                if (instance == null) {
                    instance = new BranchManager();
                }
            }
        }
        return instance;
    }

    @Override
    public List<Branch> findAll() {
        return this.branches.values().stream().collect(Collectors.toList());
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

    @Override
    public void eraseAll() {
        this.branches.clear();
    }
}
