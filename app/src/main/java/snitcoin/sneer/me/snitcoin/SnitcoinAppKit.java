package snitcoin.sneer.me.snitcoin;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import org.bitcoinj.core.AbstractWalletEventListener;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.core.Wallet.BalanceType;
import org.bitcoinj.kits.WalletAppKit;



public class SnitcoinAppKit{

    private WalletAppKit kit;
    private Activity activity;
    private Context context;
    private NetworkParameters params;

    public SnitcoinAppKit(Activity activity, Context context, NetworkParameters params) {
        this.activity = activity;
        this.context = context;
        this.params = params;

        Log.d("[DEBUG]", "Opening snitcoin-wallet file");
        kit = new WalletAppKit(params, context.getFilesDir(), "snitcoin-wallet");
        kit.setAutoSave(true);
        kit.startAsync();

        Log.d("[DEBUG]", "AwaitRunning");
        kit.awaitRunning();

        Log.d("[DEBUG]", "Balance: " + kit.wallet().getBalance().getValue());
        Log.d("[DEBUG]", "Send money to: " + kit.wallet().currentReceiveAddress().toString());

        ((TextView) activity.findViewById(R.id.address)).setText(kit.wallet().currentReceiveAddress().toString());
        ((TextView) activity.findViewById(R.id.balance)).setText(kit.wallet().getBalance().toString());

        kit.wallet().addEventListener(new AbstractWalletEventListener() {
            @Override
            public void onCoinsReceived(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
                Log.d("[DEBUG]", "-----> coins resceived: " + tx.getHashAsString());
                Log.d("[DEBUG]", "received: " + tx.getValue(wallet));
                Log.d("[DEBUG]", "prev balance: " + prevBalance.getValue());
                Log.d("[DEBUG]", "new balance: " + newBalance.getValue());
            }

            @Override
            public void onCoinsSent(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
                Log.d("[DEBUG]", "-----> coins sent: " + tx.getHashAsString());
                Log.d("[DEBUG]", "coins sent: " + tx.getValue(wallet));
                Log.d("[DEBUG]", "prev balance: " + prevBalance.getValue());
                Log.d("[DEBUG]", "new balance: " + newBalance.getValue());
            }

            @Override
            public void onWalletChanged(Wallet wallet) {
                Log.d("[DEBUG]", "Wallet chaged! Current Balance: " + wallet.getBalance().getValue());
            }
        });
    }

    public void send(String amount, String address) throws AddressFormatException{
        Address to = new Address(params, address);
        Coin value = Coin.parseCoin(amount);
        try {
            Wallet.SendResult result = kit.wallet().sendCoins(kit.peerGroup(), to, value);

            Log.d("[DEBUG]","-----> " + kit.wallet().currentReceiveAddress());
            Log.d("[DEBUG]","coins sent. transaction hash: " + result.tx.getHashAsString());
        } catch (InsufficientMoneyException e) {
            System.err.println("-----> " + kit.wallet().currentReceiveAddress());
            System.err.println("Not enough coins in your wallet. Missing " + e.missing.getValue() + " satoshis are missing (including fees)");

            ListenableFuture<Coin> balanceFuture = kit.wallet().getBalanceFuture(value, BalanceType.AVAILABLE);
            FutureCallback<Coin> callback = new FutureCallback<Coin>() {
                public void onSuccess(Coin balance) {
                    Log.d("[DEBUG]","coins arrived and the wallet now has enough balance");
                }

                public void onFailure(Throwable t) {
                    Log.d("[DEBUG]","something went wrong");
                }
            };
            Futures.addCallback(balanceFuture, callback);
        }
    }
}
