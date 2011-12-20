package net.tatablack.ci.informer.listeners;

import hudson.Extension;
import hudson.XmlFile;
import hudson.model.Saveable;
import hudson.model.Hudson;
import hudson.model.listeners.SaveableListener;

import java.util.logging.Logger;

import net.tatablack.ci.informer.serialization.CiServerObject;
import net.tatablack.ci.informer.server.HudsonModel;
import net.tatablack.ci.informer.server.WSServer;

/**
 * User: Angelo Tata
 * Date: 14/11/11
 * Time: 23.47
 */
@Extension
public class CiServerSaveableListener extends SaveableListener {
    private static final Logger logger = Logger.getLogger(CiServerSaveableListener.class.getName());

    public void onChange(Saveable saveable, XmlFile file) {
        logger.finest("Instance of " + saveable.getClass().getName() + " changed.");

        String changed = null;

        if(Hudson.class.equals(saveable.getClass())) {
            changed = HudsonModel.getViews(((Hudson)saveable).getViews());
        } else {
            changed = CiServerObject.toJSON(saveable);
        }

        WSServer.broadcast(changed);
    }
}
