/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Client;

import Dominion.DynamicCard.Card;
import javax.swing.JOptionPane;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author admin
 */
public class JSonFactory {
    public static JSonFactory JSON = new JSonFactory();
    private JSonFactory(){
        
    }
    public JSONObject toJSON(String msg){
        JSONObject o = null;
        try{
            o = new JSONObject(msg);
        }catch(JSONException e){
            // Cast exception: msg is not a valid jsonobject.
            System.err.println("cast2json error: " + e);
            return null;
        }catch(NullPointerException ne){
            System.err.println("Nullpointer Json cast");
            System.err.println(ne);
        }
        return o;
    }
    
    public boolean isJSON(String msg){
        return (toJSON(msg) != null);
    }
    
    public String toString(JSONObject obj){
        return obj.toString();
    }
    
    public JSONObject addKeyValuePair(String key, String value, JSONObject json){
        json.put(key, value);
        return json;
    }
    
    public String addKeyValuePair(String key, String value, String json){
        JSONObject obj = toJSON(json);
        obj.put(key, value);
        return obj.toString();
    }
    
    public JSONObject create(String key, String value){
        JSONObject json = new JSONObject();
        json.put(key, value);
        return json;
    }
    public JSONObject protocol_chat(String author, String message){
        JSONObject obj = new JSONObject();

        obj.put("service_type", "chat");
        obj.put("author", author);
        obj.put("message", message);

        return obj;
    }
    public JSONObject protocol_dominion(String phase, String operation, Integer repeats){
        JSONObject obj = new JSONObject();
        obj.put("service_type", "dominion");
        obj.put("phase", phase);
        obj.put("operation", operation);
        obj.put("repeat", repeats.toString());
        return obj;
    }
    public JSONObject protocol_cardClicked(Card c){
        JSONObject obj = new JSONObject();
        obj.put("service_type", "dominion");
        obj.put("operation", "cardoffer");
        obj.put("cardname", c.getName());
        obj.put("id", c.getID().toString());
        return obj;
    }
    public JSONObject protocol_endPhase(){
        JSONObject obj = new JSONObject();
        obj.put("service_type", "dominion");
        obj.put("operation", "endphase");
        return obj;
    }
    public JSONObject protocol_cardBuy(String name){
        JSONObject obj = new JSONObject();
        obj.put("service_type", "dominion");
        obj.put("operation","buy");
        obj.put("cardname", name);
        return obj;
    }
    
    public JSONObject protocol_database(String function, String username, String password){
        JSONObject obj = new JSONObject();
        obj.put("service_type", "database");
        obj.put("function", function);
        obj.put("username", username);
        obj.put("password",password);
        return obj;
    }
    
    public JSONObject protocol_interaction_changenotification(String cardname, String identifier, Integer count, Integer parent){
        JSONObject obj = new JSONObject();
        obj.put("service_type", "dominion");
        obj.put("operation", "cardoffer");
        obj.put("cardname", cardname);
        obj.put("id", identifier);
        obj.put("count", count.toString());
        obj.put("parentindex", parent.toString());
        return obj;
    }
    public JSONObject protocol_interaction_confirmrequest(){
        JSONObject obj = new JSONObject();
        obj.put("service_type", "dominion");
        obj.put("operation", "interaction_confirm");
        return obj;
    }
    
    
    // protocol_database van emiel
}
