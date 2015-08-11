package snitcoin.sneer.me.snitcoin_core;

import java.util.List;

public class Status {
    public final String balance;
    public final List<Transaction> transactions;
    public final String receiveAddress;
    public final String message;
    
    public Status(String balance, List<Transaction> transactions, String receiveAddress, String message) {
		this.balance = balance;
		this.transactions = transactions;
		this.receiveAddress = receiveAddress;
		this.message = message;
	}
}
