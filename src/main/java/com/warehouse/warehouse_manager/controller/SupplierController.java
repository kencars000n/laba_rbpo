package com.warehouse.warehouse_manager.controller;

import com.warehouse.warehouse_manager.model.Supplier;
import com.warehouse.warehouse_manager.repository.SupplierRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {
    private final SupplierRepository repository;

    public SupplierController(SupplierRepository repository) { this.repository = repository; }

    @GetMapping
    public List<Supplier> getAll() { return repository.findAll(); }

    @PostMapping
    public Supplier create(@RequestBody Supplier supplier) { return repository.save(supplier); }

    @PutMapping("/{id}")
    public Supplier update(@PathVariable Long id, @RequestBody Supplier details) {
        return repository.findById(id).map(s -> {
            s.setName(details.getName());
            s.setContactInfo(details.getContactInfo());
            return repository.save(s);
        }).orElseThrow();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { repository.deleteById(id); }
}