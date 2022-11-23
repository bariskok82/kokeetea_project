package com.guro.kokeetea_project.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "warehouse")
@Getter
@Setter
public class Warehouse {

    @Id
    @Column(name = "warehouse_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String location;

    @Column
    private String phone;

    @Column
    private String email;

    @OneToMany(mappedBy = "warehouse")
    private List<CurrentStock> currentStock = new ArrayList<>();

    public void addCurrentStock(CurrentStock currentStock) {
        if (!this.currentStock.contains(currentStock)) {
            this.currentStock.add(currentStock);
        }
        if (currentStock.getWarehouse() != this) {
            currentStock.setWarehouse(this);
        }
    }

    protected void removeCurrentStock(CurrentStock currentStock) {
        this.currentStock.remove(currentStock);
    }

    @OneToMany(mappedBy = "warehouse")
    private List<Store> store = new ArrayList<>();

    public void addStore(Store store) {
        if (!this.store.contains(store)) {
            this.store.add(store);
        }
        if (store.getWarehouse() != this) {
            store.setWarehouse(this);
        }
    }

    @Column
    private Boolean isValid;
}
