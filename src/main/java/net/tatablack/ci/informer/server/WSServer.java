/**
 *
 */
package net.tatablack.ci.informer.server;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
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
    private static CopyOnWriteArrayList<WebSocketConnection> connections = new CopyOnWriteArrayList<WebSocketConnection>();
    private static WebServer instance = null;

    public static enum MESSAGE {
        RESTART, SHUTDOWN
    }

    public static final String INFORMER_WEBSOCKET_PATH = "/ci/informer";

    synchronized public static void start(int port) {
        try {
            WSServer.instance = WebServers.createWebServer(port)
                                .add(WSServer.INFORMER_WEBSOCKET_PATH, new WSHandler())
                                .start();

            logger.info("WebSocket server now listening on port " + port);
        } catch (IOException ioEx){
            logger.severe("Error while trying to start the Informer WebSocket server on port " + port + ": " + ioEx.getMessage());
            throw new RuntimeException(ioEx);
        }
    }

    synchronized public static void stop(WSServer.MESSAGE message) {
        if (WSServer.instance != null) {
            WSServer.broadcast(message.toString());
            WSServer.closeConnections();

            try {
                WSServer.instance.stop();
                logger.info("WebSocket server stopped");
            } catch (IOException ioEx) {
                logger.severe("Error while trying to stop the Informer WebSocket server: " + ioEx.getMessage());
            }
        }
    }

    synchronized public static void closeConnections() {
        for (WebSocketConnection connection : connections){
            connection.close();
        }

        connections.clear();
    }


    synchronized public static void broadcast(String message) {
        if(CiServerObject.EMPTY_RESULT.equals(message)) return;

        logger.finest("Trying to broadcast: '" + message + "' on " + WSServer.connections.size() + " connections");

        for(WebSocketConnection connection : WSServer.connections) {
            connection.send(message);
        }
    }

    synchronized public static void addConnection(WebSocketConnection connection) {
        WSServer.connections.add(connection);
    }

    synchronized public static void removeConnection(WebSocketConnection connection) {
        int connectionIndex = WSServer.connections.indexOf(connection);

        if (connectionIndex!=-1) {
            logger.finer("A connection has been closed and it will be removed from the server list");
            WSServer.connections.remove(connectionIndex);
        }
    }
}
