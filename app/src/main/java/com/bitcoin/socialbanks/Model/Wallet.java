package com.bitcoin.socialbanks.Model;

import com.parse.ParseObject;

/**
 * Created by Marcos L Cardoso on 30/04/2015.
 */
public class Wallet {

    private ParseObject parseObject;
    private String name;
    private String idObject;
    private ParseObject socialBank;
    private String bitcoinAdress;
    private double balance;

    public Wallet(ParseObject parseObj, String id, ParseObject sbank, String btcAddress, double balance) {
        this.parseObject = parseObj;
        this.idObject = id;
        this.bitcoinAdress = btcAddress;
        this.socialBank = sbank;
        this.balance = balance;

        if (sbank != null)
            this.name = sbank.getString("name");
    }

    public Wallet(ParseObject obj) {
        this.socialBank = (ParseObject) obj.get("socialBank");
        this.name = socialBank.getString("name");
        this.bitcoinAdress = obj.getString("bitcoinAddress");
        this.balance = obj.getDouble("balance");
        this.idObject = obj.getObjectId();
        this.parseObject = obj;
    }

    public String getName() {
        return name;
    }

    public String getIdObject() {
        return idObject;
    }

    public ParseObject getSocialBank() {
        return socialBank;
    }

    public String getBitcoinAdress() {
        return bitcoinAdress;
    }

    public double getBalance() {
        return balance;
    }

    public ParseObject getParseObject() {
        return parseObject;
    }
}
