/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Server.Service;

import Server.ConnectionHandler;
import Server.JSONUtilities;
import Server.Server;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import org.json.JSONObject;

/**
 *
 * @author admin
 */
public class ChatService extends Service{
    public ChatService(Server server){
        super(server);
        known_service_types.add("chat");
    }

    @Override
    public void handleType(String type, JSONObject json) {
        if(type.equals("chat")) handle_chat(json);
        System.err.println("chatservice received a json");
    }
    
    public void handle_chat(JSONObject json){
        String author = json.getString("author");
        String message = json.getString("message");
        String writeback = author + ": " + message;
        JSONObject reply = JSONUtilities.JSON.create("sysout", writeback);
        for(ConnectionHandler ch : server.clients){
            ch.write(reply);
        }
    }

    
}
