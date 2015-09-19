package com.whitespace.magicznybank;

import java.util.Date;

/**
 * Created by Jakub on 19.09.2015.
 */
public class Operation {
    public OperationType operationType;
    public Date requestTime;
    public int status;

    public Operation(OperationType operationType, Date requestTime) {
        this.operationType = operationType;
        this.requestTime = requestTime;
        this.status = 0;
    }
}
