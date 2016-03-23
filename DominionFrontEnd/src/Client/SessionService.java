/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Client;

import java.util.ArrayList;

/**
 *
 * @author admin
 */
public class SessionService extends ServiceModel{

    private ConnectionManager manager;
    
    public static SessionService create(){
        ArrayList<String> keywords = new ArrayList<>();
        keywords.add("session");
        return new SessionService(keywords);
    }
    
    public void setConnectionManager(ConnectionManager man){
        manager = man;
    }
    
    public SessionService(ArrayList<String> keywords) {
        super(keywords);
    }

    @Override
    public void handle(String json_stringified) {
        if(manager == null) {
            System.err.println("Received a secret session token, but Sessionservice has no reference to the connection handler.");
        }else{
            System.err.println("Received a secret session token.");
            String token = extract_message(json_stringified);
            manager.setSessionToken(token);
        }
        
    }
    
    private String extract_message(String json_stringified){
        return JSonFactory.JSON.toJSON(json_stringified).getString("session");
    }
}
