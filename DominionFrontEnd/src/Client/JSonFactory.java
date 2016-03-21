/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Client;

import javax.swing.JOptionPane;
import org.json.JSONObject;

/**
 *
 * @author admin
 */
public class JSonFactory {
    static JSonFactory JSON = new JSonFactory();
    private JSonFactory(){
        
    }
    
    public JSONObject protocol_chat(String author, String message){
        JSONObject obj = new JSONObject();

        obj.put("service_type", "chat");
        obj.put("author", author);
        obj.put("message", message);

        return obj;
    }
}
