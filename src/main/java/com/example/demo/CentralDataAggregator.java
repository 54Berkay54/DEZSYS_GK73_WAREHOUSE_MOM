package com.example.demo;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CentralDataAggregator {

    private final List<WarehouseData> aggregated = new ArrayList<>();

    public synchronized void add(WarehouseData data) {
        aggregated.add(data);
    }

    public synchronized List<WarehouseData> getAll() {
        return new ArrayList<>(aggregated);
    }
}