package com.whitespace.magicznybank;

import java.util.Date;

/**
 * Created by Jakub on 19.09.2015.
 */
public class Service {
    public String id;
    public int tokens;
    public double estimate;
    public String name;
    public String description;

    public Service(String id, int tokens, double estimate, String name, String description) {
        this.id = id;
        this.tokens = tokens;
        this.estimate = estimate;
        this.name = name;
        this.description = description;
    }
}
