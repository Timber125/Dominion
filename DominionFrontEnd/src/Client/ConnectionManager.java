/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 *
 * Encapsulates Connection for the client.
 */
public class ConnectionManager {
    
    public Connection server;
    public Map<Integer, ServiceModel> models = new HashMap<>();
    public Map<String, List<Integer>> keywords = new HashMap<>();
    
    public String address;
    public int port;
    public ConnectionManager(String address, int port){
        this.address = address;
        this.port = port;
    }
    
    public synchronized void registerModel(ServiceModel model){
        int id = models.size()+1;
        for(int i = 0; i < models.size(); i++){
            if(models.get(i) == null){
                id = i;
                break;
            }
        }
        models.put(id, model);
        for(String key : model.keywords){
            if(keywords.containsKey(key)){
                keywords.get(key).add(id);
            }else{
                ArrayList<Integer> l = new ArrayList<>();
                l.add(id);
                keywords.put(key, l);
            }
        }
        
    }
    
    public void handle(String json){
        String action = JSonFactory.JSON.toJSON(json).getString("action");
        if(keywords.containsKey(action)){
            for(Integer id : keywords.get(action)){
                ServiceModel m = models.get(id);
                m.handle(json);
            }
        }else{
            System.err.println("Unknown action: [" + action + "].");
        }
    }
    
    public void write(String json){
        server.write(json);
    }
    public void write(JSONObject json){
        server.write(json.toString());
    }

    public boolean init_server() {
        try {
            server = new Connection(address, port);
        } catch (IOException ex) {
            System.err.println(ex);
        }
        if (server == null) return false;
        server.addListener(this);
        Thread connectionthread = new Thread(server);
        connectionthread.start();
        return true;
    }
    
}
