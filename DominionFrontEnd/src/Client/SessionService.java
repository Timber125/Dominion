/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Client;

import java.util.ArrayList;
import org.json.JSONObject;

/**
 *
 * @author admin
 */
public class SessionService extends ServiceModel{

    // Na het initiaten van de connectie is deze service enkel nog verantwoordelijk 
    // voor het ontvangen van een "namechange bevestiging". 
    // Men voert ergens hier een namechange uit, de server kijkt of dit mag / kan, 
    // En zendt vervolgens een antwoord aan de sessionservice. 
    // De sessionservice zal dan ook in de client de naam definitief veranderen 
    // met manager.setNickName(nickname). 
    
    private ConnectionManager manager;
    
    private boolean handshakefinished = false;
    private String initname;
    public static SessionService create(String init_nickname){
        ArrayList<String> keywords = new ArrayList<>();
        keywords.add("session");
        SessionService serv = new SessionService(keywords);
        serv.initname = init_nickname;
        return serv;
    }
    
    public void setConnectionManager(ConnectionManager man){
        manager = man;
    }
    
    public SessionService(ArrayList<String> keywords) {
        super(keywords);
    }

    
    // Only happen on connect, this is part of the "handshake" protocol. 
    @Override
    public void handle(String json_stringified) {
        //action = session
        //session = sessiontoken
        if(!handshakefinished){
            if(manager == null) {
                System.err.println("Received a secret session token, but Sessionservice has no reference to the connection handler.");
            }else{
                System.err.println("Received a secret session token.");
                String token = extract_message(json_stringified);
                manager.setSessionToken(token);
                manager.write(createRenameRequestPackage(initname));
                handshakefinished = true;
            }
        }else{
            manager.setNickName(JSonFactory.JSON.toJSON(json_stringified).getString("newname"));
        }
        
    }
    
    private String extract_message(String json_stringified){
        return JSonFactory.JSON.toJSON(json_stringified).getString("session");
    }
    
    private JSONObject createRenameRequestPackage(String name){
        JSONObject obj = JSonFactory.JSON.create("service_type", "chat");
        JSonFactory.JSON.addKeyValuePair("author", "guest", obj);
        JSonFactory.JSON.addKeyValuePair("message", "!rename " + name, obj);
        return obj;
    }
}
