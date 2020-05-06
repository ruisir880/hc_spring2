package com.ray.hc_spring2.core.repository;

import com.ray.hc_spring2.model.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;

/**
 * Created by rui on 2018/8/18.
 */
public interface PageQueryRepository<T, ID extends Serializable> extends CrudRepository<UserInfo, Long> {
    Iterable<T> findAll(Sort sort);

    Page<T> findAll(Pageable pageable);
}
