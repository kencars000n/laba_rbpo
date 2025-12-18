package com.warehouse.warehouse_manager.controller;

import com.warehouse.warehouse_manager.model.Movement;
import com.warehouse.warehouse_manager.services.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movements")
public class MovementController {

    @Autowired
    private WarehouseService warehouseService;

    // ТО ЧЕГО НЕ ХВАТАЛО: Список всех движений (Аудит)
    @GetMapping
    public List<Movement> getAll() {
        return warehouseService.getAllMovements();
    }

    // Операция №1: Приход
    @PostMapping("/receive")
    public Movement receive(@RequestBody Movement m) {
        return warehouseService.receiveGoods(m);
    }

    // Операция №2: Расход
    @PostMapping("/ship")
    public Movement ship(@RequestBody Movement m) {
        return warehouseService.shipGoods(m);
    }

    // Операция №3: Перемещение
    @PostMapping("/transfer")
    public Movement transfer(@RequestBody Movement m) {
        return warehouseService.transferGoods(m);
    }

    // Операция №4: Списание брака
    @PostMapping("/defect")
    public String reportDefect(@RequestParam Long warehouseId, @RequestParam Long itemId, @RequestParam Integer amount) {
        return warehouseService.reportDefectiveGoods(warehouseId, itemId, amount);
    }

    // Операция №5: Слияние складов
    @PostMapping("/merge")
    public String merge(@RequestParam Long fromId, @RequestParam Long toId) {
        return warehouseService.mergeWarehouses(fromId, toId);
    }
}