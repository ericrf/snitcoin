package snitcoin.sneer.me.snitcoin_core;

import com.google.common.util.concurrent.SettableFuture;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.DownloadProgressTracker;
import org.bitcoinj.core.FilteredBlock;
import org.bitcoinj.core.Peer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

/**
 * Created by Eric on 01/10/2015.
 */
public class SnitcoinDownloadProgressTracker extends DownloadProgressTracker {
    private final Logger log = LoggerFactory.getLogger(SnitcoinDownloadProgressTracker.class);
    private int originalBlocksLeft = -1;
    private int lastPercent = 0;
    private SettableFuture<Long> future = SettableFuture.create();
    private boolean caughtUp = false;

    @Override
    public void onChainDownloadStarted(Peer peer, int blocksLeft) {
        super.onChainDownloadStarted(peer, blocksLeft);
        log.info("SnitcoinDownloadProgressTracker - Starting blockchain download " + blocksLeft);
    }

    @Override
    public void onBlocksDownloaded(Peer peer, Block block, @Nullable FilteredBlock filteredBlock, int blocksLeft) {
        super.onBlocksDownloaded(peer, block, filteredBlock, blocksLeft);
        double pct = 100.0 - (100.0 * (blocksLeft / (double) originalBlocksLeft));
        if ((int) pct != lastPercent) {
            log.info("SnitcoinDownloadProgressTracker " + lastPercent + " " + blocksLeft);
            lastPercent = (int) pct;
        }
    }

    protected void doneDownload() {
        log.info("SnitcoinDownloadProgressTracker - Blockchain download done!");
    }
}
