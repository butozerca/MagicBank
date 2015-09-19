package com.whitespace.magicznybank;

/**
 * Created by Jakub on 19.09.2015.
 */
public class OperationType {
    public int id;
    public String name;
    public String description;
    public Double price;

    public OperationType(int id, String name, String description, Double price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }
}
