package snitcoin.sneer.me.snitcoin_core;

public class Transaction {
	public final String hash;
	public final String amount;
	public final String progress;
	
	public Transaction(String hash, String amount, String progress) {
		this.hash = hash;
		this.amount = amount;
		this.progress = progress;
	}
}
