package com.richard.task;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OriginRepository extends CrudRepository<Origin, Long> {

    boolean existsByUri(String uri);

    @Query("SELECT * FROM ORIGIN")
    List<Origin> findAllOrigins();
}
