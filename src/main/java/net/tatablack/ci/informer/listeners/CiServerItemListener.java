package net.tatablack.ci.informer.listeners;

import hudson.Extension;
import hudson.model.Item;
import hudson.model.listeners.ItemListener;

import java.util.logging.Logger;

import net.tatablack.ci.informer.serialization.CiServerObject;
import net.tatablack.ci.informer.server.WSServer;

/**
 * User: Angelo Tata
 * Date: 15/11/11
 * Time: 0.03
 */
@Extension
public class CiServerItemListener extends ItemListener {
    private static final Logger logger = Logger.getLogger(CiServerItemListener.class.getName());

    public void onCreated(Item item) {
        logger.info("Full Display Name: " + item.getFullDisplayName());
        logger.info("Url: " + item.getUrl());
        logger.info("Class: " + item.getClass().getName());

        WSServer.broadcast(CiServerObject.toJSON(item));
    }
}
