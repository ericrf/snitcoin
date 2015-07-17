package snitcoin.sneer.me.snitcoin;

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
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.utils.MonetaryFormat;


public class SnitcoinActivity extends ActionBarActivity {

    SnitcoinAppKit kit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snitcoin);

        kit = new SnitcoinAppKit(this, getApplicationContext(), TestNet3Params.get());
        kit.wallet().addEventListener(new WalletEventListener().invoke());
        Log.d("Current Receive Address", kit.wallet().currentReceiveAddress().toString());
        ((TextView) findViewById(R.id.address)).setText(kit.wallet().currentReceiveAddress().toString());
        ((TextView) findViewById(R.id.balance)).setText(format(kit.wallet().getBalance()));
        ((Button) findViewById(R.id.button_send)).setOnClickListener(new SendActionListener().invoke());
    }

    public String format(Monetary monetary){
        return MonetaryFormat.BTC.format(monetary).toString();
    }

    public class WalletEventListener {
        public AbstractWalletEventListener invoke() {
            return new AbstractWalletEventListener() {
                @Override
                public void onCoinsReceived(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
                    ((TextView) findViewById(R.id.balance)).setText(format(wallet.getBalance()));
                    String title = "Coins Received";
                    String text = getNotifyText(wallet, tx, prevBalance, newBalance);
                    Log.d(title, text);
                }

                @Override
                public void onCoinsSent(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
                    ((TextView) findViewById(R.id.balance)).setText(format(wallet.getBalance()));
                    String title = "Coins Sent";
                    String text = getNotifyText(wallet, tx, prevBalance, newBalance);
                    Log.d(title, text);
                }

                @Override
                public void onWalletChanged(Wallet wallet) {
                    ((TextView) findViewById(R.id.balance)).setText(format(wallet.getBalance()));
                    String title = "Wallet chaged!";
                    String text = "Current Balance:" + format(wallet.getBalance());
                    Log.d(title, text);
                }
            };
        }

        private String  getNotifyText(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
            StringBuilder sb = new StringBuilder();
            sb.append("Transaction: " + tx.getHashAsString()).append("\n");
            sb.append("Received Amount: " + format(tx.getValue(wallet))).append("\n");
            sb.append("Prev Balance: " + format(prevBalance)).append("\n");
            sb.append("New Balance: " + format(newBalance)).append("\n");
            return sb.toString();
        }
    }

    private class SendActionListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String address = ((EditText) findViewById(R.id.edit_text_address)).getText().toString();
            String amount = ((EditText) findViewById(R.id.edit_text_amount)).getText().toString();
            Coin value = Coin.parseCoin(amount);

            try {
                Wallet.SendResult result = kit.send(value, address);

            } catch (AddressFormatException e) {
                e.printStackTrace();

            } catch (InsufficientMoneyException e) {
                Toast.makeText(getApplicationContext(), "Not enough coins in your wallet. Missing " + e.missing.getValue() + " satoshis are missing (including fees)", Toast.LENGTH_LONG).show();
                ListenableFuture<Coin> balanceFuture = kit.wallet().getBalanceFuture(value, Wallet.BalanceType.AVAILABLE);
                FutureCallback<Coin> callback = new InsufficientMoneyFutureCallback();
                Futures.addCallback(balanceFuture, callback);
            } catch (Wallet.DustySendRequested e) {
                Toast.makeText(getApplicationContext(), "Too small amount", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class InsufficientMoneyFutureCallback implements FutureCallback<Coin> {
        public void onSuccess(Coin result) {
            Toast.makeText(getApplicationContext(), "coins arrived and the wallet now has enough balance", Toast.LENGTH_LONG).show();
        }

        public void onFailure(Throwable t) {
            Toast.makeText(getApplicationContext(), "something went wrong", Toast.LENGTH_LONG).show();
        }
    }

}
