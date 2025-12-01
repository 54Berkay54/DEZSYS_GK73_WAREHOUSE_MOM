package com.example.demo;

import jakarta.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "warehouses")
@XmlAccessorType(XmlAccessType.FIELD)
public class WarehousesXml {

    @XmlElement(name = "warehouse")
    private List<WarehouseData> warehouses;

    public WarehousesXml() {}

    public WarehousesXml(List<WarehouseData> warehouses) {
        this.warehouses = warehouses;
    }
}