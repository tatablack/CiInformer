package net.tatablack.ci.informer.server;

import hudson.model.Hudson;

import java.util.logging.Logger;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.tatablack.ci.informer.serialization.CiServerObject;

import org.webbitserver.BaseWebSocketHandler;
import org.webbitserver.WebSocketConnection;

/**
 * @author Angelo Tata
 */
public class WSHandler extends BaseWebSocketHandler {
    private static final Logger logger = Logger.getLogger(WSHandler.class.getName());

    public void onOpen(WebSocketConnection connection) throws Exception {
        WSServer.addConnection(connection);

        logger.finer("Connection opened (protocol: " + connection.version() + ", viewNames: " + connection.data(WSConnection.VIEWNAMES).toString() + ")");
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

            // We assume that the only message sent by clients
            // will be to require a view.
            wsMessage.setResponse(CiServerObject.toJSON(Hudson.getInstance().getView(jsonMessageData.getString("viewName"))));
            
            response = JSONObject.fromObject(wsMessage).toString();
        } catch (JSONException jsonEx) {
            logger.severe(jsonEx.getMessage());
        }
        
        logger.finest(response);
        connection.send(response);
    }
}
