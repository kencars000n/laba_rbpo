package com.warehouse.warehouse_manager.controller;

import com.warehouse.warehouse_manager.model.Warehouse;
import com.warehouse.warehouse_manager.repository.WarehouseRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {
    private final WarehouseRepository repository;

    public WarehouseController(WarehouseRepository repository) { this.repository = repository; }

    @GetMapping
    public List<Warehouse> getAll() { return repository.findAll(); }

    @PostMapping
    public Warehouse create(@RequestBody Warehouse warehouse) { return repository.save(warehouse); }

    @PutMapping("/{id}")
    public Warehouse update(@PathVariable Long id, @RequestBody Warehouse details) {
        return repository.findById(id).map(w -> {
            w.setName(details.getName());
            w.setAddress(details.getAddress());
            return repository.save(w);
        }).orElseThrow();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { repository.deleteById(id); }
}