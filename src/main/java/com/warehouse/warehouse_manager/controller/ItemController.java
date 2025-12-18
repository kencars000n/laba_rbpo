package com.warehouse.warehouse_manager.controller;

import com.warehouse.warehouse_manager.model.Item;
import com.warehouse.warehouse_manager.repository.ItemRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {
    private final ItemRepository repository;

    public ItemController(ItemRepository repository) { this.repository = repository; }

    @GetMapping
    public List<Item> getAll() { return repository.findAll(); }

    @GetMapping("/{id}")
    public Item getOne(@PathVariable Long id) { return repository.findById(id).orElseThrow(); }

    @PostMapping
    public Item create(@RequestBody Item item) { return repository.save(item); }

    @PutMapping("/{id}")
    public Item update(@PathVariable Long id, @RequestBody Item details) {
        return repository.findById(id).map(item -> {
            item.setName(details.getName());
            item.setSku(details.getSku());
            item.setUnit(details.getUnit());
            return repository.save(item);
        }).orElseThrow();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { repository.deleteById(id); }
}