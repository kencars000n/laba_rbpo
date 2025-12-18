package com.warehouse.warehouse_manager.controller;

import com.warehouse.warehouse_manager.model.Stock;
import com.warehouse.warehouse_manager.repository.StockRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/stocks")
public class StockController {
    private final StockRepository repository;

    public StockController(StockRepository repository) { this.repository = repository; }

    @GetMapping
    public List<Stock> getAll() { return repository.findAll(); }

    @PostMapping
    public Stock create(@RequestBody Stock stock) { return repository.save(stock); }

    @PutMapping("/{id}")
    public Stock update(@PathVariable Long id, @RequestBody Stock details) {
        return repository.findById(id).map(s -> {
            s.setQuantity(details.getQuantity());
            return repository.save(s);
        }).orElseThrow();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { repository.deleteById(id); }
}