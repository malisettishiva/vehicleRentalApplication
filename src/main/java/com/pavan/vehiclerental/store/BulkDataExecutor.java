package com.pavan.vehiclerental.store;

import java.util.List;

public interface BulkDataExecutor<T, ID> {
    void saveAll(List<T> data);

    void updateAll(List<T> data);
}
