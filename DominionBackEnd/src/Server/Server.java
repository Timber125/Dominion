/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Server;

import Server.Service.ChatService;
import Server.Service.DominionService;
import Server.Service.JDBCMemoryDatabaseService;
import Server.Service.LobbyService;
import Server.Service.ServiceBroker;
import Server.Service.WampMYSQLDatabaseService;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;


/**
 *
 * @author admin
 */
public class Server implements Runnable{
    
    private boolean active = false;
    
    private ServerSocket serverSocket;
    private WebSocketServer wss;
    private final String ip;
    private int port;
    private int webport;
    
    
    // Thread-race on clients; SYNCHRONIZE before access!
    public ArrayList<AbstractConnectionHandler> clients;
    
    public synchronized boolean isActive(){
        return active;
    }
    
    public Server(String ip, int port, int webport){
        this.ip = ip;
        this.port = port;
        this.webport = webport;
        clients = new ArrayList<>();
    }
    
    
    
    public void setPort(int new_port){
        this.port = new_port;
    }
    
    protected void notifyClose(){
        synchronized(clients){
            ArrayList<AbstractConnectionHandler> updated_clients = new ArrayList<>();
            HashMap<String, String> disconnected_sessions = new HashMap<>();
            for(AbstractConnectionHandler ch : clients){
                // -2 = hard disconnect, -1 = soft disconnect
                if(ch.connectionState() >= 0) updated_clients.add(ch);
                else{
                    disconnected_sessions.put(ch.my_session_token, ch.my_nickname);
                }
            }
            
            for(String s : disconnected_sessions.keySet()){
                JSONObject obj = JSONUtilities.JSON.make_clients_notify_disconnect(disconnected_sessions.get(s));
                for(AbstractConnectionHandler ch : updated_clients){
                    ch.write(obj.toString());
                }
                JSONObject lobbyDisconnect = JSONUtilities.JSON.make_server_disconnect_client_from_lobby(s, disconnected_sessions.get(s));
                ServiceBroker.instance.offerRequest(lobbyDisconnect.toString());
                
            }
            this.clients = updated_clients;
            System.err.println("Close notified: cleaned up [" + disconnected_sessions.size() + "] disconnects.");
        }
    }
    
    public void shutdown(){
        active = false;
        // Give all connections a warning that server is shutting down. 
        JSONObject obj = JSONUtilities.JSON.notify_shutdown();
        for(AbstractConnectionHandler ch : clients){
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
        
        for(AbstractConnectionHandler ch : clients){
            ch.cleanUp();
        }
        
        try {
            serverSocket.close();
            wss.stop();
        } catch (IOException ex) {
            System.err.println("Cant close main server: " + ex);
        } catch (InterruptedException ex) {
            System.err.println("Cant close webserver: " + ex);
        }
    }
    
    public void exit(){
        System.exit(0);
    }
    
    public static void main(String[] args){
        Server server = new Server("localhost", 13337, 13338);
        Thread t = new Thread(server);
        t.start();
    }

    
    private Runnable createRunnableWithCallback(final Server s){
        Runnable r = new Runnable(){

            @Override
            public void run() {
                wss = new WebSocketServer(new InetSocketAddress("127.0.0.1", webport)) {

                    @Override
                    public void onOpen(WebSocket ws, ClientHandshake ch) {
                        System.out.println("server - web - onopen");
                        
                        try {
                            WebConnectionHandler wch = new WebConnectionHandler(s, ws);
                            clients.add(wch);
                        } catch (UnknownHostException ex) {
                            System.err.println("unknown host exception: " + ex);
                        }
                       
                    }

                    @Override
                    public void onClose(WebSocket ws, int i, String string, boolean bln) {
                         System.out.println("server - web - onclose");
                    }

                    @Override
                    public void onMessage(WebSocket ws, String string) {
                        // If its a custom handshake -> we need to initialize something... 
                        if(string.startsWith("{client:websock")){
                             System.out.println("Received custom handshake");
                             for(AbstractConnectionHandler ach : clients){
                                 if(ach instanceof WebConnectionHandler){
                                     if(((WebConnectionHandler) ach).initialized) continue;
                                     WebSocket achsocket = ((WebConnectionHandler) ach).client;
                                     if(achsocket.getRemoteSocketAddress().equals(ws.getRemoteSocketAddress())){
                                         ach.InitiateConnection();
                                         return;
                                     }
                                 }
                             }
                        }
                        // any other case, process the jsonrequest. 
                        else if(JSONUtilities.JSON.isJSON(string)) ServiceBroker.instance.offerRequest(string);
                         
                    }

                    @Override
                    public void onError(WebSocket ws, Exception excptn) {
                         System.out.println("server - web - onerror");
                    }
                };
                wss.start();
            }
            
        };
        return r;
    }
    private void runWebSocketServer(){
        Runnable websocketserver = createRunnableWithCallback(this);
        Thread websocketserverthread = new Thread(websocketserver);
        websocketserverthread.start();
    }
    
    
    @Override
    public void run() {
        active = true;
        runWebSocketServer();
        // FOR WAMP: USE THIS DATABASESERVICE
        // ServiceBroker.instance.addService(new WampMYSQLDatabaseService(this));
        
        // FOR MEMORY JDBC: USE THIS DATABASESERVICE
        ServiceBroker.instance.addService(new JDBCMemoryDatabaseService(this));
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
                    AbstractConnectionHandler connection = new ConnectionHandlerPrototype(this, client_connecting);
                    connection.InitiateConnection();
                    JSONObject obj = JSONUtilities.JSON.create("action", "sysout");
                    obj = JSONUtilities.JSON.addKeyValuePair("sysout", "guest connected from " + client_connecting.getInetAddress().getHostAddress(), obj);
                    synchronized(clients){
                        for(AbstractConnectionHandler ch : clients){
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

    public AbstractConnectionHandler getClient(String session){
        for(AbstractConnectionHandler ch : clients){
            if(ch.validSession(JSONUtilities.JSON.create("session", session))) return ch;
        }
        return null;
    }
    
    public String getNickname(String session){
        return getClient(session).getNickname();
    }
    
    public void sendAll(JSONObject packet){
        for(AbstractConnectionHandler ch : clients){
            ch.write(packet);
        }
    }
    
    public void sendOne(JSONObject packet, String session){
        for(AbstractConnectionHandler ch : clients){
            if(ch.validSession(JSONUtilities.JSON.create("session",session))) ch.write(packet);
        }
    }
    
    public void sendAllExcept(String session, JSONObject packet){
        for(AbstractConnectionHandler ch : clients){
            if(!ch.validSession(JSONUtilities.JSON.create("session",session))){
                ch.write(packet);
            }
        }
    }
    
    
    
    public void setConnectionType(ConnectionHandlerPrototype ch_proto, String type){
        switch(type){
            case("DesktopClient"):{
                clients.remove(ch_proto);
                ch_proto.cleanUp();
                ConnectionHandler ch = new ConnectionHandler(this, ch_proto.client, ch_proto.client_in);
                ch.InitiateConnection();
                clients.add(ch);
                break;
            }
        }
    }
}
