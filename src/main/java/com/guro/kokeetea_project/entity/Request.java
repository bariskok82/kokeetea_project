package com.guro.kokeetea_project.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import javax.persistence.*;

import com.guro.kokeetea_project.constant.RequestStatus;

@Entity
@Table(name = "request")
@Getter
@Setter
public class Request{

    @Id
    @Column(name = "request_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    @Column
    private Long amount;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @Column
    private LocalDateTime date;

    @Column
    private LocalDateTime dateFinish;
}
