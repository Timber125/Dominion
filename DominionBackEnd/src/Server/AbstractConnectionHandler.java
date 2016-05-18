/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Server;

import java.util.Random;
import org.json.JSONObject;

/**
 *
 * @author admin
 */
public abstract class AbstractConnectionHandler{
    final protected static String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!#@";
    final protected static int tokensize = 13;
    /************************************************************/
    
    
    protected String hostName;
    protected String hostAddress;
    
    
    //public Socket client; -> type is connectionhandler-afhankelijk
    
    protected String my_session_token;
    protected String my_nickname = "guest";
    
    //private PrintWriter client_out; -> afhankelijk 
    //private ConnectionListener client_in; -> afhankelijk
    
    protected Server server;
    
    public AbstractConnectionHandler(Server server){
        this.server = server;
        my_session_token = createSessionToken();
    }
    public void changeNickname(String nickname){
        my_nickname = nickname;
    }
    public String getNickname(){
        return my_nickname;
    }
    
    public abstract void InitiateConnection();
    
    public abstract void write(String s);
    
    public abstract void write(JSONObject json);
     
    public abstract void cleanUp();
    
    public abstract int connectionState();
    
    protected String createSessionToken() {
        String s = "";
        // one in a billion RNG bug: session should be checked against server if it is already used for another client.
        while((s.equals(""))){
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
    public boolean validSession(String json){
        JSONObject obj = JSONUtilities.JSON.toJSON(json);
        String given_session = obj.getString("session");
        return (given_session.equals(my_session_token));
        // Short form for:
        // ==> IF equals THEN return true
        // ==> IF not equals THEN return false
    }
    protected String inject_client_information(String json_stringified) {
        JSONObject obj = JSONUtilities.JSON.toJSON(json_stringified);
        obj.put("author", my_nickname);
        return obj.toString();
    }
    protected String inject_client_information(JSONObject obj){
        obj.put("author", my_nickname);
        return obj.toString();
    }
    protected JSONObject inject_client_information_json(String json_stringified){
        JSONObject obj = JSONUtilities.JSON.toJSON(json_stringified);
        obj.put("author", my_nickname);
        return obj;
    }
    protected JSONObject inject_client_information_json(JSONObject obj){
        obj.put("author", my_nickname);
        return obj;
    }
    
}
