package com.whitespace.magicznybank;

/**
 * Created by Jakub on 19.09.2015.
 */
public class OperationType {
    public int id;
    public String name;
    public String description;

    public OperationType(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
