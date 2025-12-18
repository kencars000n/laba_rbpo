package com.warehouse.warehouse_manager.repository;

import com.warehouse.warehouse_manager.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {
    // Поиск конкретного товара на конкретном складе
    Optional<Stock> findByWarehouseIdAndItemId(Long warehouseId, Long itemId);
}