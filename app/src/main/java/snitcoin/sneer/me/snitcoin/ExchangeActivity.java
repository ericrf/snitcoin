package snitcoin.sneer.me.snitcoin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import sneer.android.PartnerSession;


public class ExchangeActivity extends Activity {

    private AlertDialog requestDialog;
    private AlertDialog requestReceivedDialog;

    PartnerSession session;

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
        requestDialog = builder.create();
        //requestDialog.show();

        view = inflater.inflate(R.layout.bitcoin_request_received, null);
        view.findViewById(R.id.button_change_currency).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
            }
        });


        builder = new AlertDialog.Builder(this);
        builder.setTitle("Bitcoin request received");
        builder.setView(view);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        requestDialog = builder.create();
        requestDialog.show();


    }

    private void refresh() {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //session.close();
    }
}
