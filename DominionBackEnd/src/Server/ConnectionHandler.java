/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Server;

import Server.Service.ServiceBroker;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.InvalidationListener;
import org.json.JSONObject;

/**
 *
 * @author admin
 */
public class ConnectionHandler extends AbstractConnectionHandler implements Runnable, InvalidationListener{

    public Socket client;
    
    private PrintWriter client_out;
    private ConnectionListener client_in;
    
    
    public ConnectionHandler(Server server, Socket client, ConnectionListener client_in){
        super(server);
        this.client_in = client_in;
        initialize(client);
    }
    
    @Override
    public void InitiateConnection(){
        Thread t = new Thread(this);
        t.start();
    }
    
    @Override
    public void write(String s){
        client_out.println(s);
    }
    
    @Override
    public void write(JSONObject json){
        client_out.println(json.toString());
    }
    
    @Deprecated
    public String read(){
        return client_in.buffer.poll();
    }
    
    @Deprecated
    public boolean canRead(){
        return !client_in.buffer.isEmpty();
    }
    
    @Override
    public void run() {
        
        try {
            //startListener(new BufferedReader(new InputStreamReader(client.getInputStream())));
            startWriter(new PrintWriter(client.getOutputStream()));
            
            
        } catch (IOException ex) {
            System.err.println("Client out could not be initialized");
        }
        
    }

    public void cleanUp(){
        client_out.close();
        client_in.removeListener(this);
        try {
            client.close();
        } catch (IOException ex) {
            // Client is probably alreay closed. 
            // Not a problem.
            client = null;
        }
        server.notifyClose();
    }
    
    private void initialize(Socket client) {
        this.client = client;
        client_in.addListener(this);
        InetAddress clientAdress = client.getInetAddress();
        hostName = clientAdress.getHostName();
        hostAddress = clientAdress.getHostAddress();
        
        System.out.println("Client accepted: ");
        System.out.println("Hostname: " + hostName);
        System.out.println("Hostaddress: " + hostAddress);
        System.out.println("Session token: " + my_session_token);
        System.out.println("Nickname : " + my_nickname + " (will be changed in a few milliseconds)");
    }

    private void startWriter(PrintWriter out){
        client_out = new PrintWriter(out,true);
        String act = "sysout";
        JSONObject handshake = JSONUtilities.JSON.create(act, "Server accepted connection");
        handshake = JSONUtilities.JSON.addKeyValuePair("action", act, handshake);
        //handshake = JSONUtilities.JSON.addKeyValuePair("session", my_session_token, handshake);
        client_out.println(handshake.toString());
        JSONObject sessioninitializer = JSONUtilities.JSON.create("action", "session");
        sessioninitializer = JSONUtilities.JSON.addKeyValuePair("session", my_session_token, sessioninitializer);
        client_out.println(sessioninitializer.toString());
    }
    
    private void startListener(BufferedReader in){
        //client_in = new ConnectionListener(in);
        client_in = new JSonListener(in); // Only accepts valid json strings.
        client_in.addListener(this);
        Thread listen = new Thread(client_in);
        listen.start();
    }

    @Override
    public void invalidated(javafx.beans.Observable o) {
        // The retreiving of objects in buffer and storing them into the arraylist temporarily is not efficient
        // Remove later, but keep for now -> readability
        if(o == null){
            cleanUp();
            return;
        }
        ArrayList<String> messagelist = new ArrayList<>();
        while(!client_in.buffer.isEmpty()) messagelist.add(client_in.buffer.poll());
        for(String json_stringified : messagelist){
            if(validSession(json_stringified)){
                System.out.println("Client message: ");
                System.out.println("================");
                System.out.println(json_stringified);
                System.out.println("================");
                json_stringified = inject_client_information(json_stringified);
                ServiceBroker.instance.offerRequest(json_stringified);
            }else{
                System.out.println("Intercepted a crafted/invalid session token!");
            }
        }
        
    }

    @Override
    public int connectionState() {
        if(client == null) return -2;
        if(client.isClosed()) return -1;
        if(client.isConnected()) return 1;
        else return 0;
    }

}
// EMIEL VERSION