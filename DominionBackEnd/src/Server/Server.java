/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Server;

import Server.Service.ChatService;
import Server.Service.DominionService;
import Server.Service.LobbyService;
import Server.Service.ServiceBroker;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;


/**
 *
 * @author admin
 */
public class Server implements Runnable{
    
    private boolean active = false;
    
    private ServerSocket serverSocket;
    private final String ip;
    private int port;
    
    
    // Thread-race on clients; SYNCHRONIZE before access!
    public ArrayList<ConnectionHandler> clients;
    
    public synchronized boolean isActive(){
        return active;
    }
    
    public Server(String ip, int port){
        this.ip = ip;
        this.port = port;
        clients = new ArrayList<>();
    }
    
    
    
    public void setPort(int new_port){
        this.port = new_port;
    }
    
    protected void notifyClose(){
        synchronized(clients){
            ArrayList<ConnectionHandler> updated_clients = new ArrayList<>();
            HashMap<String, String> disconnected_sessions = new HashMap<>();
            for(ConnectionHandler ch : clients){
                // Null = hard disconnect, Closed = soft disconnect
                if((ch.client != null) && (!ch.client.isClosed())) updated_clients.add(ch);
                else{
                    disconnected_sessions.put(ch.my_session_token, ch.my_nickname);
                }
            }
            
            for(String s : disconnected_sessions.keySet()){
                JSONObject obj = JSONUtilities.JSON.create("action", "sysout");
                obj = JSONUtilities.JSON.addKeyValuePair("sysout", "Client [" + disconnected_sessions.get(s) + "] disconnected.", obj);
                for(ConnectionHandler ch : updated_clients){
                    ch.write(obj.toString());
                }
                JSONObject lobbyDisconnect = JSONUtilities.JSON.create("service_type", "lobby");
                lobbyDisconnect = JSONUtilities.JSON.addKeyValuePair("session", s, lobbyDisconnect);
                lobbyDisconnect = JSONUtilities.JSON.addKeyValuePair("author", disconnected_sessions.get(s), lobbyDisconnect);
                lobbyDisconnect = JSONUtilities.JSON.addKeyValuePair("operation", "disconnect", lobbyDisconnect);
                ServiceBroker.instance.offerRequest(lobbyDisconnect.toString());
                
            }
            this.clients = updated_clients;
            System.err.println("Close notified: cleaned up [" + disconnected_sessions.size() + "] disconnects.");
        }
    }
    
    public void shutdown(){
        active = false;
        // Give all connections a warning that server is shutting down. 
        JSONObject obj = JSONUtilities.JSON.create("action", "sysout");
        obj = JSONUtilities.JSON.addKeyValuePair("sysout", "Server shutting down. You will be disconnected.", obj);
        
        for(ConnectionHandler ch : clients){
            ch.write(obj);
        }
        
        try {
            // Give myself a connection to catch shutdown signal
            Socket s = new Socket("localhost", 13337);
        } catch (IOException ex) {
            System.err.println("Server was down before shutdown signal!!!");
        } finally {
            System.err.println("Server shutdown signal sent");
        }
        
        for(ConnectionHandler ch : clients){
            ch.cleanUp();
        }
        
        try {
            serverSocket.close();
        } catch (IOException ex) {
            System.err.println("Cant close serversocket!");
        }
    }
    
    public void exit(){
        System.exit(0);
    }
    
    public static void main(String[] args){
        Server server = new Server("localhost", 13337);
        Thread t = new Thread(server);
        t.start();
    }

    @Override
    public void run() {
        active = true;
        ServiceBroker.instance.addService(new ChatService(this));
        DominionService ds = new DominionService(this);
        ServiceBroker.instance.addService(ds);
        ServiceBroker.instance.addService(new LobbyService(this,ds));
        ServiceBroker.instance.start();
        try {
            serverSocket = new ServerSocket(port);
            while(active){
                Socket client_connecting = serverSocket.accept();
                if(active){ // Catch shutdown signals, don't add them to clients.
                    ConnectionHandler connection = new ConnectionHandler(this, client_connecting);
                    connection.InitiateConnection();
                    JSONObject obj = JSONUtilities.JSON.create("action", "sysout");
                    obj = JSONUtilities.JSON.addKeyValuePair("sysout", "guest connected from " + client_connecting.getInetAddress().getHostAddress(), obj);
                    synchronized(clients){
                        for(ConnectionHandler ch : clients){
                            ch.write(obj);
                        }
                        clients.add(connection);
                    }
                }
            }
        } catch (IOException ex) {
            System.err.println("Server could not start: ");
            System.err.println(ex);
            System.err.flush();
        }
        ServiceBroker.instance.shutdown();
    }

    public ConnectionHandler getClient(String session){
        for(ConnectionHandler ch : clients){
            if(ch.validSession(JSONUtilities.JSON.create("session", session))) return ch;
        }
        return null;
    }
    
    public String getNickname(String session){
        return getClient(session).getNickname();
    }
    
    public void sendAll(JSONObject packet){
        for(ConnectionHandler ch : clients){
            ch.write(packet);
        }
    }
    
    public void sendOne(JSONObject packet, String session){
        for(ConnectionHandler ch : clients){
            if(ch.validSession(JSONUtilities.JSON.create("session",session))) ch.write(packet);
        }
    }
    
    public void sendAllExcept(String session, JSONObject packet){
        for(ConnectionHandler ch : clients){
            if(!ch.validSession(JSONUtilities.JSON.create("session",session))){
                ch.write(packet);
            }
        }
    }
}
