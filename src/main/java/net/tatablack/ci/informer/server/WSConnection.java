package net.tatablack.ci.informer.server;

import hudson.model.Hudson;
import hudson.model.View;
import org.webbitserver.WebSocketConnection;

/**
 * User: Angelo Tata
 * Date: 20/05/12
 * Time: 9.33
 */
public class WSConnection {
    public static final String VIEWNAMES = "viewNames";
    private WebSocketConnection connection;

    public WSConnection(WebSocketConnection connection) {
        this.connection = connection;
        this.connection.data(WSConnection.VIEWNAMES, connection.httpRequest().queryParam(WSConnection.VIEWNAMES).split(","));
    }

    public String[] getViewNames() {
        return (String[]) this.connection.data(WSConnection.VIEWNAMES);
    }

    public boolean isInterestedIn(String jobDisplayName) {
        boolean result = false;
        String[] viewNames = this.getViewNames();

        for (String viewName : viewNames) {
            View view = Hudson.getInstance().getView(viewName);

            if (view != null && view.getJob(jobDisplayName) != null) {
                result = true;
                break;
            }
        }

        return result;
    }

    public WebSocketConnection getConnection() {
        return this.connection;
    }

    public void send(String message) {
        this.connection.send(message);
    }

    public void close() {
        this.connection.close();
    }
}
