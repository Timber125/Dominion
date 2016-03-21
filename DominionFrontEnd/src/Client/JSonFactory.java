/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Client;

import org.json.JSONObject;

/**
 *
 * @author admin
 */
public class JSonFactory {
    static JSonFactory JSON = new JSonFactory();
    private JSonFactory(){
        
    }
    
    public JSONObject protocol_message(String message){
        JSONObject obj = new JSONObject();

        obj.put("type", "message");
        obj.put("content", message);
        obj.put("time_created", System.currentTimeMillis());

        return obj;
    }
}
