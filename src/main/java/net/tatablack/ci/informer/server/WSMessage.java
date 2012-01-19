/**
 * 
 */
package net.tatablack.ci.informer.server;

/**
 * @author atata
 *
 */
public class WSMessage {
    private String clientId;
    private String method;
    private String response;
    
    public WSMessage(String clientId, String method) {
        this.clientId = clientId;
        this.method = method;
    }
    
    public String getClientId() {
        return clientId;
    }
    
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    
    public String getMethod() {
        return method;
    }
    
    public void setMethod(String method) {
        this.method = method;
    }
    
    public String getResponse() {
        return response;
    }
    
    public void setResponse(String response) {
        this.response = response;
    }
}
