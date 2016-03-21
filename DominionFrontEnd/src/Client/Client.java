/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Client;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.stage.Stage;
import org.json.JSONObject;

/**
 *
 * @author admin
 */
public class Client extends Application{
    public static void main (String[] args){
        try {
            Socket s = new Socket("192.168.0.241", 13337);
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            while(!s.isClosed()){
                String st = in.readLine();
                if(st != null) {
                    JSONObject json = new JSONObject(st);
                    System.err.println("Raw server message: " + st);
                    String sysout = json.getString("sysout");
                    System.out.println(sysout);
                    PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
                    //pw.println("Accepted your message!");
                    String reply = JSonFactory.JSON.protocol_chat("Timber", "Hello everyone!").toString();
                    pw.println(reply);
                    System.err.println("Sent basic chat loop echo reply");
                }
                
            }
        } catch (IOException ex) {
            System.err.println("Connection failed ");
            System.err.println(ex);
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        try {
            Socket s = new Socket("192.168.0.241", 13337);
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            while(!s.isClosed()){
                String st = in.readLine();
                if(st != null) {
                    JSONObject json = new JSONObject(st);
                    System.err.println("Raw server message: " + st);
                    String sysout = json.getString("sysout");
                    System.out.println(sysout);
                    PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
                    //pw.println("Accepted your message!");
                    String reply = JSonFactory.JSON.protocol_chat("Timber", "Hello everyone!").toString();
                    pw.println(reply);
                }
                
            }
        } catch (IOException ex) {
            System.err.println("Connection failed ");
            System.err.println(ex);
        }
    }
}
