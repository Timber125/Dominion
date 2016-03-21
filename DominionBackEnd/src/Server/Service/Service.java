/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Server.Service;

import Server.JSONUtilities;
import Server.Server;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

/**

* 
* 
* 
* A Json object will be interpreted by "all" services if: 
*   the object has a parameter (key, value) == ("service_type", "all"). 
* 
* A Json object that makes use of a specific service e.g. chatservice, by using: 
*   (key, value) == ("service_type", "chat").
* 
* 
* 

 */
public abstract class Service {
    public List<String> known_service_types = new ArrayList<>();
    public abstract void handleType(String type, JSONObject json);
    protected Server server;
    public Service(Server server){
        // Default initialize "all". 
        known_service_types.add("all");
        this.server = server;
    }
    
    
}
