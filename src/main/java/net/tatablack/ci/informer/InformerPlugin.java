package net.tatablack.ci.informer;

import hudson.init.InitMilestone;
import hudson.init.Initializer;

import java.util.logging.Logger;

import net.tatablack.ci.informer.server.WSServer;

/**
 * @author Angelo
 *
 */
public class InformerPlugin {
    private static final Logger logger = Logger.getLogger(InformerPlugin.class.getName());

    @Initializer(before = InitMilestone.COMPLETED, fatal = false)
    public static void init() {
        logger.info("Informer Plugin starting...");
        WSServer.start(10011);
        InformerPlugin.ensureShutdown();
    }

    private static void ensureShutdown() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                logger.info("Informer Plugin stopping...");
                System.out.println("Informer Plugin stopping...");
                WSServer.stop(WSServer.MESSAGE.SHUTDOWN);
            };
        });
    }
}
