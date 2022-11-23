package com.guro.kokeetea_project.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "store")
@Getter
@Setter
public class Store {

    @Id
    @Column(name = "store_id")
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

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    public void setWarehouse(Warehouse warehouse) {
        if (this.warehouse != null) {
            this.warehouse.getStore().remove(this);
        }
        this.warehouse = warehouse;
        if (warehouse != null) {
            warehouse.addStore(this);
        }
    }

    @Column
    private Boolean isValid;
}
