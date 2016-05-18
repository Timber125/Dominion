/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import org.json.JSONObject;

/**
 *
 * @author admin
 */
public class ConnectionHandlerPrototype extends AbstractConnectionHandler implements Runnable, InvalidationListener{

    public Socket client;
    public ConnectionListener client_in;
    private Thread listen;
    
    public ConnectionHandlerPrototype(Server server, Socket client) {
        super(server);
        this.client = client;
    }
   

    @Override
    public void InitiateConnection() {
        Thread t = new Thread(this);
        t.start();
    }

    @Override
    public void write(String s) {
        // Pass to real connection handler before writing
    }

    @Override
    public void write(JSONObject json) {
        // Pass to real connection handler before writing
    }

    @Override
    public void cleanUp() {
      
        client_in.removeListener(this);
        
    }

    @Override
    public int connectionState() {
        return 0;
    }

    @Override
    public void run() {
        try {
            startListener(new BufferedReader(new InputStreamReader(client.getInputStream())));
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    private void startListener(BufferedReader in) {
        client_in = new JSonListener(in);
        client_in.addListener(this);
        listen = new Thread(client_in);
        listen.start();
    }

    @Override
    public void invalidated(Observable o) {
        String inputreceived = client_in.buffer.poll();
        System.out.println("Connection handshake received: " + inputreceived + "");
        if(inputreceived.equals("{client:desktop}")){
            server.setConnectionType(this, "DesktopClient");
        }else if(inputreceived.equals("{client:web}")){
            server.setConnectionType((this), "WebClient");
        }
    }
    
}
