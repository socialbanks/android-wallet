package com.bitcoin.socialbanks.Model;

import java.util.Date;

/**
 * Created by Marcos L Cardoso on 30/04/2015.
 */
public class Transaction {

    private Date dateTransaction;
    private double value;
    private String description;

    public Transaction(Double value){
        this.value = value;
        dateTransaction = new Date();
    }

    public Transaction(Date date,Double value, String description){
        this.value = value;
        dateTransaction = date;
        this.description = description;
    }

    public Date getDateTransaction() {
        return dateTransaction;
    }

    public double getValue() {
        return value;
    }

    public String getDescription() { return description;}
}
