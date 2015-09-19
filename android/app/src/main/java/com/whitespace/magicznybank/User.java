package com.whitespace.magicznybank;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Jakub on 19.09.2015.
 */
public class User {
    public String id;
    public String login;
    public String pass;
    public String name;
    public String surname;
    public String email;
    public Double maxLoan;
    public Double money;
    public String tariff;
    public List<Integer> availableOperations;
    public List<Operation> operationsHistory;

    public User(String id, String login, String pass, String name, String surname, String email, Double maxLoad, Double money, String tariff) {
        this.id = id;
        this.login = login;
        this.pass = pass;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.maxLoan = maxLoad;
        this.money = money;
        this.tariff = tariff;
        this.availableOperations = new LinkedList<Integer>();
        this.operationsHistory = new LinkedList<Operation>();
    }

    public String LoginDataJSon() {
        return "\"login\":\"\"+login+\"\\\",\\\"pass\\\":\\\"\"+pass+\"\"";
    }
}
