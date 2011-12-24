package net.tatablack.ci.informer.listeners;

import hudson.Extension;
import hudson.model.RestartListener;

import java.io.IOException;

import net.tatablack.ci.informer.server.WSServer;

/**
 * @author Angelo Tata
 */
@Extension
public class CiServerRestartListener extends RestartListener {
    /* (non-Javadoc)
     * @see hudson.model.RestartListener#isReadyToRestart()
     */
    @Override
    public boolean isReadyToRestart() throws IOException, InterruptedException {
        WSServer.stop(WSServer.MESSAGE.RESTART);
        return true;
    }
}
