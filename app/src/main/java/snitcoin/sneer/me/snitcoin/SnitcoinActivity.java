package snitcoin.sneer.me.snitcoin;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.InsufficientMoneyException;

import java.util.Calendar;

import snitcoin.sneer.me.snitcoin_core.Listener;
import snitcoin.sneer.me.snitcoin_core.Snitcoin;
import snitcoin.sneer.me.snitcoin_core.Status;
import snitcoin.sneer.me.snitcoin_core.Transaction;


public class SnitcoinActivity extends ActionBarActivity {

    private ProgressBar progressBar;
    private int progressStatus = 0;
    private TextView textView;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snitcoin);
        ((ProgressBar) findViewById(R.id.progress_bar)).setIndeterminate(true);

        Logger.getRootLogger().setLevel(Level.ALL);
        Logger.getRootLogger().addAppender(new LoggerAppender());
        Logger.getRootLogger().addAppender(new ProgressAppender());
        Logger.getRootLogger().addAppender(new MyConsoleAppender());

        final Snitcoin snitcoin = new Snitcoin(getApplication().getFilesDir());
        snitcoin.setListener(new Listener() {
            public void onChange(final Status status) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Logger.getRootLogger().warn(status.message + " : " + status.receiveAddress);
                        ((TextView) findViewById(R.id.balance)).setText(status.balance);
                        ((TextView) findViewById(R.id.address)).setText(status.receiveAddress);

                        TransactionArrayAdapter adapter = new TransactionArrayAdapter(getApplicationContext(),
                                status.transactions.toArray(new Transaction[status.transactions.size()]));

                        ((ListView) findViewById(R.id.list_transactions)).setAdapter(adapter);
                    }
                });
            }
        });
        new Thread(snitcoin).start();

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
                Logger.getRootLogger().debug("freshReceiveAddress()" + snitcoin.freshReceiveAddress());
            }
        });


        ((Button) findViewById(R.id.button_current_receive_address)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.getRootLogger().debug("currentReceiveAddress()" + snitcoin.currentReceiveAddress());
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

    public class ProgressAppender extends MyAppenderSkeleton{

        @Override
        protected void append(final LoggingEvent event) {
            runOnUiThread(new Runnable() {
                ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
                public void run() {
                    if(event.categoryName.toString().equals("root")){
                        String message = event.getMessage().toString();
                        if(message.equals("Starting..."))
                            progressBar.setVisibility(View.VISIBLE);
                        if(message.equals("Started!"))
                            progressBar.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
    }

    public class LoggerAppender extends MyAppenderSkeleton{

        @Override
        protected void append(final LoggingEvent event) {
            runOnUiThread(new Runnable() {
                TextView logger = (TextView) findViewById(R.id.logger);
                public void run() {
                    Level level = event.getLevel();
                    String categoryName = event.categoryName.toString();

                    if(level != Level.DEBUG
                            && level != Level.ERROR
                            && !categoryName.equals("root")
                            ) {
                        String time = Calendar.getInstance().getTime().toString();
                        logger.setText( time +" " +event.getLevel().toString() + " - " + event.getMessage() + " - " + event.categoryName +"\n" + logger.getText());
                    }
                }
            });
        }
    }



    public class MyConsoleAppender extends MyAppenderSkeleton {

        @Override
        protected void append(LoggingEvent event) {
            String categoryName = event.categoryName.toString();
            StringBuilder builder = new StringBuilder(event.getLevel().toString())
                    .append(" - ").append(categoryName)
                    .append(" - ").append(event.getMessage())
                    ;

            if(!categoryName.equals("org.bitcoinj.core.BitcoinSerializer")
                    && !categoryName.equals("org.bitcoinj.core.Peer")
                    && !categoryName.equals("org.bitcoinj.wallet.DeterministicKeyChain"))
                //org.bitcoinj.wallet.DeterministicKeyChain
                System.out.println(builder.toString());
        }
    }

    private abstract class MyAppenderSkeleton extends AppenderSkeleton{

        @Override
        public synchronized void doAppend(LoggingEvent event) {
            this.append(event);
        }

        @Override
        public void close() {}

        @Override
        public boolean requiresLayout() { return false; }
    }



}
