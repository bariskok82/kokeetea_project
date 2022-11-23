package com.guro.kokeetea_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.guro.kokeetea_project.entity.Warehouse;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    @Query("select w from Warehouse w where w.isValid = true")
    List<Warehouse> listWarehouse();

    @Query("select count(w) from Warehouse w where w.isValid = true")
    Long countWarehouse();
}
