package snitcoin.sneer.me.snitcoin;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.TestNet3Params;


public class SnitcoinActivity extends ActionBarActivity {

    SnitcoinAppKit kit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snitcoin);

        kit = new SnitcoinAppKit(this, getApplicationContext(), TestNet3Params.get());
        ((Button) findViewById(R.id.button_send)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = ((EditText) findViewById(R.id.edit_text_address)).getText().toString();
                String amount = ((EditText) findViewById(R.id.edit_text_amount)).getText().toString();

                try {
                    kit.send(amount,address);
                } catch (AddressFormatException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
