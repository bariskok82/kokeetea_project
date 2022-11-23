package com.guro.kokeetea_project.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "current_stock")
@Getter
@Setter
public class CurrentStock {

    @Id
    @Column(name = "current_stock_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long amount;

    @ManyToOne
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    public void setWarehouse(Warehouse warehouse) {
        if (this.warehouse != null) {
            this.warehouse.removeCurrentStock(this);
        }
        this.warehouse = warehouse;
        if (warehouse != null) {
            warehouse.addCurrentStock(this);
        }
    }
}
