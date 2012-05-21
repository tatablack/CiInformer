/**
 *
 */
package net.tatablack.ci.informer.server;

import java.io.IOException;
import java.util.concurrent.*;
import java.util.logging.Logger;

import net.tatablack.ci.informer.serialization.CiServerObject;

import org.webbitserver.WebServer;
import org.webbitserver.WebServers;
import org.webbitserver.WebSocketConnection;

/**
 * @author Angelo Tata
 */
public class WSServer {
    private static final Logger logger = Logger.getLogger(CiServerObject.class.getName());
    private static CopyOnWriteArrayList<WSConnection> connections = new CopyOnWriteArrayList<WSConnection>();
    private static WebServer instance = null;
    private static WSExceptionHandler exceptionHandler = new WSExceptionHandler();

    public static enum MESSAGE {
        RESTART, SHUTDOWN
    }

    public static final String INFORMER_WEBSOCKET_PATH = "/ci/informer";

    synchronized public static void start(int port) {
        WSServer.instance = WebServers.createWebServer(port)
                            .add(WSServer.INFORMER_WEBSOCKET_PATH, new WSHandler())
                            .connectionExceptionHandler(exceptionHandler)
                            .uncaughtExceptionHandler(exceptionHandler);

        logger.info("WebSocket server starting on port " + port);
        WSServer.instance.start();
    }

    synchronized public static void stop(WSServer.MESSAGE message) {
        if (WSServer.instance != null) {
            WSServer.broadcast(message.toString());
            WSServer.closeConnections();

            WSServer.instance.stop();
            logger.info("WebSocket server stopping...");
        }
    }

    synchronized public static void closeConnections() {
        for (WSConnection connection : connections){
            connection.close();
        }

        connections.clear();
    }


    synchronized public static void broadcast(String message) {
        if(CiServerObject.EMPTY_RESULT.equals(message)) return;

        logger.finest("Trying to broadcast: '" + message + "' on " + WSServer.connections.size() + " connections");

        for (WSConnection connection : WSServer.connections) {
            //if (connection.isInterestedIn(jobDisplayName)) connection.send(message);
            connection.send(message);
        }
    }

    synchronized public static void addConnection(WebSocketConnection connection) {
        WSServer.connections.add(new WSConnection(connection));
    }

    synchronized public static void removeConnection(WebSocketConnection connection) {
        int connectionIndex = WSServer.connections.indexOf(WSServer.getConnection(connection));

        if (connectionIndex != -1) {
            logger.finer("A connection has been closed and it will be removed from the server list");
            WSServer.connections.remove(connectionIndex);
        }
    }

    synchronized public static WSConnection getConnection(WebSocketConnection connection) {
        WSConnection result = null;

        for (WSConnection wsConnection : connections) {
            if (wsConnection.getConnection().equals(connection)) {
                result = wsConnection;
                break;
            }
        }

        return result;
    }
}
