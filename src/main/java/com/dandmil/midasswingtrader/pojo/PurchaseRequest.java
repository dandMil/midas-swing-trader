package com.dandmil.midasswingtrader.pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseRequest {
    private String name;
    private int shares;
    private double price;

}