/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Server;

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
}
