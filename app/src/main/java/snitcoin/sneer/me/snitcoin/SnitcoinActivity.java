package snitcoin.sneer.me.snitcoin;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.log4j.Level;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.InsufficientMoneyException;

import de.mindpipe.android.logging.log4j.LogConfigurator;
import snitcoin.sneer.me.snitcoin_core.Listener;
import snitcoin.sneer.me.snitcoin_core.Snitcoin;
import snitcoin.sneer.me.snitcoin_core.Status;
import snitcoin.sneer.me.snitcoin_core.Transaction;


public class SnitcoinActivity extends ActionBarActivity {

    SnitcoinAppKit kit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogConfigurator logConfigurator = new LogConfigurator();
        logConfigurator.setRootLevel(Level.ALL);
        logConfigurator.setLevel("org.bitcoinj", Level.ALL);
        logConfigurator.setUseFileAppender(false);
        logConfigurator.setUseLogCatAppender(true);
        logConfigurator.setLogCatPattern("%d %-5p [%c{2}]-[%L] %m%n");
        logConfigurator.configure();


        setContentView(R.layout.activity_snitcoin);

        final Snitcoin snitcoin = new Snitcoin(getApplication().getFilesDir());
        snitcoin.setListener(new Listener() {
            public void onChange(final Status status) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("--------------------------------------------");
                        System.out.println("Message: " + status.message);
                        System.out.println("Receive Address: " + status.receiveAddress);

                        ((TextView) findViewById(R.id.balance)).setText(status.balance);
                        ((TextView) findViewById(R.id.address)).setText(status.receiveAddress);

                        TransactionArrayAdapter adapter = new TransactionArrayAdapter(getApplicationContext(),
                                status.transactions.toArray(new Transaction[status.transactions.size()]));

                        ((ListView) findViewById(R.id.list_transactions)).setAdapter(adapter);
                    }
                });
            }
        });

        runOnUiThread(snitcoin);
        ((Button) findViewById(R.id.button_send)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = ((TextView) findViewById(R.id.edit_text_amount)).getText().toString();
                String address = ((TextView) findViewById(R.id.edit_text_address)).getText().toString();
                try {
                    snitcoin.send(amount, address);
                } catch (AddressFormatException e) {
                    e.printStackTrace();
                } catch (InsufficientMoneyException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        ((Button) findViewById(R.id.button_fresh_receive_address)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String freshReceiveAddres = snitcoin.freshReceiveAddress();
                System.out.println(freshReceiveAddres);
            }
        });



        ((Button) findViewById(R.id.button_current_receive_address)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentReceiveAddres = snitcoin.currentReceiveAddress();
                System.out.println(currentReceiveAddres);
            }
        });
    }

    private class TransactionArrayAdapter extends ArrayAdapter<Transaction>{

        private final Context context;
        private final Transaction[] transactions;

        public TransactionArrayAdapter(Context context, Transaction[] transactions) {
            super(context, -1, transactions);
            this.context = context;
            this.transactions = transactions;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_view_transaction, parent, false);

            ((TextView) view.findViewById(R.id.text_transaction_progress)).setText(transactions[position].progress);
            ((TextView) view.findViewById(R.id.text_transaction_amount)).setText(transactions[position].amount);
            ((TextView) view.findViewById(R.id.text_transaction_fee)).setText(transactions[position].fee);
            ((TextView) view.findViewById(R.id.text_transaction_hash)).setText(transactions[position].hash);

            if(transactions[position].inputs.length > 1)
                ((TextView) view.findViewById(R.id.text_transaction_input_address)).setText("multiple");
            else
                ((TextView) view.findViewById(R.id.text_transaction_input_address)).setText(transactions[position].inputs[0]);

            if(transactions[position].outputs.length > 1)
                ((TextView) view.findViewById(R.id.text_transaction_output_address)).setText("multiple");
            else
                ((TextView) view.findViewById(R.id.text_transaction_output_address)).setText(transactions[position].outputs[0]);

            return view;
        }
    }
}
