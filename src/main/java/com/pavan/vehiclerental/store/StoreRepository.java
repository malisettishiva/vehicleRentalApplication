package com.pavan.vehiclerental.store;

import java.util.List;

public interface StoreRepository<T, ID> {

    List<T> findAll();

    T findById(ID id);

    void save(T data);

    T update(T data);

    void delete(ID id);

    void eraseAll();
}
