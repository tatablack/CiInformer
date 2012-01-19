package net.tatablack.ci.informer.server;

import hudson.model.Hudson;

import java.util.logging.Logger;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.tatablack.ci.informer.serialization.CiServerObject;

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

        logger.finer("Connection opened (protocol: " + connection.version() + ", viewNames: " + connection.data(WSHandler.VIEWNAMES) + ")");
    }

    public void onClose(WebSocketConnection connection) throws Exception {
        WSServer.removeConnection(connection);
        logger.finer("Connection closed");
    }

    public void onMessage(WebSocketConnection connection, String message) throws Throwable {
        String response = "";
        logger.finest(message);
        
        try {
            JSONObject jsonMessage = JSONObject.fromObject(message);
            JSONObject jsonMessageData = jsonMessage.getJSONObject("data");
            
            WSMessage wsMessage = new WSMessage(jsonMessage.getString("clientId"), jsonMessage.getString("method"));
            wsMessage.setResponse(CiServerObject.toJSON(Hudson.getInstance().getView(jsonMessageData.getString("viewName"))));
            
            response = JSONObject.fromObject(wsMessage).toString();
        } catch (JSONException jsonEx) {
            logger.severe(jsonEx.getMessage());
        }
        
        logger.finest(response);
        connection.send(response);
    }

    public void onMessage(WebSocketConnection connection, byte[] msg) throws Throwable {
    }

    public void onPong(WebSocketConnection connection, String msg) throws Throwable {
    }
}
