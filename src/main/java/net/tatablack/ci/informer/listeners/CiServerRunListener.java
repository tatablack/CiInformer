package net.tatablack.ci.informer.listeners;

import hudson.Extension;
import hudson.model.TaskListener;
import hudson.model.AbstractBuild;
import hudson.model.listeners.RunListener;

import java.util.logging.Logger;

import net.tatablack.ci.informer.serialization.CiServerObject;
import net.tatablack.ci.informer.server.WSServer;

/**
 * @author Angelo Tata
 */
@Extension
public class CiServerRunListener extends RunListener<AbstractBuild> {
    private static final Logger logger = Logger.getLogger(CiServerRunListener.class.getName());

    @Override
    public void onStarted(AbstractBuild build, TaskListener listener) {
        if(build!=null) {
            logger.finest("Build for " + build.getFullDisplayName() + " started...");
            logger.finest(build.getProject().getRootProject().getName());

            WSServer.broadcast(CiServerObject.toJSON(build));
        }
    }

    @Override
    public void onFinalized(AbstractBuild build) {
        if(build!=null) {
            logger.finest("Build for " + build.getFullDisplayName() + " terminated.");
            WSServer.broadcast(CiServerObject.toJSON(build));
        }
    }
}
