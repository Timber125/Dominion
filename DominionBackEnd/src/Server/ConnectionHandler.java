/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Server;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.InvalidationListener;

/**
 *
 * @author admin
 */
public class ConnectionHandler implements Runnable, InvalidationListener{

    private String hostName;
    private String hostAddress;
    public Socket client;
    
    private PrintWriter client_out;
    private ConnectionListener client_in;
    
    private Server server;
    
    public ConnectionHandler(Server server, Socket client){
        initialize(client);
    }
    
    public void InitiateConnection(){
        Thread t = new Thread(this);
        t.start();
    }
    
    @Override
    public void run() {
        
        try {
            startListener(new BufferedReader(new InputStreamReader(client.getInputStream())));
            startWriter(new PrintWriter(client.getOutputStream()));
            
            
        } catch (IOException ex) {
            System.err.println("Client out could not be initialized");
        }
        
    }

    public void cleanUp() throws IOException{
        client_out.close();
        client_in.removeListener(this);
        client.close();
        server.notifyClose();
    }
    
    private void initialize(Socket client) {
        this.client = client;
        InetAddress clientAdress = client.getInetAddress();
        hostName = clientAdress.getHostName();
        hostAddress = clientAdress.getHostAddress();
        
        System.out.println("Client accepted: ");
        System.out.println("Hostname: " + hostName);
        System.out.println("Hostaddress: " + hostAddress);
        System.out.println("Connected: " + client.isConnected());
    }

    private void startWriter(PrintWriter out){
        client_out = new PrintWriter(out,true);
        client_out.println("Connection accepted.");
    }
    
    private void startListener(BufferedReader in){
        //client_in = new ConnectionListener(in);
        client_in = new JSonListener(in);
        client_in.addListener(this);
        Thread listen = new Thread(client_in);
        listen.start();
    }

    @Override
    public void invalidated(javafx.beans.Observable o) {
         // The retreiving of objects in buffer and storing them into the arraylist temporarily is not efficient
        // Remove later, but keep for now -> readability
        ArrayList<String> messagelist = new ArrayList<>();
        while(!client_in.buffer.isEmpty()) messagelist.add(client_in.buffer.poll());
        for(String message : messagelist){
            System.out.println("Client message: ");
            System.out.println("================");
            System.out.println(message);
            System.out.println("================");
        }
        
    }
    
}
