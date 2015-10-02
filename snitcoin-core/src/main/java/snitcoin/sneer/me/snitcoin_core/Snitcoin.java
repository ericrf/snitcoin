package snitcoin.sneer.me.snitcoin_core;

import org.bitcoinj.core.AbstractWalletEventListener;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Peer;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionBroadcast;
import org.bitcoinj.core.TransactionBroadcast.ProgressCallback;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.core.Wallet.DustySendRequested;
import org.bitcoinj.core.WalletEventListener;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.MainNetParams;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Snitcoin implements Runnable {
	public static final String SNITCOIN = "SNITCOIN";
	public static final String STARTING = "#Starting#";
	public static final String DONE = "#Done#";
	private final org.slf4j.Logger log = LoggerFactory.getLogger(Snitcoin.class);

	private final File filesDir;
	private NetworkParameters params;
	private WalletAppKit bitcoin;
	private String filePrefix;
	private Listener listener;
	private List<snitcoin.sneer.me.snitcoin_core.Transaction> transactions;

	public Snitcoin(File filesDir) {
		this.filesDir = filesDir;
		this.params = MainNetParams.get();
		this.filePrefix = "mainnet";
		transactions = new ArrayList<snitcoin.sneer.me.snitcoin_core.Transaction>();
	}

	public void send(String amount, String address)
			throws AddressFormatException, InsufficientMoneyException, DustySendRequested, Exception {

		for(ECKey k : bitcoin.wallet().getIssuedReceiveKeys())
			if(k.toAddress(bitcoin.params()).toString().equals(address))
				throw new Exception("Invalid Address");

		Address to = new Address(params, address);
		Coin value = Coin.parseCoin(amount);
		bitcoin.wallet().sendCoins(bitcoin.peerGroup(), to, value);
	}

	public void run() {
		log.warn(STARTING);
		bitcoin = new WalletAppKit(params, filesDir, this.filePrefix);
		bitcoin.setDownloadListener(new SnitcoinDownloadProgressTracker())
				.setBlockingStartup(false)
				.setUserAgent(SNITCOIN, "1.0");
		bitcoin.setAutoSave(true);
        bitcoin.startAsync();
        bitcoin.awaitRunning();
        bitcoin.peerGroup().setDownloadTxDependencies(true);
        bitcoin.wallet().addEventListener(new WalletEventListenerImpl());

        Set<Transaction> ts = bitcoin.wallet().getTransactions(true);
        for (Transaction t : ts) {
        	List<Peer> peers = bitcoin.peerGroup().getConnectedPeers();
        	boolean mined = t.getAppearsInHashes() != null;
        	int numToBroadcastTo = (int) Math.max(1, Math.round(Math.ceil(peers.size() / 2.0)));
        	int numWaitingFor = (int) Math.ceil((peers.size() - numToBroadcastTo) / 2.0);
        	int numSeenPeers = t.getConfidence().numBroadcastPeers() + t.getConfidence().numBroadcastPeers() / 1;

        	final double progress = Math.min(1.0, mined ? 1.0 : numSeenPeers / (double) numWaitingFor);
        	transactions.add(new snitcoin.sneer.me.snitcoin_core.Transaction(t.getHashAsString(), t.getValue(bitcoin.wallet()).toPlainString(), "" + progress, getFee(t), getInputs(t), getOutputs(t)));
		}
		log.warn(DONE);
	}



	private String getFee(Transaction t) {
		return t.getFee() == null ? "0.00" : t.getFee().toPlainString();
	}

	public void setListener(Listener listener) {
		this.listener = listener;
	}

	void notify2(Wallet wallet, String message) {
		listener.onChange(new Status(wallet.getBalance().toPlainString() + "/EST:" + wallet.getBalance(Wallet.BalanceType.ESTIMATED).toPlainString(),
				transactions, bitcoin.wallet().currentReceiveAddress().toString(), message));
	}

	String[] getInputs(Transaction tx){
		List<TransactionInput> transactionInputs = tx.getInputs();
		String inputs[] = new String[transactionInputs.size()];
		for(int i = 0; i < transactionInputs.size() ; i++){
			inputs[i] = transactionInputs.get(i).getFromAddress().toString();
		}

		return inputs;
	}

	String[] getOutputs(Transaction tx){
		List<TransactionOutput> transactionOutputs = tx.getOutputs();
		String outputs[] = new String[transactionOutputs.size()];
		for(int i = 0; i < transactionOutputs.size() ; i++){
			TransactionOutput output = transactionOutputs.get(i);
			NetworkParameters params = bitcoin.params();
			Address addressFromP2SH = output.getAddressFromP2SH(params);
			Address addressFromP2PKHScript = output.getAddressFromP2PKHScript(params);
			outputs[i] = addressFromP2PKHScript.toString();
		}
		return outputs;
	}

	public String freshReceiveAddress() {
		return bitcoin.wallet().freshReceiveAddress().toString();
	}

	public String currentReceiveAddress() {
		return bitcoin.wallet().currentReceiveAddress().toString();
	}

	private class WalletEventListenerImpl extends AbstractWalletEventListener implements WalletEventListener {

		private void addTransaction(Transaction tx) {
			String hash = tx.getHashAsString();
			String amount = tx.getValue(bitcoin.wallet()).toPlainString();
			transactions.add(new snitcoin.sneer.me.snitcoin_core.Transaction(hash, amount, "0.0", getFee(tx), getInputs(tx), getOutputs(tx)));
		}

		private void setBroadcastProgressCallback(final Transaction tx, TransactionBroadcast broadcast) {
			broadcast.setProgressCallback(new ProgressCallback() {
				public void onBroadcastProgress(double progress) {
					for (int i = 0; i < transactions.size(); i++) {
						snitcoin.sneer.me.snitcoin_core.Transaction t = transactions.get(i);
						if (t.hash.equals(tx.getHashAsString())) {
							transactions.set(i,
								new snitcoin.sneer.me.snitcoin_core.Transaction(
										t.hash, t.amount, String.valueOf(progress), getFee(tx),
										getInputs(tx), getOutputs(tx)));
							notify2(bitcoin.wallet(), "Transaction Progress: " + tx.getHashAsString());
						}
					}
				}
			});
		}

		public void onCoinsReceived(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
			addTransaction(tx);
			TransactionBroadcast broadcast = bitcoin.peerGroup().broadcastTransaction(tx);
			setBroadcastProgressCallback(tx, broadcast);
			notify2(wallet, "Coins Received: " + tx.getHashAsString());
		}

		public void onCoinsSent(Wallet wallet, final Transaction tx, Coin prevBalance, Coin newBalance) {
			notify2(wallet, "Coins Sent: " + tx.getHashAsString());
		}

		public void onWalletChanged(Wallet wallet) {
			notify2(wallet, "Wallet Changed!");
		}
	}
}
