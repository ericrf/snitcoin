package snitcoin.sneer.me.snitcoin_core;

public class Transaction {
	public final String hash;
	public final String amount;
	public final String progress;
	public final String[] inputs;
	public final String[] outputs;
	
	public Transaction(String hash, String amount, String progress, String[] inputs, String[] outputs) {
		this.hash = hash;
		this.amount = amount;
		this.progress = progress;
		this.inputs = inputs;
		this.outputs = outputs;
	}
}
