package snitcoin.sneer.me.snitcoin;

import android.app.Activity;
import android.content.Context;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.kits.WalletAppKit;


public class SnitcoinAppKit{
    private WalletAppKit kit;
    private Activity activity;
    private Context context;
    private NetworkParameters params;

    public SnitcoinAppKit(final Activity activity, Context context, NetworkParameters params) {
        this.activity = activity;
        this.context = context;
        this.params = params;

        kit = new WalletAppKit(params, context.getFilesDir(), "snitcoin-wallet-2");
        kit.setAutoSave(true);
        kit.startAsync();
        kit.awaitRunning();
    }

    public Wallet wallet(){
        return kit.wallet();
    }

    public Wallet.SendResult send(Coin coin, String address) throws AddressFormatException, InsufficientMoneyException, Wallet.DustySendRequested{
        Wallet.SendResult result = kit.wallet().sendCoins(kit.peerGroup(), (new Address(params, address)), coin);
        return result;
    }
}
