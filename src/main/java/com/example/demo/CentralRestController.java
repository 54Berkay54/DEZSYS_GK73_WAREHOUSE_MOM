package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/central")
public class CentralRestController {

    @Autowired
    private CentralDataAggregator aggregator;

    @GetMapping(value = "/stock", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<WarehouseData> getJsonStock() {
        return aggregator.getAll();
    }

    @GetMapping(value = "/stock.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public WarehousesXml getXmlStock() {
        return new WarehousesXml(aggregator.getAll());
    }
}