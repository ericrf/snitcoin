package snitcoin.sneer.me.snitcoin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import sneer.android.Message;
import sneer.android.PartnerSession;


public class ExchangeActivity extends Activity {

    private AlertDialog bitcoinRequestDialog;
    private AlertDialog send;

    private AlertDialog menu;


    PartnerSession session;

    boolean isRequest;
    private boolean isSending;
    private boolean isRequestAccepted;
    private boolean isReceivingAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.bitcoin_request, null);
        view.findViewById(R.id.button_change_currency).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Bitcoin request");
        builder.setView(view);
        builder.setPositiveButton("Request", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        bitcoinRequestDialog = builder.create();
        //bitcoinRequestDialog.show();

        view = inflater.inflate(R.layout.bitcoin_request, null);
        view.findViewById(R.id.button_change_currency).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
            }
        });


        builder = new AlertDialog.Builder(this);
        builder.setTitle("Bitcoin request");
        builder.setView(view);
        builder.setPositiveButton("Request", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        bitcoinRequestDialog = builder.create();


    }

    private void refresh() {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //session.close();
    }
}
