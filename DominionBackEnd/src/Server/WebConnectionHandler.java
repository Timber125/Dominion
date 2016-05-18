/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Server;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

/**
 *
 * @author admin
 */
public class WebConnectionHandler extends AbstractConnectionHandler{
    
    public WebSocket client;
    public boolean initialized = false;
    
    public WebConnectionHandler(Server server, WebSocket client) throws UnknownHostException {
        super(server);
        initialize(client);
    }

    private void initialize(WebSocket client) throws UnknownHostException{
        this.client = client;
    }
    
    @Override
    public void InitiateConnection() {
        Runnable r = new Runnable(){

            @Override
            public void run() {
                    while(!client.getReadyState().equals(WebSocket.READYSTATE.values()[2])){
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(WebConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    client.send("{\"secret_session_token\":\"" + my_session_token + "\"}");
                    initialized = true;
            }
            
        };
        Thread t = new Thread(r);
        t.start();
    }

    @Override
    public void write(String s) {
        client.send(s);
    }

    @Override
    public void write(JSONObject json) {
        client.send(json.toString());
    }

    @Override
    public void cleanUp() {
        // Not needed?
    }

    @Override
    public int connectionState() {
        return 1;
    }

    
}
