package com.warehouse.warehouse_manager.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item; // Какой товар

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse; // На каком складе

    private Integer quantity; // Количество
}