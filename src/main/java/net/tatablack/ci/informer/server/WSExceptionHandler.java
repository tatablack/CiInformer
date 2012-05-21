package net.tatablack.ci.informer.server;

import java.util.logging.Logger;

/**
 * User: Angelo Tata
 * Date: 19/05/12
 * Time: 12.47
 */
public class WSExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static final Logger logger = Logger.getLogger(WSExceptionHandler.class.getName());

    public void uncaughtException(Thread t, Throwable e) {
        logger.severe("Bad things happen: " + e.getMessage());
    }
}

