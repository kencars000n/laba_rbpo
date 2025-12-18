package com.warehouse.warehouse_manager.services;

import com.warehouse.warehouse_manager.model.*;
import com.warehouse.warehouse_manager.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WarehouseService {

    @Autowired
    private MovementRepository movementRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    // ======================================================
    // МЕТОДЫ ПОЛУЧЕНИЯ ДАННЫХ (GET)
    // ======================================================

    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    public List<Movement> getAllMovements() {
        return movementRepository.findAll();
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    // ======================================================
    // 5 БИЗНЕС-ОПЕРАЦИЙ
    // ======================================================

    /**
     * №1: ПРИХОД ТОВАРА
     * Затрагивает: Stock (обновление) и Movement (логирование)
     */
    @Transactional
    public Movement receiveGoods(Movement movement) {
        movement.setTimestamp(LocalDateTime.now());

        Stock stock = stockRepository.findByWarehouseIdAndItemId(
                movement.getToWarehouse().getId(),
                movement.getItem().getId()
        ).orElseGet(() -> {
            Stock s = new Stock();
            s.setWarehouse(movement.getToWarehouse());
            s.setItem(movement.getItem());
            s.setQuantity(0);
            return s;
        });

        stock.setQuantity(stock.getQuantity() + movement.getAmount());
        stockRepository.save(stock);
        return movementRepository.save(movement);
    }

    /**
     * №2: РАСХОД ТОВАРА
     * Затрагивает: Stock (списание) и Movement
     */
    @Transactional
    public Movement shipGoods(Movement movement) {
        movement.setTimestamp(LocalDateTime.now());

        Stock stock = stockRepository.findByWarehouseIdAndItemId(
                movement.getFromWarehouse().getId(),
                movement.getItem().getId()
        ).orElseThrow(() -> new RuntimeException("Товар отсутствует на складе!"));

        if (stock.getQuantity() < movement.getAmount()) {
            throw new RuntimeException("Недостаточно товара! В наличии: " + stock.getQuantity());
        }

        stock.setQuantity(stock.getQuantity() - movement.getAmount());
        stockRepository.save(stock);
        return movementRepository.save(movement);
    }

    /**
     * №3: ПЕРЕМЕЩЕНИЕ (ТРАНСФЕР)
     * Затрагивает: Два склада и Movement в одной транзакции
     */
    @Transactional
    public Movement transferGoods(Movement movement) {
        movement.setTimestamp(LocalDateTime.now());

        // Используем уже написанную логику списания и прихода
        shipGoods(movement);
        receiveGoods(movement);

        return movement;
    }

    /**
     * №4: РЕГИСТРАЦИЯ БРАКА
     * Затрагивает: Stock (списание), Item (обновление имени в каталоге) и Movement
     */
    @Transactional
    public String reportDefectiveGoods(Long warehouseId, Long itemId, Integer amount) {
        Stock stock = stockRepository.findByWarehouseIdAndItemId(warehouseId, itemId)
                .orElseThrow(() -> new RuntimeException("Товар не найден"));

        if (stock.getQuantity() < amount) {
            throw new RuntimeException("Недостаточно товара для списания брака");
        }

        // 1. Списываем остаток
        stock.setQuantity(stock.getQuantity() - amount);
        stockRepository.save(stock);

        // 2. Помечаем в каталоге (Item)
        Item item = stock.getItem();
        if (!item.getName().contains("[БРАК]")) {
            item.setName(item.getName() + " [БРАК ПОДТВЕРЖДЕН]");
            itemRepository.save(item);
        }

        // 3. Создаем запись в истории
        Movement m = new Movement();
        m.setItem(item);
        m.setFromWarehouse(stock.getWarehouse());
        m.setAmount(amount);
        m.setTimestamp(LocalDateTime.now());
        movementRepository.save(m);

        return "Брак зафиксирован. Остатки уменьшены, каталог обновлен.";
    }

    /**
     * №5: СЛИЯНИЕ СКЛАДОВ (ЛИКВИДАЦИЯ)
     * Затрагивает: Массовое обновление Stock и создание Movement для каждой позиции
     */
    @Transactional
    public String mergeWarehouses(Long fromId, Long toId) {
        List<Stock> sourceStocks = stockRepository.findAll().stream()
                .filter(s -> s.getWarehouse().getId().equals(fromId) && s.getQuantity() > 0)
                .toList();

        if (sourceStocks.isEmpty()) {
            return "Склад #" + fromId + " пуст. Переносить нечего.";
        }

        Warehouse targetWh = warehouseRepository.findById(toId)
                .orElseThrow(() -> new RuntimeException("Целевой склад не найден"));

        for (Stock s : sourceStocks) {
            Movement m = new Movement();
            m.setItem(s.getItem());
            m.setAmount(s.getQuantity());
            m.setFromWarehouse(s.getWarehouse());
            m.setToWarehouse(targetWh);

            // Вызываем логику перемещения для каждой строки
            transferGoods(m);
        }

        return "Ликвидация завершена. Все товары со склада #" + fromId + " перенесены на #" + toId;
    }
}