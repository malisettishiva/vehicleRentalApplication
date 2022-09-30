package com.pavan.vehiclerental.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

public class Utils {
    public static <T> Page<T> sortAndPaginateList(List<T> data, Pageable pageable) {
        data.sort(ComparatorUtils.comparatorOf(pageable.getSort()));
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), data.size());
        if (start > end) {
            return new PageImpl<>(Collections.emptyList());
        }
        return new PageImpl<>(data.subList(start, end), pageable, data.size());
    }
}
