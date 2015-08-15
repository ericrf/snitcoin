package snitcoin.sneer.me.snitcoin;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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

        Snitcoin snitcoin = new Snitcoin(getApplication().getFilesDir());
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

                        System.out.println("Transactions: ");
                        for (Transaction transaction : status.transactions) {
                            ((TextView) findViewById(R.id.balance)).setText(transaction.amount);
                            System.out.println("\tTransaction hash: " + transaction.hash);
                            System.out.println("\tProgress: " + transaction.progress);
                        }
                    }
                });
            }
        });

        runOnUiThread(snitcoin);
        //((Button) findViewById(R.id.button_send)).setOnClickListener(new SendActionListener());
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
            ((TextView) view.findViewById(R.id.text_transaction_hash)).setText(transactions[position].hash);

            //TODO: create listener to open transaction detail's intent sending current transaction as param

            return view;
        }
    }
}
