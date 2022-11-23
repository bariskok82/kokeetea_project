package com.guro.kokeetea_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.guro.kokeetea_project.entity.Store;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {
    @Query("select s from Store s where s.isValid = true")
    List<Store> listStore();

    @Query("select count(s) from Store s where s.isValid = true")
    Long countStore();

    @Query("select s from Store s where s.email = :email and s.isValid = true")
    Store findByEmail(@Param("email") String email);
}
