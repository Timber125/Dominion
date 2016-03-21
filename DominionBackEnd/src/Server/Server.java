/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Server;

import Server.Service.ChatService;
import Server.Service.ServiceBroker;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author admin
 */
public class Server implements Runnable{
    
    private boolean active = false;
    
    private ServerSocket serverSocket;
    private String ip;
    private int port;
    
    // Thread-race on clients; SYNCHRONIZE before access!
    public ArrayList<ConnectionHandler> clients;
    
    public Server(String ip, int port){
        this.ip = ip;
        this.port = port;
        clients = new ArrayList<>();
    }
    
    protected void notifyClose(){
        synchronized(clients){
            ArrayList<ConnectionHandler> updated_clients = new ArrayList<>();
            for(ConnectionHandler ch : clients){
                if(ch.client.isConnected()) updated_clients.add(ch);
            }
            this.clients = updated_clients;
        }
    }
    
    public void shutdown(){
        active = false;
        try {
            // Give myself a connection to catch shutdown signal
            Socket s = new Socket("localhost", 13337);
        } catch (IOException ex) {
            System.err.println("Server was down before shutdown signal!!!");
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
        ServiceBroker.instance.start();
        try {
            serverSocket = new ServerSocket(port);
            while(active){
                Socket client_connecting = serverSocket.accept();
                if(active){ // Catch shutdown signals, don't add them to clients.
                    ConnectionHandler connection = new ConnectionHandler(this, client_connecting);
                    connection.InitiateConnection();
                    synchronized(clients){
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

    
}
