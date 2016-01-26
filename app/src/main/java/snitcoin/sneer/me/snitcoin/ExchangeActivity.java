package snitcoin.sneer.me.snitcoin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import sneer.android.Message;
import sneer.android.PartnerSession;


public class ExchangeActivity extends Activity {

    LayoutInflater inflater;
    private AlertDialog requestDialog;
    private AlertDialog requestReceivedDialog;
    PartnerSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater = getLayoutInflater();

        session = PartnerSession.join(this, new PartnerSession.Listener() {  /////////////Sneer API call
            @Override
            public void onUpToDate() {
                refresh();
            }

            @Override
            public void onMessage(Message message) {
                handle(message);
            }
        });
    }

    private AlertDialog createRequestReceivedDialog() {
        View view = inflater.inflate(R.layout.bitcoin_request_received, null);
        setupButtonChangeCurrency(view);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Bitcoin request received");
        builder.setView(view);
        setNegativeButton(builder);
        return builder.create();
    }

    private void setNegativeButton(AlertDialog.Builder builder) {
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                restart();
                finish();
            }
        });
    }

    private void restart() {
        requestDialog = null;
        requestReceivedDialog = null;
    }

    private AlertDialog createRequestDialog() {
        View view = inflater.inflate(R.layout.bitcoin_request, null);
        setupButtonChangeCurrency(view);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Bitcoin request");
        builder.setView(view);
        builder.setPositiveButton("Request", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        setNegativeButton(builder);
        return builder.create();
    }

    private void setupButtonChangeCurrency(View view) {
        view.findViewById(R.id.button_change_currency).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
            }
        });
    }

    private void refresh() {

    }

    private void handle(Message message){
        
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //session.close();
    }
}
