package com.warehouse.warehouse_manager.controller;

import com.warehouse.warehouse_manager.model.Movement;
import com.warehouse.warehouse_manager.repository.MovementRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movements")
public class MovementController {

    private final MovementRepository repository;

    public MovementController(MovementRepository repository) {
        this.repository = repository;
    }

    // Получить все перемещения
    @GetMapping
    public List<Movement> getAll() {
        return repository.findAll();
    }

    // Создать новое перемещение
    @PostMapping
    public Movement create(@RequestBody Movement movement) {
        // Устанавливаем текущее время, если оно не пришло в запросе
        if (movement.getTimestamp() == null) {
            movement.setTimestamp(java.time.LocalDateTime.now());
        }
        return repository.save(movement);
    }

    // ОБНОВИТЬ перемещение (Тот самый метод, которого не хватало)
    @PutMapping("/{id}")
    public Movement update(@PathVariable Long id, @RequestBody Movement details) {
        return repository.findById(id).map(movement -> {
            movement.setAmount(details.getAmount());
            // Если нужно менять дату или склады, можно добавить и их
            if (details.getItem() != null) movement.setItem(details.getItem());
            if (details.getFromWarehouse() != null) movement.setFromWarehouse(details.getFromWarehouse());
            if (details.getToWarehouse() != null) movement.setToWarehouse(details.getToWarehouse());

            return repository.save(movement);
        }).orElseThrow(() -> new RuntimeException("Movement not found with id " + id));
    }

    // Удалить перемещение
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}