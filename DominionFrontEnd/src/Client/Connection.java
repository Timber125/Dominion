/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import org.json.JSONObject;

/**
 *
 * @author admin
 */
public class Connection implements Runnable{
    public Socket s;
    private BufferedReader in;
    private PrintWriter out;
    private ConnectionManager manager;
    public Connection(String host, int port) throws IOException{
        s = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        out = new PrintWriter(s.getOutputStream(), true);
    }

    @Override
    public void run() {
        while(!s.isClosed()){
            try {
                String st = in.readLine();
                if(st != null) {
                    JSONObject json = new JSONObject(st);
                    System.err.println("Raw server message: " + st);
                    manager.handle(st);
                    //String sysout = json.getString("sysout");
                    //System.out.println(sysout);
                    //PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
                    //pw.println("Accepted your message!");
                    //String reply = JSonFactory.JSON.protocol_chat("Timber", "Hello everyone!").toString();
                    //pw.println(reply);
                    //System.err.println("Sent basic chat loop echo reply");
                }
            } catch (IOException ex) {
                break;
            }
                
        }
        System.err.println("Connection closed.");
    }

    public void write(String json_stringified){
        out.println(json_stringified);
    }
    
    public void addListener(ConnectionManager manager) {
        this.manager = manager;
    }

    public void removeListener(ConnectionManager manager) {
        this.manager = null;
    }
}
