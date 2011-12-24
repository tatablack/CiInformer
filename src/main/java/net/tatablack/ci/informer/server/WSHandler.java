package net.tatablack.ci.informer.server;

import java.util.logging.Logger;

import org.webbitserver.WebSocketConnection;
import org.webbitserver.WebSocketHandler;

/**
 * @author Angelo Tata
 */
public class WSHandler implements WebSocketHandler {
    private static final Logger logger = Logger.getLogger(WSHandler.class.getName());
    private static final String VIEWNAMES = "viewNames";

    public void onOpen(WebSocketConnection connection) throws Exception {
        WSServer.addConnection(connection);
        connection.data(WSHandler.VIEWNAMES, connection.httpRequest().queryParam(WSHandler.VIEWNAMES));

        connection.send(HudsonModel.getViews(
            ((String)connection.data(WSHandler.VIEWNAMES)).split(",")
        ));

        logger.finer("Connection opened (protocol: " + connection.version() + ", viewNames: " + connection.data(WSHandler.VIEWNAMES) + ")");
    }

    public void onClose(WebSocketConnection connection) throws Exception {
        WSServer.removeConnection(connection);
        logger.finer("Connection closed");
    }

    public void onMessage(WebSocketConnection connection, String msg) throws Throwable {

    }

    public void onMessage(WebSocketConnection connection, byte[] msg) throws Throwable {
    }

    public void onPong(WebSocketConnection connection, String msg) throws Throwable {
    }
}
