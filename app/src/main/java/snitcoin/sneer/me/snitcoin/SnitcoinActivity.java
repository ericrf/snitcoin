package snitcoin.sneer.me.snitcoin;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import org.bitcoinj.core.AbstractWalletEventListener;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.Monetary;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.utils.MonetaryFormat;

import snitcoin.sneer.me.snitcoin_core.Listener;
import snitcoin.sneer.me.snitcoin_core.Snitcoin;
import snitcoin.sneer.me.snitcoin_core.Status;
import snitcoin.sneer.me.snitcoin_core.Transaction;


public class SnitcoinActivity extends ActionBarActivity {

    SnitcoinAppKit kit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snitcoin);
        Snitcoin snitcoin = new Snitcoin();
        snitcoin.setListener(new Listener() {
            public void onChange(Status status) {

                System.out.println("--------------------------------------------");
                System.out.println("Message: " + status.message);
                ((TextView) findViewById(R.id.balance)).setText(status.balance);
                ((TextView) findViewById(R.id.address)).setText(status.receiveAddress);

                System.out.println("Transactions: ");
                for (Transaction transaction : status.transactions) {
                    ((TextView) findViewById(R.id.balance)).setText(transaction.amount);
                    System.out.println("\tTransaction hash: " + transaction.hash);
                    System.out.println("\tProgress: " + transaction.progress);
                }
            }
        });
        snitcoin.run();
        //((Button) findViewById(R.id.button_send)).setOnClickListener(new SendActionListener());
    }
}
