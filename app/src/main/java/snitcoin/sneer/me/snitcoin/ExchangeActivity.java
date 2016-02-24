package snitcoin.sneer.me.snitcoin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

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
        setContentView(R.layout.teste_rest);




//        inflater = getLayoutInflater();
//        session = PartnerSession.join(this, new PartnerSession.Listener() {
//            @Override
//            public void onUpToDate() {
//                refresh();
//            }
//
//            @Override
//            public void onMessage(Message message) {
//                handle(message);
//            }
//        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        new BlockChainExchangeRate().execute("BRL", 200);
    }

    private class BlockChainExchangeRate extends AsyncTask<String, Void, String>{


        @Override
        protected String doInBackground(String... params) {
            try {
                final String url = "https://blockchain.info/tobtc?currency=USD&value=500";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                String btc = restTemplate.getForObject(url, String.class);
                return btc;
            } catch (Exception e) {
                Log.e("ExchangeActivity", e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String btc) {
            ((TextView) findViewById(R.id.btc)).setText(btc);
        }

        public void execute(String brl, int i) {
            super.execute();
        }
    }



    private void handle(Message message){
        if(message.wasSentByMe()){
            if(requestDialog == null)
                requestDialog = createRequestDialog();
        }else{
            if(requestDialog == null)
                requestReceivedDialog = createRequestReceivedDialog();
        }
        refresh();
    }

    private void refresh() {
        if (requestDialog == null && requestReceivedDialog == null) requestDialog = createRequestDialog();
        if (requestDialog != null) requestDialog.show();
        if (requestReceivedDialog != null) requestReceivedDialog.show();
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

    private void setNegativeButton(AlertDialog.Builder builder) {
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                restart();
                finish();
            }
        });
    }

    private void setupButtonChangeCurrency(View view) {
        view.findViewById(R.id.button_change_currency).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
            }
        });
    }

    private void restart() {
        requestDialog = null;
        requestReceivedDialog = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        session.close();
    }


}
