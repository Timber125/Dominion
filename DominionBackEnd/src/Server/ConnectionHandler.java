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
public class ConnectionHandler implements Runnable, InvalidationListener{

    /* These variables are not related to ConnectionHandler objects!
     There is only 1 of each per "program", and it is only accessible for each ConnectionHandler object. 
     You create variables like this (1 per program, accessible only for these objects) by using the "private static" keyword.
     If you want it to be available throughout the whole program, make it "public static". This can be avoided in most cases by good design. 
     If you ever need a public static variable, consider every other possible way, because public static is ugly. 
     As you probably know, adding keyword "final" makes it a constant. 
    */
    
    private static Map<String, ConnectionHandler> sessions = new HashMap<>();
    final private static String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!#@";
    final private static int tokensize = 13;
    /************************************************************/
    
    
    private String hostName;
    private String hostAddress;
    public Socket client;
    
    protected String my_session_token;
    protected String my_nickname = "guest";
    
    private PrintWriter client_out;
    private ConnectionListener client_in;
    
    private Server server;
    
    
    
    public ConnectionHandler(Server server, Socket client){
        this.server = server;
        my_session_token = createSessionToken();
        initialize(client);
    }
    
    // Be careful, should only be accessed from within communication protocols
    public void changeNickname(String nickname){
        my_nickname = nickname;
    }
    
    public String getNickname(){
        return my_nickname;
    }
    
    public void InitiateConnection(){
        Thread t = new Thread(this);
        t.start();
    }
    
    public void write(String s){
        client_out.println(s);
    }
    
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
            startListener(new BufferedReader(new InputStreamReader(client.getInputStream())));
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
        sessions.remove(my_session_token);
    }
    
    private void initialize(Socket client) {
        this.client = client;
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

    private String createSessionToken() {
        String s = "";
        while((s.equals("")) || (sessions.containsKey(s))){
            s = "";
            Random r = new Random(System.currentTimeMillis()); // Create a random seed 
            for(int i = 0; i < tokensize; i++){
                int b = r.nextInt(alphabet.length());
                char c = alphabet.charAt(b);
                s += c;
            }
        }
        return s;
    }
    public boolean validSession(JSONObject json){
        return (json.getString("session").equals(my_session_token));
    }
    // Extract crafted json-communication before it is sent to the services!
    public boolean validSession(String json){
        JSONObject obj = JSONUtilities.JSON.toJSON(json);
        String given_session = obj.getString("session");
        return (given_session.equals(my_session_token));
        // Short form for:
        // ==> IF equals THEN return true
        // ==> IF not equals THEN return false
    }

    
        // remove potentially crafted authors
        // Add the valid author name registered on server
        // Send through
    
    private String inject_client_information(String json_stringified) {
        JSONObject obj = JSONUtilities.JSON.toJSON(json_stringified);
        obj.put("author", my_nickname);
        return obj.toString();
    }
    
    private String inject_client_information(JSONObject obj){
        obj.put("author", my_nickname);
        return obj.toString();
    }
    
    private JSONObject inject_client_information_json(String json_stringified){
        JSONObject obj = JSONUtilities.JSON.toJSON(json_stringified);
        obj.put("author", my_nickname);
        return obj;
    }
    private JSONObject inject_client_information_json(JSONObject obj){
        obj.put("author", my_nickname);
        return obj;
    }

    
}
