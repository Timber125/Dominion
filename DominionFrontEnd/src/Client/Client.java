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

/**
 *
 * @author admin
 */
public class Client {
    public static void main (String[] args){
        try {
            Socket s = new Socket("localhost", 13337);
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            while(!s.isClosed()){
                String st = in.readLine();
                if(st != null) {
                    System.out.println("Server says: " + st);
                    PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
                    //pw.println("Accepted your message!");
                    String reply = JSonFactory.JSON.protocol_message("Timber").toString();
                    pw.println(reply);
                    pw.println("Nonvalid");
                    System.out.println("client echoed reply.");
                }
                
            }
        } catch (IOException ex) {
            System.err.println("Connection failed ");
            System.err.println(ex);
        }
    }
}
