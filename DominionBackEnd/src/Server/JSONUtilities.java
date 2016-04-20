/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Server;

import java.util.ArrayList;
import java.util.Set;
import org.json.*;

/**
 *
 * @author admin
 */
public class JSONUtilities {
    public static JSONUtilities JSON = new JSONUtilities();
    private JSONUtilities(){
        
    }
    
    public JSONObject toJSON(String msg){
        JSONObject o = null;
        try{
            o = new JSONObject(msg);
        }catch(JSONException e){
            // Cast exception: msg is not a valid jsonobject.
            System.err.println("cast2json error: " + e);
            return null;
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
    
    public JSONObject make_client_print(String message){
        JSONObject json = create("action", "sysout");
        json = addKeyValuePair("sysout", message, json);
        return json;
    }
    public JSONObject make_client_gain(String cardname){
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "gain", json);
        json = addKeyValuePair("gain", cardname, json);
        return json;
    }
    public JSONObject make_client_lose(String cardname){
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "lose", json);
        json = addKeyValuePair("lose", cardname, json);
        return json;
    }
    public JSONObject make_client_turninfo(int actions, int purchases, int money){
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "turninfo", json);
        json = addKeyValuePair("actioncount", actions + "", json);
        json = addKeyValuePair("purchasecount", purchases + "", json);
        json = addKeyValuePair("money", money + "", json);
        return json;
    }
    public JSONObject make_client_hand_valid(Set<String> cards){
        String cardEnumeration = "";
        for(String s : cards){
            cardEnumeration += "," + s;
        }
        cardEnumeration = cardEnumeration.substring(1); // Kap eerste komma weg, gaat sneller dan laatste
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "control", json);
        json = addKeyValuePair("subject", "hand", json);
        json = addKeyValuePair("items", cardEnumeration, json);
        json = addKeyValuePair("control", "clickable", json);
        return json;
    }
    public JSONObject make_client_hand_invalid(){
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "control", json);
        json = addKeyValuePair("subject", "hand", json);
        json = addKeyValuePair("items", "all", json);
        json = addKeyValuePair("control", "unclickable", json);
        return json;
    }
    public JSONObject make_client_show_cardplayed(String cardname){
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "control", json);
        json = addKeyValuePair("subject", "table", json);
        json = addKeyValuePair("control", "display", json);
        json = addKeyValuePair("items", cardname, json);
        return json;
    }
    
}
