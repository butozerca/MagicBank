package com.whitespace.magicznybank;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Jakub on 19.09.2015.
 */
public class User {
    public int id;
    public String name;
    public String surname;
    public String email;
    public List<Integer> availableOperations;
    public List<Operation> operationsHistory;

    public User(int id, String name, String surname, String email) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.availableOperations = new LinkedList<Integer>();
        this.operationsHistory = new LinkedList<Operation>();
    }
}
